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

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * JPA entity for representing a scenario parameter
 */
@Entity
public class ScenarioParameter extends AbstractAuditingEntity<ScenarioParameter, Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parameterId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private ControlType controlType;

    @Lob
    @Column(columnDefinition = "CLOB", name = "`value`")
    private String value;

    @ManyToOne
    @JsonIgnoreProperties(value = { "scenarioParameters", "scenarioActions", "scenarioMessages" }, allowSetters = true)
    private ScenarioExecution scenarioExecution;

    private boolean required;

    private String label;

    @Transient
    private List<ScenarioParameterOption> options;

    public Long getParameterId() {
        return parameterId;
    }

    public void setParameterId(Long parameterId) {
        this.parameterId = parameterId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<ScenarioParameterOption> getOptions() {
        return options;
    }

    public void setOptions(List<ScenarioParameterOption> options) {
        this.options = options;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ControlType getControlType() {
        return controlType;
    }

    public void setControlType(ControlType controlType) {
        this.controlType = controlType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ScenarioExecution getScenarioExecution() {
        return scenarioExecution;
    }

    public void setScenarioExecution(ScenarioExecution scenarioExecution) {
        this.scenarioExecution = scenarioExecution;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public String toString() {
        return "ScenarioParameter{" +
                ", parameterId='" + getParameterId() +
                ", createdDate='" + getCreatedDate() +
                ", name='" + getName() + '\'' +
                ", controlType='" + getControlType() +
                ", value='" + getValue() + '\'' +
                ", options='" + getScenarioExecution() +
                ", required='" + isRequired() +
                ", label='" + getLabel() +
                ", options='" + getOptions() +
                '}';
    }

    public enum ControlType {
        TEXTBOX,
        TEXTAREA,
        DROPDOWN
    }
}
