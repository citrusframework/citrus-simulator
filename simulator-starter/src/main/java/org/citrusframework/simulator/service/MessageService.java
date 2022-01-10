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

import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.MessageFilter;
import org.citrusframework.simulator.model.MessageHeader;
import org.citrusframework.simulator.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Service for persisting and retrieving {@link Message} data.
 */
@Service
@Transactional
public class MessageService {

    private QueryFilterAdapterFactory queryFilterAdapterFactory;

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, QueryFilterAdapterFactory queryFilterAdapterFactory) {
        this.messageRepository = messageRepository;
        this.queryFilterAdapterFactory = queryFilterAdapterFactory;
    }

    public Message saveMessage(Message.Direction direction, String payload, String citrusMessageId, Map<String, Object> headers) {
        Message message = new Message();
        message.setDate(now());
        message.setDirection(direction);
        message.setPayload(payload);
        message.setCitrusMessageId(citrusMessageId);
        if (headers != null) {
            for (Entry<String, Object> headerEntry : headers.entrySet()) {
                if (headerEntry.getValue() != null
                    && !StringUtils.isEmpty(headerEntry.getValue().toString())) {
                    message.addHeader(
                        new MessageHeader(headerEntry.getKey(),
                            StringUtils.abbreviate(headerEntry.getValue().toString(), 255)));
                }
            }
        }
        return messageRepository.save(message);
    }

    public Message getMessageById(Long id) {
        return messageRepository.findById(id).orElseThrow(() -> new CitrusRuntimeException(String.format("Failed to find message for id %s", id)));
    }

	public List<Message> getMessagesMatchingFilter(MessageFilter filter) {
	    return messageRepository.find(queryFilterAdapterFactory.getQueryAdapter(filter));
	}

    public void clearMessages() {
        messageRepository.deleteAll();
    }

    private Date now() {
        return Date.from(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant());
    }

}
