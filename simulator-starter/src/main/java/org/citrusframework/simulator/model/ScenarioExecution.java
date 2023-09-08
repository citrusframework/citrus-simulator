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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * JPA entity for representing a scenario execution
 */
@Entity
public class ScenarioExecution implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    public static final String EXECUTION_ID = "scenarioExecutionId";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long executionId;

    @Column(nullable = false)
    private Instant startDate;

    private Instant endDate;

    @Column(nullable = false)
    private String scenarioName;

    @Column(nullable = false)
    private Status status;

    @Column(length = 1000)
    private String errorMessage;

    @OrderBy("name ASC")
    @OneToMany(mappedBy = "scenarioExecution", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "scenarioExecution" }, allowSetters = true)
    private List<ScenarioParameter> scenarioParameters = new ArrayList<>();

    @OrderBy("actionId ASC")
    @OneToMany(mappedBy = "scenarioExecution", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScenarioAction> scenarioActions = new ArrayList<>();

    @OrderBy("messageId ASC")
    @OneToMany(mappedBy = "scenarioExecution", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "headers", "scenarioExecution" }, allowSetters = true)
    private List<Message> scenarioMessages = new ArrayList<>();

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
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

    public void setErrorMessage(String errorMessage) throws ErrorMessageTruncationException {
        this.errorMessage = errorMessage;
        if(StringUtils.hasLength(this.errorMessage)) {
            try {
                int size = getClass().getDeclaredField("errorMessage").getAnnotation(Column.class).length();
                int inLength = this.errorMessage.length();
                if (inLength > size) {
                    this.errorMessage = this.errorMessage.substring(0, size);
                }
            } catch (SecurityException | NoSuchFieldException ex) {
                throw new ErrorMessageTruncationException(String.format("Error truncating error message '%s'!", errorMessage), ex);
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
                "executionId='" + getExecutionId() + "'" +
                ", startDate='" + getStartDate() + "'" +
                ", endDate='" + getEndDate() + "'" +
                ", scenarioName='" + getScenarioName() + "'" +
                ", status='" + getStatus() + "'" +
                ", errorMessage='" + getErrorMessage() + "'" +
                ", scenarioParameters='" + getScenarioParameters() + "'" +
                ", scenarioActions='" + getScenarioActions() + "'" +
                ", scenarioMessages='" + getScenarioMessages() + "'" +
                '}';
    }

    public enum Status {
        ACTIVE,
        SUCCESS,
        FAILED
    }

    public class ErrorMessageTruncationException extends Throwable {
        public ErrorMessageTruncationException(String errorMessage, Exception exception) {
            super(errorMessage, exception);
        }
    }
}
