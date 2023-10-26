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

package org.citrusframework.simulator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * JPA entity for representing inbound and outbound messages
 */
@Entity
public class Message extends AbstractAuditingEntity<Message, Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    /**
     * Actual direction as a numerical representation of {@link Direction}
     */
    @Column(nullable = false, updatable = false)
    private Integer direction = Direction.UNKNOWN.getId();

    @Lob
    @Column(columnDefinition = "CLOB", updatable = false)
    private String payload;

    @NotEmpty
    @Column(unique = true, nullable = false, updatable = false)
    private String citrusMessageId;

    @OrderBy("name ASC")
    @JsonIgnoreProperties(value = { "message" }, allowSetters = true)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<MessageHeader> headers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"scenarioParameters", "scenarioActions", "scenarioMessages"}, allowSetters = true)
    private ScenarioExecution scenarioExecution;

    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    public Long getMessageId() {
        return messageId;
    }

    void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Direction getDirection() {
        return Direction.fromId(direction);
    }

    public String getPayload() {
        return payload;
    }

    public String getCitrusMessageId() {
        return citrusMessageId;
    }

    public void addHeader(MessageHeader messageHeader) {
        headers.add(messageHeader);
        messageHeader.setMessage(this);
    }

    public void removeHeader(MessageHeader messageHeader) {
        headers.remove(messageHeader);
        messageHeader.setMessage(null);
    }

    public Collection<MessageHeader> getHeaders() {
        return headers;
    }

    public ScenarioExecution getScenarioExecution() {
        return scenarioExecution;
    }

    public void setScenarioExecution(ScenarioExecution scenarioExecution) {
        this.scenarioExecution = scenarioExecution;
    }

    public Long getScenarioExecutionId() {
        if (scenarioExecution != null) {
            return scenarioExecution.getExecutionId();
        }
        return null;
    }

    public String getScenarioName() {
        if (scenarioExecution != null) {
            return scenarioExecution.getScenarioName();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Message message) {
            return messageId != null && messageId.equals(message.messageId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Message{" +
            "messageId='" + getMessageId() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", direction='" + getDirection() + "'" +
            ", payload='" + getPayload() + "'" +
            ", citrusMessageId='" + getCitrusMessageId() + "'" +
            "}";
    }

    public enum Direction {

        UNKNOWN(0), INBOUND(1), OUTBOUND(2);

        private final int id;

        Direction(int i) {
            this.id = i;
        }

        public int getId() {
            return id;
        }

        public static Direction fromId(int id) {
            return Arrays.stream(values())
                .filter(direction -> direction.id == id)
                .findFirst()
                .orElse(Direction.UNKNOWN);
        }
    }

    public static class MessageBuilder extends AuditingEntityBuilder<MessageBuilder, Message, Long> {

        private final Message message = new Message();

        private MessageBuilder() {
            // Static access through entity
        }

        @Override
        protected Message getEntity() {
            return message;
        }

        public MessageBuilder messageId(Long messageId) {
            message.messageId = messageId;
            return this;
        }

        public MessageBuilder direction(Direction direction) {
            message.direction = direction.getId();
            return this;
        }

        public MessageBuilder payload(String payload) {
            message.payload = payload;
            return this;
        }

        public MessageBuilder citrusMessageId(String citrusMessageId) {
            message.citrusMessageId = citrusMessageId;
            return this;
        }

        public MessageBuilder headers(Map<String, Object> headers) {
            headers.entrySet().stream()
                .map(header -> {
                    if (header.getValue() != null
                        && StringUtils.isNotEmpty(header.getValue().toString())) {
                        return new MessageHeader(header.getKey(), header.getValue().toString());
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList()
                .forEach(message::addHeader);
            return this;
        }
    }
}
