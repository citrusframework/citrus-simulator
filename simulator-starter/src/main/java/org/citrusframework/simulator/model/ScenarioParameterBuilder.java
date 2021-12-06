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

import java.util.ArrayList;
import java.util.List;

public class ScenarioParameterBuilder {
    private ScenarioParameter.ControlType controlType = ScenarioParameter.ControlType.TEXTBOX;
    private String label;
    private String name;
    private List<ScenarioParameterOption> options = new ArrayList<>();
    private boolean required = true;
    private String value = "";

    public ScenarioParameterBuilder textbox() {
        this.controlType = ScenarioParameter.ControlType.TEXTBOX;
        return this;
    }

    public ScenarioParameterBuilder dropdown() {
        this.controlType = ScenarioParameter.ControlType.DROPDOWN;
        return this;
    }

    public ScenarioParameterBuilder textarea() {
        this.controlType = ScenarioParameter.ControlType.TEXTAREA;
        return this;
    }

    public ScenarioParameterBuilder label(String label) {
        this.label = label;
        return this;
    }

    public ScenarioParameterBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ScenarioParameterBuilder addOption(String key, String value) {
        this.options.add(new ScenarioParameterOption(key, value));
        return this;
    }

    public ScenarioParameterBuilder required() {
        this.required = true;
        return this;
    }

    public ScenarioParameterBuilder optional() {
        this.required = false;
        return this;
    }

    public ScenarioParameterBuilder value(String value) {
        this.value = value;
        return this;
    }

    public ScenarioParameter build() {
        ScenarioParameter tp = new ScenarioParameter();
        tp.setControlType(controlType);
        tp.setLabel(label);
        tp.setName(name);
        tp.setOptions(options);
        tp.setRequired(required);
        tp.setValue(value);
        return tp;
    }
}
