package com.consol.citrus.simulator.repository;

import com.consol.citrus.simulator.model.Message;
import com.consol.citrus.simulator.model.ScenarioExecution;
import com.consol.citrus.simulator.model.ScenarioExecutionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class ScenarioExecutionRepositoryImpl extends AbstractRepository implements ScenarioExecutionRepositoryCustom {

    @Autowired
    private EntityManager em;

    @Override
    public List<ScenarioExecution> find(ScenarioExecutionFilter filter) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ScenarioExecution> criteriaQuery = criteriaBuilder.createQuery(ScenarioExecution.class);

        Root<ScenarioExecution> scenarioExecution = criteriaQuery.from(ScenarioExecution.class);

        List<Predicate> predicates = new ArrayList<>();
        addScenarioNamePredicate(filter, criteriaBuilder, scenarioExecution, predicates);
        addDatePredicates(filter, criteriaBuilder, scenarioExecution, predicates);
        joinMessage(filter, criteriaBuilder, scenarioExecution);
        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        TypedQuery<ScenarioExecution> messageQuery = em.createQuery(criteriaQuery.distinct(true));
        addPagingRestrictions(filter, messageQuery);

        return messageQuery.getResultList();
    }

    private void addScenarioNamePredicate(ScenarioExecutionFilter filter, CriteriaBuilder criteriaBuilder,
            Root<ScenarioExecution> scenarioExecution, List<Predicate> predicates) {
        if (StringUtils.hasLength(filter.getScenarioName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(scenarioExecution.get("scenarioName")),
                    filter.getScenarioName().toUpperCase()));
        }
    }

    private void addPagingRestrictions(ScenarioExecutionFilter filter, TypedQuery<?> query) {
        query.setFirstResult(filter.getPageNumber());
        query.setMaxResults(filter.getPageSize());
    }

    private void joinMessage(ScenarioExecutionFilter filter, CriteriaBuilder criteriaBuilder,
            Root<ScenarioExecution> scenarioExecution) {

        if (StringUtils.hasText(filter.getContainingText()) || !filter.getDirectionOutbound()
                || !filter.getDirectionInbound() || StringUtils.hasText(filter.getHeaderFilter())) {

            Join<ScenarioExecution, Message> messageJoin = scenarioExecution.join("scenarioMessages");
                   
            
            joinHeader(criteriaBuilder, filter.getHeaderFilter(),
                    messageJoin, (root) -> root.join("headers", JoinType.INNER));
           
            List<Predicate> additionalJoinPredicates = new ArrayList<Predicate>();
            addPayloadPredicate(filter, criteriaBuilder, messageJoin, additionalJoinPredicates);
            addDirectionPredicate(filter, criteriaBuilder, messageJoin, additionalJoinPredicates);
            messageJoin.on(additionalJoinPredicates.toArray(new Predicate[0]));
        }
    }

    private void addPayloadPredicate(ScenarioExecutionFilter filter, CriteriaBuilder criteriaBuilder,
            Join<ScenarioExecution, Message> parentJoin, List<Predicate> predicates) {
        if (StringUtils.hasText(filter.getContainingText())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(parentJoin.get("payload")),
                    filter.getContainingText().toUpperCase()));
        }
    }

    private void addDatePredicates(ScenarioExecutionFilter filter, CriteriaBuilder criteriaBuilder,
            Root<ScenarioExecution> scenarioExecution, List<Predicate> predicates) {
        if (filter.getFromDate() != null) {
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(scenarioExecution.get("startDate"), filter.getFromDate()));
        }

        if (filter.getToDate() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(scenarioExecution.get("endDate"), filter.getToDate()));
        }
    }

}
