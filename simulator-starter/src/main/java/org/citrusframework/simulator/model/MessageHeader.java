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
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * JPA entity for representing message headers
 *
 * @author Georgi Todorov
 */
@Entity
public class MessageHeader extends AbstractAuditingEntity<MessageHeader, Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long headerId;

    @NotEmpty
    @Column(nullable = false, updatable = false)
    private String name;

    @NotEmpty
    @Column(name = "header_value", nullable = false, updatable = false)
    private String value;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @JsonIgnoreProperties(value = {"headers", "scenarioExecution"}, allowSetters = true)
    private Message message;

    public MessageHeader() {
        // Hibernate constructor
    }

    MessageHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static MessageHeaderBuilder builder() {
        return new MessageHeaderBuilder();
    }

    public Long getHeaderId() {
        return headerId;
    }

    void setHeaderId(Long headerId) {
        this.headerId = headerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof MessageHeader messageHeader) {
            return headerId != null && headerId.equals(messageHeader.headerId);
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
        return "MessageHeader{" +
            "headerId='" + getHeaderId() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", name='" + getName() + "'" +
            ", value='" + getValue() + "'" +
            "}";
    }

    public static class MessageHeaderBuilder extends AuditingEntityBuilder<MessageHeaderBuilder, MessageHeader, Long> {

        private final MessageHeader messageHeader = new MessageHeader();

        public MessageHeaderBuilder name(String name) {
            messageHeader.setName(name);
            return this;
        }

        public MessageHeaderBuilder value(String value) {
            messageHeader.setValue(value);
            return this;
        }

        public MessageHeaderBuilder message(Message message) {
            messageHeader.setMessage(message);
            return this;
        }

        @Override
        protected MessageHeader getEntity() {
            return messageHeader;
        }
    }
}
