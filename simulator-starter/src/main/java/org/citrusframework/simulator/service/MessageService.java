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
     * Updates a message.
     *
     * @param message the entity to update.
     * @return the persisted entity.
     */
    Message update(Message message);

    /**
     * Get all the messages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Message> findAll(Pageable pageable);

    /**
     * Get the "id" message.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Message> findOne(Long id);

    /**
     * Delete the "id" message.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
