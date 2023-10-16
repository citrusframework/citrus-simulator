/*
 * Copyright 2006-2017 the original author or authors.
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

import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.MessageFilter;
import org.citrusframework.simulator.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    private final QueryFilterAdapterFactory queryFilterAdapterFactory = new QueryFilterAdapterFactory(new SimulatorConfigurationProperties());

    @Mock
    private MessageRepository messageRepositoryMock;

    private ArgumentCaptor<MessageFilter> messageFilterCaptor;

    private MessageService fixture;

    @BeforeEach
    void beforeEachSetup() {
        messageFilterCaptor = ArgumentCaptor.forClass(MessageFilter.class);
        fixture = new MessageService(messageRepositoryMock, queryFilterAdapterFactory);
    }

    @Test
    void shouldGetMessagesUsingDefaults() {
        MessageFilter filter = new MessageFilter();

        when(messageRepositoryMock.find(messageFilterCaptor.capture())).thenReturn(singleResult());

        assertHasSingleResult(fixture.getMessagesMatchingFilter(filter));

        assertPagingMatches(0, 25);
        assertDirectionMatches(true, true);
    }

    @Test
    void shouldGetMessagesUsingNoDefaults() {
        Instant dateFrom = Instant.now();
        Instant dateTo = Instant.now();
        int pageNumber = 10;
        int pageSize = 1000;
        String text = "abc";

        MessageFilter filter = new MessageFilter();
        filter.setFromDate(dateFrom);
        filter.setToDate(dateTo);
        filter.setPageNumber(pageNumber);
        filter.setPageSize(pageSize);
        filter.setDirectionInbound(false);
        filter.setDirectionOutbound(false);
        filter.setContainingText(text);

        when(messageRepositoryMock.find(messageFilterCaptor.capture())).thenReturn(singleResult());

        assertHasSingleResult(fixture.getMessagesMatchingFilter(filter));

        assertPagingMatches(pageNumber, pageSize);
        assertDirectionMatches(false, false);
    }

    private List<Message> singleResult() {
        return Collections.singletonList(new Message());
    }

    private void assertHasSingleResult(List<Message> messages) {
        assertEquals(messages.size(), 1);
    }

    private void assertPagingMatches(int pageNumber, int pageSize) {
        assertEquals(messageFilterCaptor.getValue().getPageNumber(), (Integer) pageNumber);
        assertEquals(messageFilterCaptor.getValue().getPageSize(), (Integer) pageSize);
    }

    private void assertDirectionMatches(boolean inbound, boolean outbound) {
        assertEquals(messageFilterCaptor.getValue().getDirectionInbound(), (Boolean) inbound);
        assertEquals(messageFilterCaptor.getValue().getDirectionOutbound(), (Boolean) outbound);
    }
}
