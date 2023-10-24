package org.citrusframework.simulator.repository;

import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.model.MessageHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.citrusframework.simulator.web.rest.MessageHeaderResourceIT.createEntity;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
class MessageHeaderRepositoryIT {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MessageHeaderRepository messageHeaderRepository;

    private MessageHeader messageHeader;

    @BeforeEach
    void beforeEachSetup() {
        messageHeader = createEntity(entityManager);
        entityManager.persist(messageHeader);
    }

    @Test
    @Transactional
    void testFindAllWithToOneRelationships() {
        Page<MessageHeader> messageHeaders = messageHeaderRepository.findAllWithToOneRelationships(Pageable.unpaged());

        assertTrue(messageHeaders.hasContent());
        verifyRelationships(messageHeaders.getContent().get(0));
    }

    @Test
    @Transactional
    void testFindOneWithToOneRelationships() {
        Optional<MessageHeader> messageHeaders = messageHeaderRepository.findOneWithToOneRelationships(messageHeader.getHeaderId());

        assertTrue(messageHeaders.isPresent());
        verifyRelationships(messageHeaders.get());

        assertFalse(messageHeaderRepository.findOneWithToOneRelationships(Long.MAX_VALUE).isPresent());
    }

    private static void verifyRelationships(MessageHeader messageHeaders) {
        assertNotNull(messageHeaders.getMessage());
    }
}
