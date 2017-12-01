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

package com.consol.citrus.simulator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * JPA entity for representing inbound and outbound messages
 */
@Entity
public class Message implements Serializable {
    private static final long serialVersionUID = -4858126051234255084L;

    public enum Direction {
        INBOUND,
        OUTBOUND
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_ID")
    private Long messageId;

    @JsonIgnore
    @ManyToOne
    private ScenarioExecution scenarioExecution;

    @Column(nullable = false)
    private Direction direction;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(columnDefinition = "CLOB")
    @Lob
    private String payload;

    @Column(unique = true)
    private String citrusMessageId;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("name ASC")
    private Collection<MessageHeader> headers = new ArrayList<>();

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getCitrusMessageId() {
        return citrusMessageId;
    }

    public void setCitrusMessageId(String citrusMessageId) {
        this.citrusMessageId = citrusMessageId;
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


    @Override
    public String toString() {
        return "Message{" +
                "date=" + date +
                ", messageId=" + messageId +
                ", direction=" + direction +
                ", payload='" + payload + '\'' +
                ", citrusMessageId=" + citrusMessageId +
                ", scenarioExecutionId=" + getScenarioExecutionId() +
                ", scenarioName=" + getScenarioName() +
                ", headers=" + headers +
                '}';
    }

}
