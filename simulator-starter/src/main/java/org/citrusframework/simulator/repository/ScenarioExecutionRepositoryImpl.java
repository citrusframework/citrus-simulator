package org.citrusframework.simulator.repository;

import ch.qos.logback.classic.spi.Configurator.ExecutionStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioExecution.Status;
import org.citrusframework.simulator.model.ScenarioExecutionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class ScenarioExecutionRepositoryImpl extends AbstractRepository implements ScenarioExecutionRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<ScenarioExecution> find(ScenarioExecutionFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ScenarioExecution> criteriaQuery = criteriaBuilder.createQuery(ScenarioExecution.class);

        Root<ScenarioExecution> scenarioExecution = criteriaQuery.from(ScenarioExecution.class);

        List<Predicate> predicates = new ArrayList<>();
        addScenarioNamePredicate(filter, criteriaBuilder, scenarioExecution, predicates);
        addScenarioStatusPredicate(filter, criteriaBuilder, scenarioExecution, predicates);
        addDatePredicates(filter, criteriaBuilder, scenarioExecution, predicates);
        joinMessage(filter, criteriaBuilder, scenarioExecution);
        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        criteriaQuery.orderBy(criteriaBuilder.desc(scenarioExecution.get("startDate")));

        TypedQuery<ScenarioExecution> messageQuery = entityManager.createQuery(criteriaQuery.distinct(true));
        addPagingRestrictions(filter, messageQuery);

        return messageQuery.getResultList();
    }

    /**
     * Adds the scenario name predicate if respective filter is active.
     *
     * @param filter
     * @param criteriaBuilder
     * @param scenarioExecution
     * @param predicates
     */
    private void addScenarioNamePredicate(ScenarioExecutionFilter filter, CriteriaBuilder criteriaBuilder,
            Root<ScenarioExecution> scenarioExecution, List<Predicate> predicates) {
        if (StringUtils.hasLength(filter.getScenarioName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(scenarioExecution.get("scenarioName")),
                    filter.getScenarioName().toUpperCase()));
        }
    }

    /**
     * Adds the execution status predicate if respective filter is active.
     *
     * @param filter
     * @param criteriaBuilder
     * @param scenarioExecution
     * @param predicates
     */
    private void addScenarioStatusPredicate(ScenarioExecutionFilter filter, CriteriaBuilder criteriaBuilder,
            Root<ScenarioExecution> scenarioExecution, List<Predicate> predicates) {
        if (filter.getExecutionStatus() != null && filter.getExecutionStatus().length>0) {
            CriteriaBuilder.In<Integer> inClause = criteriaBuilder.in(scenarioExecution.get("status"));
            Arrays.stream(filter.getExecutionStatus()).map(Status::getId).forEach(inClause::value);
            predicates.add(inClause);
        }
    }

    /**
     * Adds the paging restrictions to the query.
     *
     * @param filter
     * @param query
     */
    private void addPagingRestrictions(ScenarioExecutionFilter filter, TypedQuery<?> query) {
        query.setFirstResult(filter.getPageNumber() * filter.getPageSize());
        query.setMaxResults(filter.getPageSize());
    }

    /**
     * Join the scenarioMessages respective filters are active.
     *
     * @param filter
     * @param criteriaBuilder
     * @param scenarioExecution
     */
    private void joinMessage(ScenarioExecutionFilter filter, CriteriaBuilder criteriaBuilder,
            Root<ScenarioExecution> scenarioExecution) {

        if (StringUtils.hasText(filter.getContainingText()) || !filter.getDirectionOutbound()
                || !filter.getDirectionInbound() || StringUtils.hasText(filter.getHeaderFilter())) {

            Join<ScenarioExecution, Message> messageJoin = scenarioExecution.join("scenarioMessages");

            joinHeader(criteriaBuilder, filter.getHeaderFilter(),
                    messageJoin, (root) -> root.join("headers", JoinType.INNER));

            List<Predicate> additionalJoinPredicates = new ArrayList<>();
            addPayloadPredicate(filter, criteriaBuilder, messageJoin, additionalJoinPredicates);
            addDirectionPredicate(filter, criteriaBuilder, messageJoin, additionalJoinPredicates);
            if (!additionalJoinPredicates.isEmpty()) {
                messageJoin.on(additionalJoinPredicates.toArray(new Predicate[0]));
            }
        }
    }

    /**
     * Adds the payload predicate if respective filter is active.
     *
     * @param filter
     * @param criteriaBuilder
     * @param parentJoin
     * @param predicates
     */
    private void addPayloadPredicate(ScenarioExecutionFilter filter, CriteriaBuilder criteriaBuilder,
                                     Join<ScenarioExecution, Message> parentJoin, List<Predicate> predicates) {
        if (StringUtils.hasText(filter.getContainingText())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(criteriaBuilder.toString(parentJoin.get("payload"))),
                    filter.getContainingText().toUpperCase()));
        }
    }

    /**
     * Adds date predicates if respective filter are active.
     *
     * @param filter
     * @param criteriaBuilder
     * @param scenarioExecution
     * @param predicates
     */
    private void addDatePredicates(ScenarioExecutionFilter filter, CriteriaBuilder criteriaBuilder,
                                   Root<ScenarioExecution> scenarioExecution, List<Predicate> predicates) {
        if (filter.getFromDate() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(scenarioExecution.get("startDate"), filter.getFromDate()));
        }

        if (filter.getToDate() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(scenarioExecution.get("endDate"), filter.getToDate()));
        }
    }
}
