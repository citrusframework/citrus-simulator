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
import jakarta.persistence.ManyToOne;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * JPA entity for representing a scenario action
 */
@Entity
public class ScenarioAction implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actionId;

    @Column(nullable = false)
    private String name;

    private Instant startDate;

    private Instant endDate;

    @ManyToOne
    @JsonIgnoreProperties(value = { "scenarioParameters", "scenarioActions", "scenarioMessages" }, allowSetters = true)
    private ScenarioExecution scenarioExecution;

    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public ScenarioExecution getScenarioExecution() {
        return scenarioExecution;
    }

    public void setScenarioExecution(ScenarioExecution scenarioExecution) {
        this.scenarioExecution = scenarioExecution;
    }

    @Override
    public String toString() {
        return "ScenarioAction{" +
                "actionId='" + getActionId() + "'" +
                ", name='" + getName() + "'" +
                ", startDate='" + getStartDate() + "'" +
                ", endDate='" + getEndDate() + "'" +
                ", scenarioExecution='" + getScenarioExecution() + "'" +
                '}';
    }
}
