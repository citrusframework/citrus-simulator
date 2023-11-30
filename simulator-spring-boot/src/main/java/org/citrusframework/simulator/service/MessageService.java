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

import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
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
     * Get the "id" message.
     *
     * @param messageId the id of the entity.
     * @return the entity.
     */
    Optional<Message> findOne(Long messageId);

    /**
     * Persists the message along with the scenario execution details. With the help of the {@code citrusMessageId}
     * a check is made to determine whether the message has already been persisted. If it has then there's nothing
     * to be done and the persisted message is simply returned.
     *
     * @param scenarioExecutionId the scenario execution id
     * @param direction           the direction of the message
     * @param payload             the message content
     * @param citrusMessageId     the internal citrus message id
     * @param headers             the message headers
     * @return the already or newly persisted message
     */
    Message attachMessageToScenarioExecutionAndSave(Long scenarioExecutionId, Message.Direction direction, String payload, String citrusMessageId, Map<String, Object> headers, EntityManager entityManager);
}
