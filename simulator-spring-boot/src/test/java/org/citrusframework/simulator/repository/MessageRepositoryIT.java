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

package org.citrusframework.simulator.repository;

import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.web.rest.MessageResourceIT;
import org.citrusframework.simulator.web.rest.ScenarioExecutionResourceIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.data.domain.Pageable.unpaged;

@IntegrationTest
class MessageRepositoryIT {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MessageRepository messageRepository;

    private ScenarioExecution scenarioExecution;
    private Message message;

    private static void verifyRelationships(Message messages) {
        assertNotNull(messages.getScenarioExecutionId());
        assertNotNull(messages.getScenarioName());
    }

    @BeforeEach
    void beforeEachSetup() {
        scenarioExecution = ScenarioExecutionResourceIT.createEntity(entityManager);
        entityManager.persist(scenarioExecution);

        message = createAndPersistMessageForScenarioExecution(MessageResourceIT.createEntity(entityManager), scenarioExecution);
    }

    private Message createAndPersistMessageForScenarioExecution(Message entity, ScenarioExecution scenarioExecution) {
        entity.setScenarioExecution(scenarioExecution);
        entityManager.persist(entity);
        return entity;
    }

    @Test
    @Transactional
    void testFindAllWithToOneRelationships() {
        Page<Message> messages = messageRepository.findAllWithToOneRelationships(unpaged());

        assertTrue(messages.hasContent());
        verifyRelationships(messages.getContent().get(0));
    }

    @Test
    @Transactional
    void testFindOneWithToOneRelationships() {
        Optional<Message> messages = messageRepository.findOneWithToOneRelationships(message.getMessageId());

        assertTrue(messages.isPresent());
        verifyRelationships(messages.get());

        assertFalse(messageRepository.findOneWithToOneRelationships(Long.MAX_VALUE).isPresent());
    }

    @Test
    @Transactional
    void findAllForScenarioExecution() {
        Message message2 = createAndPersistMessageForScenarioExecution(MessageResourceIT.createUpdatedEntity(entityManager), scenarioExecution);

        verifyOnlyOneMessageFoundForScenarioExecution(message);
        verifyOnlyOneMessageFoundForScenarioExecution(message2);
    }

    private void verifyOnlyOneMessageFoundForScenarioExecution(Message expectedMessage) {
        List<Message> allForScenarioExecution = messageRepository.findAllForScenarioExecution(scenarioExecution.getExecutionId(), expectedMessage.getCitrusMessageId(), expectedMessage.getDirection());
        assertThat(allForScenarioExecution)
            .hasSize(1)
            .containsExactly(expectedMessage);
    }

    @Test
    @Transactional
    void findAllByScenarioExecutionExecutionIdEqualsAndCitrusMessageIdEqualsIgnoreCaseAndDirectionEquals() {
        Message message2 = createAndPersistMessageForScenarioExecution(MessageResourceIT.createUpdatedEntity(entityManager), scenarioExecution);

        verifyOnlyOneMessageFoundByIdentifier(message);
        verifyOnlyOneMessageFoundByIdentifier(message2);
    }

    void verifyOnlyOneMessageFoundByIdentifier(Message expectedMessage) {
        List<Message> allForScenarioExecution = messageRepository.findAllByScenarioExecutionExecutionIdEqualsAndCitrusMessageIdEqualsIgnoreCaseAndDirectionEquals(scenarioExecution.getExecutionId(), expectedMessage.getCitrusMessageId(), expectedMessage.getDirection().getId());
        assertThat(allForScenarioExecution)
            .hasSize(1)
            .containsExactly(expectedMessage);
    }
}
