package org.citrusframework.simulator.service;

import org.citrusframework.simulator.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Message}.
 */
public interface MessageService {
    /**
     * Save a message.
     *
     * @param message the entity to save.
     * @return the persisted entity.
     */
    Message save(Message message);

    /**
     * Get all the messages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Message> findAll(Pageable pageable);

    /**
     * Get all the messages with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Message> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" message.
     *
     * @param messageId the id of the entity.
     * @return the entity.
     */
    Optional<Message> findOne(Long messageId);
}
