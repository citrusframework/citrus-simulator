/*
 * Copyright the original author or authors.
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

package org.citrusframework.simulator.service.impl;

import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.repository.MessageRepository;
import org.citrusframework.simulator.service.MessageService;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Service Implementation for managing {@link Message§}.
 */
@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageRepository messageRepository;
    private final ScenarioExecutionService scenarioExecutionService;

    public MessageServiceImpl(MessageRepository messageRepository, ScenarioExecutionService scenarioExecutionService) {
        this.messageRepository = messageRepository;
        this.scenarioExecutionService = scenarioExecutionService;
    }

    @Override
    public Message save(Message message) {
        logger.debug("Request to save Message : {}", message);
        return messageRepository.save(message);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> findAll(Pageable pageable) {
        logger.debug("Request to get all Messages with eager relationships");
        return messageRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Message> findOne(Long messageId) {
        logger.debug("Request to get Message : {}", messageId);
        return messageRepository.findOneWithEagerRelationships(messageId);
    }

    @Override
    public Message attachMessageToScenarioExecutionAndSave(Long scenarioExecutionId, Message.Direction direction, String payload, String citrusMessageId, Map<String, Object> headers) {
        logger.debug("Request to save {} Message with citrusMessageId '{}' in correlation with ScenarioExecution : {}", direction, citrusMessageId, scenarioExecutionId);

        List<Message> messages = messageRepository.findAllForScenarioExecution(scenarioExecutionId, citrusMessageId, direction);
        if (!messages.isEmpty()) {
            logger.trace("Message is already persisted and attached to execution scenario");
            return messages.get(0);
        }

        Message message = messageRepository.save(
            Message.builder()
                .direction(direction)
                .payload(payload)
                .citrusMessageId(citrusMessageId)
                .headers(headers)
                .build()
        );

        scenarioExecutionService.save(
            scenarioExecutionService.findOneLazy(scenarioExecutionId)
                .map(scenarioExecution -> {
                    scenarioExecution.addScenarioMessage(message);
                    return scenarioExecution;
                })
                .orElseThrow(() -> new CitrusRuntimeException(format("Error while attaching Message to ScenarioExecution %s: Did not find corresponding ScenarioExecution!", scenarioExecutionId)))
        );

        return message;
    }
}
