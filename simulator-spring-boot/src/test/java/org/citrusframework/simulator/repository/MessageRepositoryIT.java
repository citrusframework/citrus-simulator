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
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        Page<Message> messages = messageRepository.findAllWithToOneRelationships(Pageable.unpaged());

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
        List<Message> allForScenarioExecution = messageRepository.findAllForScenarioExecution(scenarioExecution.getExecutionId(), expectedMessage.getDirection(), expectedMessage.getCitrusMessageId(), entityManager);
        assertThat(allForScenarioExecution)
            .hasSize(1)
            .containsExactly(expectedMessage);
    }
}
