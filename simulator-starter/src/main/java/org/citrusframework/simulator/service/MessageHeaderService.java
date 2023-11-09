/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
