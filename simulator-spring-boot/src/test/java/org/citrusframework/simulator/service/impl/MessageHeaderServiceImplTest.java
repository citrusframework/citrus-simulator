/*
 * Copyright 2024 the original author or authors.
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

import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.MessageHeader;
import org.citrusframework.simulator.repository.MessageHeaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.springframework.data.domain.Pageable.unpaged;

@ExtendWith(MockitoExtension.class)
class MessageHeaderServiceImplTest {

    @Mock
    private MessageHeaderRepository messageHeaderRepositoryMock;

    private MessageHeader messageHeaderWithMessage;

    private MessageHeaderServiceImpl fixture;

    @BeforeEach
    void beforeEachSetup() {
        MessageHeader messageHeader = new MessageHeader();
        Message message = Message.builder()
            .messageId(1234L)
            .citrusMessageId("citrus-message-id")
            .build();
        messageHeader.setMessage(message);
        messageHeaderWithMessage = spy(messageHeader);

        fixture = new MessageHeaderServiceImpl(messageHeaderRepositoryMock);
    }

    @Test
    void testSave() {
        MessageHeader messageHeader = new MessageHeader();

        doReturn(messageHeader).when(messageHeaderRepositoryMock).save(messageHeader);

        MessageHeader savedMessageHeader = fixture.save(messageHeader);
        assertEquals(messageHeader, savedMessageHeader);
    }

    @Test
    void testFindAll() {
        Pageable pageable = unpaged();
        Page<MessageHeader> page = new PageImpl<>(List.of(messageHeaderWithMessage));

        doReturn(page).when(messageHeaderRepositoryMock).findAllWithEagerRelationships(pageable);

        Page<MessageHeader> result = fixture.findAll(pageable);

        assertEquals(page, result);
    }

    @Test
    void testFindOne() {
        Long messageId = 1L;

        doReturn(Optional.of(messageHeaderWithMessage)).when(messageHeaderRepositoryMock).findOneWithEagerRelationships(messageId);

        Optional<MessageHeader> maybeMessageHeader = fixture.findOne(messageId);

        assertTrue(maybeMessageHeader.isPresent());
        assertEquals(messageHeaderWithMessage, maybeMessageHeader.get());
    }
}
