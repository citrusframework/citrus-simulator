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

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * JPA entity for representing a scenario parameter
 */
@Entity
public class ScenarioParameter implements Serializable {
    public enum ControlType {
        TEXTBOX,
        TEXTAREA,
        DROPDOWN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PARAMETER_ID")
    private Long parameterId;

    @JsonIgnore
    @ManyToOne
    private ScenarioExecution scenarioExecution;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private ControlType controlType;

    @Column(columnDefinition = "CLOB")
    @Lob
    private String value;
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

    public ScenarioExecution getScenarioExecution() {
        return scenarioExecution;
    }

    public void setScenarioExecution(ScenarioExecution scenarioExecution) {
        this.scenarioExecution = scenarioExecution;
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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public String toString() {
        return "ScenarioParameter{" +
                "controlType=" + controlType +
                ", parameterId=" + parameterId +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", required=" + required +
                ", label='" + label + '\'' +
                ", options=" + options +
                '}';
    }
}
