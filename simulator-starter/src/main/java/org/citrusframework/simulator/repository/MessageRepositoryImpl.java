package org.citrusframework.simulator.repository;

import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.MessageFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class MessageRepositoryImpl extends AbstractRepository implements MessageRepositoryCustom {

    @Autowired
    private EntityManager em;

    @Override
    public List<Message> find(MessageFilter queryFilter) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Message> criteriaQuery = criteriaBuilder.createQuery(Message.class);

        Root<Message> message = criteriaQuery.from(Message.class);

        List<Predicate> predicates = new ArrayList<>();

        addDatePredicates(queryFilter, criteriaBuilder, message, predicates);
        addDirectionPredicate(queryFilter, criteriaBuilder, message, predicates);
        addPayloadPredicate(queryFilter, criteriaBuilder, message, predicates);
        addHeaderPredicates(queryFilter, criteriaBuilder, message);

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Message> messageQuery = em.createQuery(criteriaQuery);
        addPagingRestrictions(queryFilter, messageQuery);

        return messageQuery.getResultList();
    }

    private void addPagingRestrictions(MessageFilter queryFilter, TypedQuery<Message> messageQuery) {
        if (queryFilter != null) {
            messageQuery.setFirstResult(queryFilter.getPageNumber());
            messageQuery.setMaxResults(queryFilter.getPageSize());
        }
    }

    private void addHeaderPredicates(MessageFilter filter,
                    CriteriaBuilder criteriaBuilder, Root<Message> message) {
        if (StringUtils.hasText(filter.getHeaderFilter())) {
            joinHeader(criteriaBuilder, filter.getHeaderFilter(), message, (root)->root.join("headers", JoinType.INNER));
        }
    }
    
    private void addPayloadPredicate(MessageFilter filter, CriteriaBuilder criteriaBuilder,
                    Root<Message> message, List<Predicate> predicates) {
        if (StringUtils.hasText(filter.getContainingText())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(message.get("payload")),
                            filter.getContainingText().toUpperCase()));
        }
    }

    private void addDatePredicates(MessageFilter filter, CriteriaBuilder criteriaBuilder,
                    Root<Message> message, List<Predicate> predicates) {
        if (filter.getFromDate() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(message.get("date"),
                            filter.getFromDate()));
        }

        if (filter.getToDate() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(message.get("date"),
                            filter.getToDate()));
        }
    }
 
}
