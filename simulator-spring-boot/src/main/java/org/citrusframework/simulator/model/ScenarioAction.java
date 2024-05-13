/*
 * Copyright the original author or authors.
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
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import static lombok.AccessLevel.NONE;

/**
 * JPA entity for representing a scenario action
 */
@Getter
@Setter
@Entity
@Table(
    name = "scenario_action",
    indexes = {
        @Index(name = "idx_scenario_action_scenario_execution_execution_id", columnList = "scenario_execution_execution_id")
    }
)
@ToString
public class ScenarioAction implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    @Id
    @Setter(NONE)
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actionId;

    @NotEmpty
    @Column(nullable = false, updatable = false)
    private String name;

    @Column(nullable = false, updatable = false)
    private Instant startDate;

    private Instant endDate;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnoreProperties(value = { "scenarioParameters", "scenarioActions", "scenarioMessages" }, allowSetters = true)
    private ScenarioExecution scenarioExecution;

    public static ScenarioActionBuilder builder(){
        return new ScenarioActionBuilder();
    }

    void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof ScenarioAction scenarioAction) {
            return actionId != null && actionId.equals(scenarioAction.actionId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    public static class ScenarioActionBuilder {

        private final ScenarioAction scenarioAction = new ScenarioAction();

        public ScenarioActionBuilder name(String name) {
            scenarioAction.name = name;
            return this;
        }

        public ScenarioActionBuilder startDate(Instant startDate) {
            scenarioAction.startDate = startDate;
            return this;
        }

        public ScenarioActionBuilder endDate(Instant endDate) {
            scenarioAction.endDate = endDate;
            return this;
        }

        public ScenarioAction build(){
            return scenarioAction;
        }
    }
}
