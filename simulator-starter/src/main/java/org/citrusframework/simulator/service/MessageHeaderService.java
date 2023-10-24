package org.citrusframework.simulator.service;

import org.citrusframework.simulator.model.Message;
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
     * Get the "id" messageHeader.
     *
     * @param headerId the id of the entity.
     * @return the entity.
     */
    Optional<MessageHeader> findOne(Long headerId);

    /**
     * Function that converts the {@link MessageHeader} to its "DTO-form": It may only contain the {@code messageId}
     * and {@code citrusMessageId} of the related {@link Message}, no further attributes. That is especially true for
     * relationships, because of a possible {@link org.hibernate.LazyInitializationException}).
     *
     * @param messageHeader The entity, which should be returned
     * @return the entity with prepared {@link Message}
     */
    static MessageHeader restrictToDtoProperties(MessageHeader messageHeader) {
        Message message = messageHeader.getMessage();
        messageHeader.setMessage(Message.builder().messageId(message.getMessageId()).citrusMessageId(message.getCitrusMessageId()).build());
        return messageHeader;
    }
}
