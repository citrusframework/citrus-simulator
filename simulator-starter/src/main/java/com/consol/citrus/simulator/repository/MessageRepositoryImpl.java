package com.consol.citrus.simulator.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.consol.citrus.simulator.model.Message;
import com.consol.citrus.simulator.model.Message.Direction;
import com.consol.citrus.simulator.model.MessageHeader;

public class MessageRepositoryImpl implements MessageRepositoryCustom {

    @Autowired
    private EntityManager em;

    @Override
    public List<Message> findByDateBetweenAndDirectionInAndPayloadContainingIgnoreCase(
                    Date fromDate, Date toDate, Collection<Direction> directions,
                    String containingText, String headerParameterName,
                    String headerParameterValue, Pageable pageable) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Message> criteriaQuery = criteriaBuilder.createQuery(Message.class);

        Root<Message> message = criteriaQuery.from(Message.class);

        List<Predicate> predicates = new ArrayList<>();

        addDatePredicates(fromDate, toDate, criteriaBuilder, message, predicates);
        addDirectionPredicate(directions, criteriaBuilder, message, predicates);
        addPayloadPredicate(containingText, criteriaBuilder, message, predicates);
        addHeaderPredicates(headerParameterName, headerParameterValue, criteriaBuilder, message);

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Message> messageQuery = em.createQuery(criteriaQuery);
        addPagingRestrictions(pageable, messageQuery);

        return messageQuery.getResultList();
    }

    private void addPagingRestrictions(Pageable pageable, TypedQuery<Message> messageQuery) {
        if (pageable != null) {
            messageQuery.setMaxResults(pageable.getPageSize());
            messageQuery.setFirstResult(pageable.getPageNumber());
        }
    }

    private void addHeaderPredicates(String headerParameterName, String headerParameterValue,
                    CriteriaBuilder criteriaBuilder, Root<Message> message) {
        if (StringUtils.hasText(headerParameterName)
                        && StringUtils.hasText(headerParameterValue)) {
            Join<Message, MessageHeader> join = message.join("headers", JoinType.INNER);
            join.on(criteriaBuilder.equal(join.get("name"), headerParameterName),
                            criteriaBuilder.like(join.get("value"),
                                            headerParameterValue));
        }
    }

    private void addPayloadPredicate(String containingText, CriteriaBuilder criteriaBuilder,
                    Root<Message> message, List<Predicate> predicates) {
        if (StringUtils.hasText(containingText)) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(message.get("payload")),
                            containingText.toUpperCase()));
        }
    }

    private void addDatePredicates(Date fromDate, Date toDate, CriteriaBuilder criteriaBuilder,
                    Root<Message> message, List<Predicate> predicates) {
        if (fromDate != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(message.get("date"),
                            fromDate));
        }

        if (toDate != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(message.get("date"),
                            toDate));
        }
    }

    private void addDirectionPredicate(Collection<Direction> directions,
                    CriteriaBuilder criteriaBuilder, Root<Message> message,
                    List<Predicate> predicates) {
        if (!CollectionUtils.isEmpty(directions)) {
            In<Direction> inDirections = criteriaBuilder.in(message.get("direction"));
            directions.forEach(inDirections::value);
            predicates.add(inDirections);
        }
    }

}
