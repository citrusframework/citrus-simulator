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

package com.consol.citrus.simulator.service;

import com.consol.citrus.simulator.model.Message;
import com.consol.citrus.simulator.model.Message.Direction;
import com.consol.citrus.simulator.model.MessageFilter;
import com.consol.citrus.simulator.repository.MessageRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;

public class MessageServiceTest {
    private MessageRepository messageRepository;
    private ArgumentCaptor<Collection<Message.Direction>> directionsCaptor;
    private ArgumentCaptor<Pageable> pageableCaptor;

    private MessageService sut;

    @BeforeMethod
    public void init() {
        messageRepository = Mockito.mock(MessageRepository.class);
        directionsCaptor = ArgumentCaptor.forClass(Collection.class);
        pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        sut = new MessageService(messageRepository);
    }

    @AfterMethod
    public void clear() {
        Mockito.reset(messageRepository);
    }

    @Test
    public void shouldGetMessagesUsingDefaults() throws Exception {
        MessageFilter filter = new MessageFilter();

        when(messageRepository.findByDateBetweenAndDirectionIn(
                notNull(),
                notNull(),
                directionsCaptor.capture(),
                pageableCaptor.capture())
        ).thenReturn(singleResult());

        assertHasSingleResult(sut.getMessagesMatchingFilter(filter));

        assertPagingMatches(0, 25);
        assertDirectionMatches(Message.Direction.INBOUND, Message.Direction.OUTBOUND);
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

        when(messageRepository.findByDateBetweenAndDirectionInAndPayloadContainingIgnoreCase(
                eq(dateFrom),
                eq(dateTo),
                directionsCaptor.capture(),
                eq(text),
                pageableCaptor.capture())
        ).thenReturn(singleResult());

        assertHasSingleResult(sut.getMessagesMatchingFilter(filter));

        assertPagingMatches(pageNumber, pageSize);
        assertDirectionMatches();
    }
    
    @Test
    void testFindByHeader() {

        String payload = "This is a test message!";
        String uid = UUID.randomUUID().toString();

        Map<String, Object> headers = new HashMap<>();
        headers.put("h1", uid);

        sut.saveMessage(Direction.INBOUND, payload, UUID.randomUUID().toString(), headers);

        List<Message> result =
                        messageRepository
                                        .findByDateBetweenAndDirectionInAndPayloadContainingIgnoreCase(
                                                        null, null,
                                                        Collections.singleton(Direction.INBOUND),
                                                        null, "h1", uid, null);

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(payload, result.get(0).getPayload());

    }

    /**
     * Passing only one parameter of headerParamName and headerParamValue should not perform any filtering
     */
    @Test
    void testFindByHeaderInsufficientParams() {

        String payload = "This is a test message!";
        String uid = UUID.randomUUID().toString();

        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("h1", uid);

        sut.saveMessage(Direction.INBOUND, payload, UUID.randomUUID().toString(), headers);

        // If headerParamName or headerParamValue is not supported no filtering should be performed
        List<Message> result = messageRepository
                        .findByDateBetweenAndDirectionInAndPayloadContainingIgnoreCase(null, null,
                                        Collections.singleton(Direction.INBOUND), null, "h1", null,
                                        null);

        // No filter thus all messages should be returned. Depending on the number of executed tests or savedMessages this should be a value > 0.
        Assert.assertTrue(result.size() > 0);

        result = messageRepository.findByDateBetweenAndDirectionInAndPayloadContainingIgnoreCase(
                        null, null, Collections.singleton(Direction.INBOUND), null, null, uid,
                        null);

        // No filter thus all messages should be returned. Depending on the number of executed tests or savedMessages this should be a value > 0.
        Assert.assertTrue(result.size() > 0);

    }


    @Test
    void testFindByAllParams() {

        String payload = "This is a test message!";
        String uid = UUID.randomUUID().toString();

        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("h1", uid);

        Date startSavingDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));

        sut.saveMessage(Direction.INBOUND, payload,
                        UUID.randomUUID().toString(), headers);

        Date endSavingDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));

        // Filter by all valid
        List<Message> result = messageRepository
                        .findByDateBetweenAndDirectionInAndPayloadContainingIgnoreCase(
                                        startSavingDate, endSavingDate,
                                        Collections.singleton(Direction.INBOUND), payload, "h1",
                                        uid, null);

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(payload, result.get(0).getPayload());


    }

    private List<Message> singleResult() {
        return Collections.singletonList(new Message());
    }

    private void assertHasSingleResult(List<Message> messages) {
        Assert.assertEquals(messages.size(), 1);
    }

    private void assertPagingMatches(int pageNumber, int pageSize) {
        Assert.assertEquals(pageableCaptor.getValue().getPageNumber(), pageNumber);
        Assert.assertEquals(pageableCaptor.getValue().getPageSize(), pageSize);
        Assert.assertEquals(pageableCaptor.getValue().getSort().getOrderFor("date").getDirection(), Sort.Direction.DESC);
    }

    private void assertDirectionMatches(Message.Direction... direction) {
        int expectedSize = direction.length;
        Assert.assertEquals(directionsCaptor.getValue().size(), expectedSize);

        for (Message.Direction d : direction) {
            Assert.assertEquals(directionsCaptor.getValue().contains(d), true);
        }
    }

}