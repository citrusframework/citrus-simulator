package org.citrusframework.simulator.service;

import org.citrusframework.simulator.model.MessageHeader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link MessageHeader}.
 */
public interface MessageHeaderService {
    /**
     * Save a messageHeader.
     *
     * @param messageHeader the entity to save.
     * @return the persisted entity.
     */
    MessageHeader save(MessageHeader messageHeader);

    /**
     * Get all the messageHeaders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MessageHeader> findAll(Pageable pageable);

    /**
     * Get all the messageHeaders with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MessageHeader> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" messageHeader.
     *
     * @param headerId the id of the entity.
     * @return the entity.
     */
    Optional<MessageHeader> findOne(Long headerId);
}
