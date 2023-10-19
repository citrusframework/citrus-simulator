package org.citrusframework.simulator.repository;

import org.citrusframework.simulator.model.MessageHeader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the MessageHeader entity.
 */
@Repository
public interface MessageHeaderRepository extends JpaRepository<MessageHeader, Long>, JpaSpecificationExecutor<MessageHeader> {
    default Optional<MessageHeader> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<MessageHeader> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<MessageHeader> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select messageHeader from MessageHeader messageHeader left join fetch messageHeader.message",
        countQuery = "select count(messageHeader) from MessageHeader messageHeader"
    )
    Page<MessageHeader> findAllWithToOneRelationships(Pageable pageable);

    @Query("select messageHeader from MessageHeader messageHeader left join fetch messageHeader.message")
    List<MessageHeader> findAllWithToOneRelationships();

    @Query("select messageHeader from MessageHeader messageHeader left join fetch messageHeader.message where messageHeader.id =:id")
    Optional<MessageHeader> findOneWithToOneRelationships(@Param("id") Long id);
}
