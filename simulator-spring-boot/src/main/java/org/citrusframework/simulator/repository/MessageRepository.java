package org.citrusframework.simulator.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.Message_;
import org.citrusframework.simulator.model.ScenarioExecution_;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Message} entity.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {

    default Optional<Message> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default Page<Message> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select message from Message message left join fetch message.scenarioExecution",
        countQuery = "select count(message) from Message message"
    )
    Page<Message> findAllWithToOneRelationships(Pageable pageable);

    @Query("select message from Message message left join fetch message.headers left join fetch message.scenarioExecution where message.messageId = :messageId")
    Optional<Message> findOneWithToOneRelationships(@Param("messageId") Long messageId);

    @Override
    @EntityGraph(attributePaths = {"headers", "scenarioExecution"})
    Page<Message> findAll(Specification<Message> spec, Pageable pageable);

    default List<Message> findAllForScenarioExecution(Long scenarioExecutionId, Message.Direction direction, String citrusMessageId, EntityManager entityManager) {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Message> criteriaQuery = criteriaBuilder.createQuery(Message.class);
            Root<Message> root = criteriaQuery.from(Message.class);

            Predicate[] predicates = new Predicate[]{
                criteriaBuilder.equal( root.join(Message_.scenarioExecution, JoinType.LEFT).get(
                        ScenarioExecution_.executionId),
                    scenarioExecutionId),
                criteriaBuilder.equal(root.get(Message_.citrusMessageId), citrusMessageId),
                criteriaBuilder.equal(root.get(Message_.direction), direction.getId())
            };

            criteriaQuery.select(root).where(predicates);

            return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
