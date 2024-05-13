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
import org.citrusframework.simulator.model.MessageHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.citrusframework.simulator.web.rest.MessageHeaderResourceIT.createEntity;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.data.domain.Pageable.unpaged;

@IntegrationTest
class MessageHeaderRepositoryIT {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MessageHeaderRepository messageHeaderRepository;

    private MessageHeader messageHeader;

    private static void verifyRelationships(MessageHeader messageHeaders) {
        assertNotNull(messageHeaders.getMessage());
    }

    @BeforeEach
    void beforeEachSetup() {
        messageHeader = createEntity(entityManager);
        entityManager.persist(messageHeader);
    }

    @Test
    @Transactional
    void testFindAllWithToOneRelationships() {
        Page<MessageHeader> messageHeaders = messageHeaderRepository.findAllWithToOneRelationships(unpaged());

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
}
