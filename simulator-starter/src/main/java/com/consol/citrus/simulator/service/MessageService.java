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
import com.consol.citrus.simulator.model.MessageFilter;
import com.consol.citrus.simulator.model.MessageHeader;
import com.consol.citrus.simulator.repository.MessageRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Service for persisting and retrieving {@link Message} data.
 */
@Service
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message saveMessage(Message.Direction direction, String payload, String citrusMessageId, Map<String, Object> headers) {
        Message message = new Message();
        message.setDate(now());
        message.setDirection(direction);
        message.setPayload(payload);
        message.setCitrusMessageId(citrusMessageId);
        if (headers != null) {
            headers.entrySet().stream()
                    .forEach(headerEntry -> message.addHeader(
                            new MessageHeader(headerEntry.getKey(),
                                    StringUtils.abbreviate(headerEntry.getValue().toString(), 255))));
        }
        return messageRepository.save(message);
    }

    public Message getMessageById(Long id) {
        return messageRepository.findOne(id);
    }

    public List<Message> getMessagesMatchingFilter(MessageFilter filter) {
        Date calcFromDate = Optional.ofNullable(filter.getFromDate()).orElse(startOfDay());
        Date calcToDate = Optional.ofNullable(filter.getFromDate()).orElse(endOfDay());
        Integer calcPage = Optional.ofNullable(filter.getPageNumber()).orElse(0);
        Integer calcSize = Optional.ofNullable(filter.getPageSize()).orElse(25);
        boolean includeInbound = Optional.ofNullable(filter.getDirectionInbound()).orElse(true);
        boolean includeOutbound = Optional.ofNullable(filter.getDirectionOutbound()).orElse(true);

        Collection<Message.Direction> includeDirections = new TreeSet<>();

        if (includeInbound) {
            includeDirections.add(Message.Direction.INBOUND);
        }

        if (includeOutbound) {
            includeDirections.add(Message.Direction.OUTBOUND);
        }

        Pageable pageable = new PageRequest(calcPage, calcSize, Sort.Direction.DESC, "date");

        if (StringUtils.isNotEmpty(filter.getContainingText())) {
            return messageRepository.findByDateBetweenAndDirectionInAndPayloadContainingIgnoreCase(calcFromDate,
                    calcToDate,
                    includeDirections,
                    filter.getContainingText(),
                    pageable);
        } else {
            return messageRepository.findByDateBetweenAndDirectionIn(calcFromDate,
                    calcToDate,
                    includeDirections,
                    pageable);
        }
    }

    public void clearMessages() {
        messageRepository.deleteAll();
    }

    private Date now() {
        return Date.from(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant());
    }

    private Date startOfDay() {
        return Date.from(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC));
    }

    private Date endOfDay() {
        return Date.from(LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC));
    }


}
