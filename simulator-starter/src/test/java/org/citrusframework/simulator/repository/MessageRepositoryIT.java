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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
class MessageRepositoryIT {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MessageRepository messageRepository;

    private Message message;

    private static void verifyRelationships(Message messages) {
        assertNotNull(messages.getScenarioExecutionId());
        assertNotNull(messages.getScenarioName());
    }

    @BeforeEach
    void beforeEachSetup() {
        message = MessageResourceIT.createEntity(entityManager);

        ScenarioExecution scenarioExecution = ScenarioExecutionResourceIT.createEntity(entityManager);
        entityManager.persist(scenarioExecution);
        message.setScenarioExecution(scenarioExecution);

        entityManager.persist(message);
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
}
