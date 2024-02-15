/*
 * Copyright 2006-2024 the original author or authors.
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static lombok.AccessLevel.NONE;

/**
 * JPA entity for representing a scenario execution
 */
@Getter
@Setter
@Entity
@ToString
public class ScenarioExecution implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    public static final String EXECUTION_ID = "scenarioExecutionId";

    @Id
    @Setter(NONE)
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long executionId;

    @Column(nullable = false, updatable = false)
    private Instant startDate;

    private Instant endDate;

    @NotEmpty
    @Column(nullable = false, updatable = false)
    private String scenarioName;

    /**
     * Actual status as a numerical representation of {@link Status}
     */
    @NotNull
    @Getter(NONE)
    @Setter(NONE)
    @Column(nullable = false)
    private Integer status = Status.UNKNOWN.getId();

    @Setter(NONE)
    @Size(max = 1000)
    @Column(length = 1000)
    private String errorMessage;

    @OrderBy("name ASC")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "scenarioExecution", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = {"scenarioExecution"}, allowSetters = true)
    private final Set<ScenarioParameter> scenarioParameters = new HashSet<>();

    @OrderBy("actionId ASC")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "scenarioExecution", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<ScenarioAction> scenarioActions = new HashSet<>();

    @OrderBy("messageId ASC")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "scenarioExecution", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = {"headers", "scenarioExecution"}, allowSetters = true)
    private final Set<Message> scenarioMessages = new HashSet<>();

    public static ScenarioExecutionBuilder builder() {
        return new ScenarioExecutionBuilder();
    }

    void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public Status getStatus() {
        return Status.fromId(status);
    }

    public void setStatus(Status status) {
        this.status = status.id;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = EntityUtils.truncateToColumnSize(getClass(), "errorMessage", errorMessage);
    }

    public void addScenarioParameter(ScenarioParameter scenarioParameter) {
        scenarioParameters.add(scenarioParameter);
        scenarioParameter.setScenarioExecution(this);
    }

    public void addScenarioAction(ScenarioAction scenarioAction) {
        scenarioActions.add(scenarioAction);
        scenarioAction.setScenarioExecution(this);
    }

    public void addScenarioMessage(Message scenarioMessage) {
        scenarioMessages.add(scenarioMessage);
        scenarioMessage.setScenarioExecution(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof ScenarioExecution scenarioExecution) {
            return executionId != null && executionId.equals(scenarioExecution.executionId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Getter
    public enum Status {

        UNKNOWN(0), RUNNING(1), SUCCESS(2), FAILED(3);

        private final int id;

        Status(int id) {
            this.id = id;
        }

        public static Status fromId(int id) {
            return Arrays.stream(values())
                .filter(status -> status.id == id)
                .findFirst()
                .orElse(Status.UNKNOWN);
        }
    }

    public static class ScenarioExecutionBuilder {

        private final ScenarioExecution scenarioExecution = new ScenarioExecution();

        private ScenarioExecutionBuilder() {
            // Static access through entity
        }

        public ScenarioExecution build() {
            return scenarioExecution;
        }

        public ScenarioExecutionBuilder executionId(Long executionId) {
            scenarioExecution.executionId = executionId;
            return this;
        }

        public ScenarioExecutionBuilder startDate(Instant startDate) {
            scenarioExecution.setStartDate(startDate);
            return this;
        }

        public ScenarioExecutionBuilder endDate(Instant endDate) {
            scenarioExecution.setEndDate(endDate);
            return this;
        }

        public ScenarioExecutionBuilder scenarioName(String scenarioName) {
            scenarioExecution.setScenarioName(scenarioName);
            return this;
        }

        public ScenarioExecutionBuilder status(Status status) {
            scenarioExecution.setStatus(status);
            return this;
        }

        public ScenarioExecutionBuilder errorMessage(String errorMessage) {
            scenarioExecution.setErrorMessage(errorMessage);
            return this;
        }
    }
}
