/*
 * Copyright the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.citrusframework.simulator.service;

import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.SetJoin;
import lombok.extern.slf4j.Slf4j;
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.MessageHeader;
import org.citrusframework.simulator.model.MessageHeader_;
import org.citrusframework.simulator.model.Message_;
import org.citrusframework.simulator.model.ScenarioAction_;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioExecution_;
import org.citrusframework.simulator.model.ScenarioParameter_;
import org.citrusframework.simulator.model.TestResult_;
import org.citrusframework.simulator.repository.ScenarioExecutionRepository;
import org.citrusframework.simulator.service.criteria.ScenarioExecutionCriteria;
import org.citrusframework.simulator.service.filter.Filter;
import org.citrusframework.simulator.service.filter.LongFilter;
import org.citrusframework.simulator.service.filter.RangeFilter;
import org.citrusframework.simulator.service.filter.StringFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

import static jakarta.persistence.criteria.JoinType.LEFT;
import static java.lang.Boolean.TRUE;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.regex.Pattern.compile;
import static org.citrusframework.simulator.service.CriteriaQueryUtils.selectAll;
import static org.citrusframework.simulator.service.CriteriaQueryUtils.selectAllIds;
import static org.citrusframework.simulator.service.ScenarioExecutionQueryService.MessageHeaderFilter.fromFilterPattern;
import static org.citrusframework.simulator.service.ScenarioExecutionQueryService.Operator.parseOperator;
import static org.citrusframework.simulator.service.ScenarioExecutionQueryService.ResultDetailsConfiguration.withAllDetails;
import static org.citrusframework.util.StringUtils.isEmpty;

/**
 * Service for executing complex queries for {@link ScenarioExecution} entities in the database.
 * The main input is a {@link ScenarioExecutionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ScenarioExecution} or a {@link Page} of {@link ScenarioExecution} which fulfills the criteria.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ScenarioExecutionQueryService extends QueryService<ScenarioExecution> {

    private static final Pattern HEADER_FILTER_PATTERN = compile("^\\w?(([\\w-]+)[=~]?[ \\w,/.:()-]*|([\\w-]+)[<>]=?\\d+)$");
    public static final String MULTIPLE_FILTER_EXPRESSION_SEPARATOR = "; |;";

    private final EntityManager entityManager;
    private final ScenarioExecutionRepository scenarioExecutionRepository;

    @VisibleForTesting
    static boolean isValidFilterPattern(String filterPattern) {
        return HEADER_FILTER_PATTERN.matcher(filterPattern).matches();
    }

    private static Specification<ScenarioExecution> withIdIn(List<Long> executionIds) {
        return (root, query, builder) -> {
            var in = builder.in(root.get(ScenarioExecution_.executionId));
            executionIds.forEach(in::value);
            return in;
        };
    }

    private static Specification<ScenarioExecution> withResultDetailsConfiguration(ResultDetailsConfiguration resultDetailsConfiguration) {
        return (root, query, cb) -> {
            assert query != null;

            // Prevent duplicate joins in count queries
            if (query.getResultType() == Long.class || query.getResultType() == long.class) {
                return null;
            }

            root.fetch(ScenarioExecution_.testResult, LEFT);

            if (resultDetailsConfiguration.includeParameters()) {
                root.fetch(ScenarioExecution_.scenarioParameters, LEFT);
            }

            if (resultDetailsConfiguration.includeActions()) {
                root.fetch(ScenarioExecution_.scenarioActions, LEFT);
            }

            if (resultDetailsConfiguration.includeMessages()
                || resultDetailsConfiguration.includeMessageHeaders()) {
                var messageFetch = root.fetch(ScenarioExecution_.scenarioMessages, LEFT);
                if (resultDetailsConfiguration.includeMessageHeaders()) {
                    messageFetch.fetch(Message_.headers, LEFT);
                }
            }

            // Return no additional where clause
            return null;
        };
    }

    private static <X> Specification<ScenarioExecution> buildSpecification(@Nonnull Filter<X> filter, Path<X> path) {
        return (root, query, criteriaBuilder) -> {
            if (filter.getEquals() != null) {
                return criteriaBuilder.equal(path, filter.getEquals());
            } else if (filter.getNotEquals() != null) {
                return criteriaBuilder.notEqual(path, filter.getNotEquals());
            } else if (filter.getIn() != null) {
                return path.in(filter.getIn());
            } else if (filter.getNotIn() != null) {
                return criteriaBuilder.not(path.in(filter.getNotIn()));
            } else if (filter.getSpecified() != null) {
                return TRUE.equals(filter.getSpecified()) ? criteriaBuilder.isNotNull(path) : criteriaBuilder.isNull(path);
            } else if (filter instanceof StringFilter stringFilter) {
                if (nonNull(stringFilter.getEqualsIgnoreCase())){
                    return criteriaBuilder.equal(criteriaBuilder.upper(path.as(String.class)), stringFilter.getEqualsIgnoreCase().toUpperCase());
                } else if (nonNull(stringFilter.getContains())) {
                    return criteriaBuilder.like(criteriaBuilder.upper(path.as(String.class)), wrapLikeQuery(stringFilter.getContains().toUpperCase()));
                } else if (nonNull(stringFilter.getDoesNotContain())) {
                    return criteriaBuilder.like(criteriaBuilder.upper(path.as(String.class)), wrapLikeQuery(stringFilter.getDoesNotContain().toUpperCase()));
                }
            } else if (filter instanceof RangeFilter rangeFilter) {
                var comparableExpression = path.as(Comparable.class);
                if (nonNull(rangeFilter.getGreaterThan())) {
                    return criteriaBuilder.greaterThan(comparableExpression, rangeFilter.getGreaterThan());
                } else if (nonNull(rangeFilter.getGreaterThanOrEqual())) {
                    return criteriaBuilder.greaterThanOrEqualTo(comparableExpression, rangeFilter.getGreaterThanOrEqual());
                } else if (nonNull(rangeFilter.getLessThan())) {
                    return criteriaBuilder.lessThan(comparableExpression, rangeFilter.getLessThan());
                } else if (nonNull(rangeFilter.getLessThanOrEqual())) {
                    return criteriaBuilder.lessThanOrEqualTo(comparableExpression, rangeFilter.getLessThanOrEqual());
                }
            }

            return null;
        };
    }

    public ScenarioExecutionQueryService(EntityManager entityManager, ScenarioExecutionRepository scenarioExecutionRepository) {
        this.entityManager = entityManager;
        this.scenarioExecutionRepository = scenarioExecutionRepository;
    }

    /**
     * Return a {@link List} of {@link ScenarioExecution} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ScenarioExecution> findByCriteria(ScenarioExecutionCriteria criteria) {
        logger.debug("find by criteria : {}", criteria);
        final Specification<ScenarioExecution> specification = createSpecification(criteria);
        return scenarioExecutionRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link ScenarioExecution} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ScenarioExecution> findByCriteria(ScenarioExecutionCriteria criteria, Pageable page) {
        return findByCriteria(criteria, page, withAllDetails());
    }

    /**
     * Return a {@link Page} of {@link ScenarioExecution} which matches the criteria from the database.
     *
     * @param criteria                   The object which holds all the filters, which the entities should match.
     * @param page                       The page, which should be returned.
     * @param resultDetailsConfiguration Fetch-configuration of relationships
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ScenarioExecution> findByCriteria(ScenarioExecutionCriteria criteria, Pageable page, ResultDetailsConfiguration resultDetailsConfiguration) {
        logger.debug("find by criteria : {}, page: {}", criteria, page);

        var specification = createSpecification(criteria);
        var scenarioExecutionIds = selectAllIds(ScenarioExecution.class, ScenarioExecution_.executionId, specification, page, entityManager);
        if (scenarioExecutionIds.isEmpty()) {
            return Page.empty(page);
        }

        var fetchSpec = withIdIn(scenarioExecutionIds)
            .and(withResultDetailsConfiguration(resultDetailsConfiguration));

        var scenarioExecutions = selectAll(
            ScenarioExecution.class,
            fetchSpec,
            page,
            entityManager
        );

        return new PageImpl<>(scenarioExecutions, page, scenarioExecutionRepository.count(specification));
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ScenarioExecutionCriteria criteria) {
        logger.debug("count by criteria : {}", criteria);
        final Specification<ScenarioExecution> specification = createSpecification(criteria);
        return scenarioExecutionRepository.count(specification);
    }

    /**
     * Function to convert {@link ScenarioExecutionCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ScenarioExecution> createSpecification(@Nullable ScenarioExecutionCriteria criteria) {
        Specification<ScenarioExecution> specification = Specification.where(null);
        if (isNull(criteria)) {
            return specification;
        }

        // Apply distinct filter first
        if (nonNull(criteria.getDistinct())) {
            specification = specification.and(distinct(criteria.getDistinct()));
        }

        // Add basic range and string filters
        if (nonNull(criteria.getExecutionId())) {
            specification = specification.and(buildRangeSpecification(criteria.getExecutionId(), ScenarioExecution_.executionId));
        }

        if (nonNull(criteria.getStartDate())) {
            specification = specification.and(buildRangeSpecification(criteria.getStartDate(), ScenarioExecution_.startDate));
        }

        if (nonNull(criteria.getEndDate())) {
            specification = specification.and(buildRangeSpecification(criteria.getEndDate(), ScenarioExecution_.endDate));
        }

        if (nonNull(criteria.getScenarioName())) {
            specification = specification.and(buildStringSpecification(criteria.getScenarioName(), ScenarioExecution_.scenarioName));
        }

        // Join testResult for status filter
        specification = specification.and((root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.conjunction();
            var testResultJoin = root.join(ScenarioExecution_.testResult, LEFT);

            if (nonNull(criteria.getStatus())) {
                predicate = criteriaBuilder.and(
                    predicate, buildSpecification(criteria.getStatus(), r -> testResultJoin.get(TestResult_.status)).toPredicate(root, query, criteriaBuilder)
                );
            }

            return predicate;
        });

        // Grouped join for scenarioMessages-related filters
        if (shouldJoinMessagesOrHeaders(criteria)) {
            specification = specification.and((root, query, criteriaBuilder) -> {
                var predicate = criteriaBuilder.conjunction();
                var messageJoin = root.join(ScenarioExecution_.scenarioMessages, LEFT);

                if (nonNull(criteria.getScenarioMessagesId())) {
                    predicate = criteriaBuilder.and(
                        predicate, buildSpecification(criteria.getScenarioMessagesId(), messageJoin.get(Message_.messageId)).toPredicate(root, query, criteriaBuilder)
                    );
                }

                if (nonNull(criteria.getScenarioMessagesDirection())) {
                    predicate = criteriaBuilder.and(
                        predicate, buildSpecification(criteria.getScenarioMessagesDirection(), messageJoin.get(Message_.direction)).toPredicate(root, query, criteriaBuilder)
                    );
                }

                if (nonNull(criteria.getScenarioMessagesPayload())) {
                    predicate = criteriaBuilder.and(
                        predicate, buildSpecification(criteria.getScenarioMessagesPayload(), messageJoin.get(Message_.payload)).toPredicate(root, query, criteriaBuilder)
                    );
                }

                // Message headers filter
                if (nonNull(criteria.getHeaders())) {
                    var messageHeaderSpecifications = createMessageHeaderSpecifications(criteria.getHeaders(), messageJoin);
                    for (var messageHeaderSpecification : messageHeaderSpecifications) {
                        predicate = criteriaBuilder.and(
                            predicate, messageHeaderSpecification.toPredicate(root, query, criteriaBuilder)
                        );
                    }
                }

                return predicate;
            });
        }

        // Single join for scenarioActions
        if (nonNull(criteria.getScenarioActionsId())) {
            specification = specification.and((root, query, criteriaBuilder) -> {
                var predicate = criteriaBuilder.conjunction();
                var scenarioExecutionJoin = root.join(ScenarioExecution_.scenarioActions, LEFT);

                if (nonNull(criteria.getScenarioActionsId())) {
                    predicate = criteriaBuilder.and(
                        predicate,
                        buildSpecification(criteria.getScenarioActionsId(), r -> scenarioExecutionJoin.get(ScenarioAction_.actionId)).toPredicate(root, query, criteriaBuilder)
                    );
                }

                return predicate;
            });
        }

        // Single join for scenarioParameters
        if (nonNull(criteria.getScenarioParametersId())) {
            specification = specification.and((root, query, criteriaBuilder) -> {
                var predicate = criteriaBuilder.conjunction();
                var scenarioParameterJoin = root.join(ScenarioExecution_.scenarioParameters, LEFT);

                if (nonNull(criteria.getScenarioParametersId())) {
                    predicate = criteriaBuilder.and(
                        predicate,
                        buildSpecification(criteria.getScenarioParametersId(), r -> scenarioParameterJoin.get(ScenarioParameter_.parameterId)).toPredicate(root, query, criteriaBuilder)
                    );
                }

                return predicate;
            });
        }

        return specification;
    }

    private static boolean shouldJoinMessagesOrHeaders(ScenarioExecutionCriteria criteria) {
        return nonNull(criteria.getScenarioMessagesId())
            || nonNull(criteria.getScenarioMessagesDirection())
            || nonNull(criteria.getScenarioMessagesPayload())
            || nonNull(criteria.getHeaders());
    }

    private List<Specification<ScenarioExecution>> createMessageHeaderSpecifications(@Nullable String headers, SetJoin<ScenarioExecution, Message> messageJoin) {
        List<Specification<ScenarioExecution>> specifications = new ArrayList<>();
        if (isEmpty(headers)) {
            return specifications;
        }

        var filterPatterns = headers.split(MULTIPLE_FILTER_EXPRESSION_SEPARATOR);
        for (var filterPattern : filterPatterns) {
            var messageHeaderJoin = messageJoin.join(Message_.headers, LEFT);
            newMessageHeaderFilterFromFilterPattern(filterPattern)
                .map(filter -> createSpecificationFromMessageHeaderFilter(filter, messageHeaderJoin))
                .ifPresent(specifications::add);
        }

        return specifications;
    }

    private Optional<MessageHeaderFilter> newMessageHeaderFilterFromFilterPattern(String filterPattern) {
        try {
            return Optional.of(fromFilterPattern(filterPattern));
        } catch (InvalidPatternException e) {
            logger.warn("Ignoring invalid filter pattern: {}", filterPattern, e);
            return Optional.empty();
        }
    }

    private Specification<ScenarioExecution> createSpecificationFromMessageHeaderFilter(MessageHeaderFilter messageHeaderFilter, SetJoin<Message, MessageHeader> messageHeaderJoin) {
        if (messageHeaderFilter.isValueFilterOnly()) {
            return buildSpecification(
                new StringFilter().setContains(messageHeaderFilter.value),
                r -> messageHeaderJoin.get(MessageHeader_.value));
        }

        Specification<ScenarioExecution> messageHeaderKeyEqualsSpecification = buildSpecification(
            new StringFilter().setEqualsIgnoreCase(messageHeaderFilter.key),
            r -> messageHeaderJoin.get(MessageHeader_.name));

        var messageHeaderValueSpecification = switch (messageHeaderFilter.operator) {
            case EQUALS, CONTAINS ->
                buildMessageHeaderValueSpecification((StringFilter) messageHeaderFilter.operator.filter.apply(messageHeaderFilter.value), messageHeaderJoin);
            case GREATER_THAN, GREATER_THAN_OR_EQUAL_TO, LESS_THAN, LESS_THAN_OR_EQUAL_TO ->
                buildMessageHeaderValueSpecification((LongFilter) messageHeaderFilter.operator.filter.apply(messageHeaderFilter.value), messageHeaderJoin);
        };

        return messageHeaderKeyEqualsSpecification.and(messageHeaderValueSpecification);
    }

    private Specification<ScenarioExecution> buildMessageHeaderValueSpecification(StringFilter stringFilter, SetJoin<Message, MessageHeader> messageHeaderJoin) {
        return buildSpecification(stringFilter, r -> messageHeaderJoin.get(MessageHeader_.value));
    }

    private Specification<ScenarioExecution> buildMessageHeaderValueSpecification(LongFilter longFilter, SetJoin<Message, MessageHeader> messageHeaderJoin) {
        return buildSpecification(longFilter, r -> messageHeaderJoin.get(MessageHeader_.value).as(Long.class));
    }

    enum Operator {

        EQUALS("=", value -> new StringFilter().setEqualsIgnoreCase(value)),
        CONTAINS("~", value -> new StringFilter().setContains(value)),
        GREATER_THAN(">", value -> new LongFilter().setGreaterThan(parseLong(value))),
        GREATER_THAN_OR_EQUAL_TO(">=", value -> new LongFilter().setGreaterThanOrEqual(parseLong(value))),
        LESS_THAN("<", value -> new LongFilter().setLessThan(parseLong(value))),
        LESS_THAN_OR_EQUAL_TO("<=", value -> new LongFilter().setLessThanOrEqual(parseLong(value)));

        private final String stringOperator;
        private final Function<String, Filter<?>> filter;

        Operator(String stringOperator, Function<String, Filter<?>> filter) {
            this.stringOperator = stringOperator;
            this.filter = filter;
        }

        static Operator parseOperator(@Nonnull String operator) {
            return stream(values())
                .filter(value -> value.stringOperator.equals(operator.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(format("Invalid operator '%s'!", operator)));
        }

        public StringFilter getOperator() {
            return null;
        }
    }

    record MessageHeaderFilter(String key, Operator operator, String value) {

        static MessageHeaderFilter fromFilterPattern(String filterPattern) throws InvalidPatternException {
            var parts = filterPattern.split("(?=[=~]|[<>]=?)|(?<=[=~]|[<>]=?)");
            if (isEmpty(filterPattern)
                || !isValidFilterPattern(filterPattern)) {
                throw new InvalidPatternException(filterPattern);
            }

            if (parts.length == 1) {
                return new MessageHeaderFilter(null, null, parts[0]);
            } else if (parts.length == 2) {
                return new MessageHeaderFilter(parts[0], parseOperator(parts[1]), "");
            } else if (parts.length == 4) {
                return new MessageHeaderFilter(parts[0], parseOperator(parts[1] + parts[2]), parts[3]);
            } else {
                return new MessageHeaderFilter(parts[0], parseOperator(parts[1]), parts[2]);
            }
        }

        public boolean isValueFilterOnly() {
            return isNull(key) && isNull(operator);
        }
    }

    static class InvalidPatternException extends Exception {

        public InvalidPatternException(String filterPattern) {
            super(format("The header filter pattern '%s' does not comply with the regex '%s'!", filterPattern, HEADER_FILTER_PATTERN.pattern()));
        }
    }

    public record ResultDetailsConfiguration(
        boolean includeActions,
        boolean includeMessages,
        boolean includeMessageHeaders,
        boolean includeParameters
    ) {

        static ResultDetailsConfiguration withAllDetails() {
            return new ResultDetailsConfiguration(true, true, true, true);
        }
    }
}
