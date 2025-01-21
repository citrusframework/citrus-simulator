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
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
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

import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.regex.Pattern.compile;
import static org.citrusframework.simulator.service.CriteriaQueryUtils.newSelectIdBySpecificationQuery;
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

    public ScenarioExecutionQueryService(EntityManager entityManager, ScenarioExecutionRepository scenarioExecutionRepository) {
        this.entityManager = entityManager;
        this.scenarioExecutionRepository = scenarioExecutionRepository;
    }

    @VisibleForTesting
    static boolean isValidFilterPattern(String filterPattern) {
        return HEADER_FILTER_PATTERN.matcher(filterPattern).matches();
    }

    private static Specification<ScenarioExecution> withResultDetailsConfiguration(ResultDetailsConfiguration config) {
        return (root, query, cb) -> {
            assert query != null;

            // Prevent duplicate joins in count queries
            if (query.getResultType() == Long.class || query.getResultType() == long.class) {
                return null;
            }

            root.fetch(ScenarioExecution_.testResult, JoinType.LEFT);

            if (config.includeParameters()) {
                root.fetch(ScenarioExecution_.scenarioParameters, JoinType.LEFT);
            }

            if (config.includeActions()) {
                root.fetch(ScenarioExecution_.scenarioActions, JoinType.LEFT);
            }

            if (config.includeMessages() || config.includeMessageHeaders()) {
                var messagesJoin = root.fetch(ScenarioExecution_.scenarioMessages, JoinType.LEFT);

                if (config.includeMessageHeaders()) {
                    messagesJoin.fetch(Message_.headers, JoinType.LEFT);
                }
            }

            // Return no additional where clause
            return null;
        };
    }

    private static Specification<ScenarioExecution> withIds(List<Long> executionIds) {
        return (root, query, builder) -> {
            var in = builder.in(root.get(ScenarioExecution_.executionId));
            executionIds.forEach(in::value);
            return in;
        };
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
        var scenarioExecutionIds = newSelectIdBySpecificationQuery(
            ScenarioExecution.class,
            ScenarioExecution_.executionId,
            specification,
            page,
            entityManager
        )
            .getResultList();

        if (scenarioExecutionIds.isEmpty()) {
            return Page.empty(page);
        }

        var fetchSpec = withIds(scenarioExecutionIds)
            .and(withResultDetailsConfiguration(resultDetailsConfiguration));

        var scenarioExecutions = scenarioExecutionRepository.findAll(fetchSpec, page.getSort());

        return new PageImpl<>(
            scenarioExecutions,
            page,
            scenarioExecutionRepository.count(specification)
        );
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
    protected Specification<ScenarioExecution> createSpecification(ScenarioExecutionCriteria criteria) {
        Specification<ScenarioExecution> specification = Specification.where(null);
        if (nonNull(criteria)) {
            // This has to be called first, because the distinct method returns null
            if (nonNull(criteria.getDistinct())) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
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
            if (criteria.getStatus() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getStatus(),
                            root -> root.join(ScenarioExecution_.testResult, JoinType.LEFT).get(TestResult_.status)
                        )
                    );
            }
            if (nonNull(criteria.getScenarioActionsId())) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getScenarioActionsId(),
                            root -> root.join(ScenarioExecution_.scenarioActions, JoinType.LEFT).get(ScenarioAction_.actionId)
                        )
                    );
            }
            if (nonNull(criteria.getScenarioMessagesId())) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getScenarioMessagesId(),
                            root -> root.join(ScenarioExecution_.scenarioMessages, JoinType.LEFT).get(Message_.messageId)
                        )
                    );
            }
            if (nonNull(criteria.getScenarioMessagesDirection())) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getScenarioMessagesDirection(),
                            root -> root.join(ScenarioExecution_.scenarioMessages, JoinType.LEFT).get(Message_.direction)
                        )
                    );
            }
            if (nonNull(criteria.getScenarioParametersId())) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getScenarioParametersId(),
                            root -> root.join(ScenarioExecution_.scenarioParameters, JoinType.LEFT).get(ScenarioParameter_.parameterId)
                        )
                    );
            }
            if (nonNull(criteria.getHeaders())) {
                var messageHeaderSpecifications = createMessageHeaderSpecifications(criteria.getHeaders());
                for (var messageHeaderSpecification : messageHeaderSpecifications) {
                    specification = specification.and(messageHeaderSpecification);
                }
            }
            if (nonNull(criteria.getScenarioMessagesPayload())) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getScenarioMessagesPayload(),
                            root -> root.join(ScenarioExecution_.scenarioMessages, JoinType.LEFT).get(Message_.payload)
                        )
                    );
            }
        }
        return specification;
    }

    private List<Specification<ScenarioExecution>> createMessageHeaderSpecifications(String headers) {
        List<Specification<ScenarioExecution>> specifications = new ArrayList<>();

        var filterPatterns = headers.split(MULTIPLE_FILTER_EXPRESSION_SEPARATOR);
        for (var filterPattern : filterPatterns) {
            newMessageHeaderFilterFromFilterPattern(filterPattern)
                .map(this::createSpecificationFromMessageHeaderFilter)
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

    private Specification<ScenarioExecution> createSpecificationFromMessageHeaderFilter(MessageHeaderFilter messageHeaderFilter) {
        if (messageHeaderFilter.isValueFilterOnly()) {
            return buildSpecification(
                new StringFilter().setContains(messageHeaderFilter.value),
                ScenarioExecutionQueryService::joinMessageHeadersAndGetValue);
        }

        Specification<ScenarioExecution> messageHeaderKeyEqualsSpecification = buildSpecification(
            new StringFilter().setEqualsIgnoreCase(messageHeaderFilter.key),
            root -> joinMessageHeaders(root).get(MessageHeader_.name));

        var messageHeaderValueSpecification = switch (messageHeaderFilter.operator) {
            case EQUALS, CONTAINS ->
                buildMessageHeaderValueSpecification((StringFilter) messageHeaderFilter.operator.filter.apply(messageHeaderFilter.value));
            case GREATER_THAN, GREATER_THAN_OR_EQUAL_TO, LESS_THAN, LESS_THAN_OR_EQUAL_TO ->
                buildMessageHeaderValueSpecification((LongFilter) messageHeaderFilter.operator.filter.apply(messageHeaderFilter.value));
        };

        return messageHeaderKeyEqualsSpecification.and(messageHeaderValueSpecification);
    }

    private Specification<ScenarioExecution> buildMessageHeaderValueSpecification(StringFilter stringFilter) {
        return buildSpecification(stringFilter, ScenarioExecutionQueryService::joinMessageHeadersAndGetValue);
    }

    private Specification<ScenarioExecution> buildMessageHeaderValueSpecification(LongFilter longFilter) {
        return buildSpecification(longFilter, root -> joinMessageHeadersAndGetValue(root).as(Long.class));
    }

    private static SetJoin<Message, MessageHeader> joinMessageHeaders(Root<ScenarioExecution> root) {
        return root.join(ScenarioExecution_.scenarioMessages, JoinType.LEFT)
            .join(Message_.headers, JoinType.LEFT);
    }

    private static Path<String> joinMessageHeadersAndGetValue(Root<ScenarioExecution> root) {
        return joinMessageHeaders(root)
            .get(MessageHeader_.value);
    }

    enum Operator {

        EQUALS("=", value -> new StringFilter().setContains(value)),
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
