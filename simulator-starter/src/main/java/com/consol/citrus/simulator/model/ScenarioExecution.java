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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * JPA entity for representing a scenario execution
 */
@Entity
public class ScenarioExecution implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(ScenarioExecution.class);

    public static final String EXECUTION_ID = "scenarioExecutionId";

    public enum Status {
        ACTIVE,
        SUCCESS,
        FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EXECUTION_ID")
    private Long executionId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Column(nullable = false)
    private String scenarioName;

    @Column(nullable = false)
    private Status status;

    @Column(length = 1000)
    private String errorMessage;

    @OneToMany(mappedBy = "scenarioExecution", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("name ASC")
    private List<ScenarioParameter> scenarioParameters = new ArrayList<>();

    @OneToMany(mappedBy = "scenarioExecution", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("actionId ASC")
    private List<ScenarioAction> scenarioActions = new ArrayList<>();

    @OneToMany(mappedBy = "scenarioExecution", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("messageId ASC")
    private List<Message> scenarioMessages = new ArrayList<>();

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        if(StringUtils.hasLength(this.errorMessage)) {
            try {
                int size = getClass().getDeclaredField("errorMessage").getAnnotation(Column.class).length();
                int inLength = this.errorMessage.length();
                if (inLength > size) {
                    this.errorMessage = this.errorMessage.substring(0, size);
                }
            } catch (SecurityException | NoSuchFieldException ex) {
                LOG.error(String.format("Error truncating error message", errorMessage), ex);
            }
        }
    }

    public Collection<ScenarioParameter> getScenarioParameters() {
        return scenarioParameters;
    }

    public void addScenarioParameter(ScenarioParameter scenarioParameter) {
        scenarioParameters.add(scenarioParameter);
        scenarioParameter.setScenarioExecution(this);
    }

    public void removeScenarioParameter(ScenarioParameter scenarioParameter) {
        scenarioParameters.remove(scenarioParameter);
        scenarioParameter.setScenarioExecution(null);
    }

    public Collection<ScenarioAction> getScenarioActions() {
        return scenarioActions;
    }

    public void addScenarioAction(ScenarioAction scenarioAction) {
        scenarioActions.add(scenarioAction);
        scenarioAction.setScenarioExecution(this);
    }

    public void removeScenarioAction(ScenarioAction scenarioAction) {
        scenarioActions.remove(scenarioAction);
        scenarioAction.setScenarioExecution(null);
    }

    public Collection<Message> getScenarioMessages() {
        return scenarioMessages;
    }

    public void addScenarioMessage(Message scenarioMessage) {
        scenarioMessages.add(scenarioMessage);
        scenarioMessage.setScenarioExecution(this);
    }

    public void removeScenarioMessage(Message scenarioMessage) {
        scenarioMessages.remove(scenarioMessage);
        scenarioMessage.setScenarioExecution(null);
    }

    @Override
    public String toString() {
        return "ScenarioExecution{" +
                "endDate=" + endDate +
                ", executionId=" + executionId +
                ", startDate=" + startDate +
                ", scenarioName='" + scenarioName + '\'' +
                ", status=" + status +
                ", errorMessage='" + errorMessage + '\'' +
                ", scenarioParameters=" + scenarioParameters +
                ", scenarioActions=" + scenarioActions +
                ", scenarioMessages=" + scenarioMessages +
                '}';
    }
}
