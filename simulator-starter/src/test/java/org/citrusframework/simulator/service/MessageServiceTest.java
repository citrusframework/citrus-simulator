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
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

public class MessageServiceTest {

    private QueryFilterAdapterFactory queryFilterAdapterFactory = new QueryFilterAdapterFactory(
            new SimulatorConfigurationProperties());

    private MessageRepository messageRepository;
    private ArgumentCaptor<Collection<Message.Direction>> directionsCaptor;
    private ArgumentCaptor<Pageable> pageableCaptor;

    private MessageService sut;
    private ArgumentCaptor<MessageFilter> messageFilterCaptor;

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void init() {
        messageFilterCaptor = ArgumentCaptor.forClass(MessageFilter.class);
        messageRepository = Mockito.mock(MessageRepository.class);
        directionsCaptor = ArgumentCaptor.forClass(Collection.class);
        pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        sut = new MessageService(messageRepository, queryFilterAdapterFactory);
    }

    @AfterMethod
    public void clear() {
        Mockito.reset(messageRepository);
    }

    @Test
    public void shouldGetMessagesUsingDefaults() throws Exception {
        MessageFilter filter = new MessageFilter();

        when(messageRepository.find(messageFilterCaptor.capture())).thenReturn(singleResult());

        assertHasSingleResult(sut.getMessagesMatchingFilter(filter));

        assertPagingMatches(0, 25);
        assertDirectionMatches(true, true);
    }

    @Test
    public void shouldGetMessagesUsingNoDefaults() throws Exception {
        Date dateFrom = new Date();
        Date dateTo = new Date();
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

        when(messageRepository.find(messageFilterCaptor.capture())).thenReturn(singleResult());

        assertHasSingleResult(sut.getMessagesMatchingFilter(filter));

        assertPagingMatches(pageNumber, pageSize);
        assertDirectionMatches(false, false);
    }

    private List<Message> singleResult() {
        return Collections.singletonList(new Message());
    }

    private void assertHasSingleResult(List<Message> messages) {
        Assert.assertEquals(messages.size(), 1);
    }

    private void assertPagingMatches(int pageNumber, int pageSize) {
        Assert.assertEquals(messageFilterCaptor.getValue().getPageNumber(), (Integer) pageNumber);
        Assert.assertEquals(messageFilterCaptor.getValue().getPageSize(), (Integer) pageSize);
    }

    private void assertDirectionMatches(boolean inbound, boolean outbound) {
        Assert.assertEquals(messageFilterCaptor.getValue().getDirectionInbound(), (Boolean) inbound);
        Assert.assertEquals(messageFilterCaptor.getValue().getDirectionOutbound(), (Boolean) outbound);
    }

}
