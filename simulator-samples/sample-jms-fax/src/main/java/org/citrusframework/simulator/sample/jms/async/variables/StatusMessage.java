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

package org.citrusframework.simulator.sample.jms.async.variables;

import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.model.ScenarioParameterBuilder;

import static org.citrusframework.simulator.sample.jms.async.variables.Variables.STATUS_MESSAGE_VAR;

/**
 * Helper class for the StatusMessage variable.
 *
 * @author Martin Maher
 */
public class StatusMessage {
    private final String statusMessage;

    public StatusMessage(final String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getValue() {
        return statusMessage;
    }

    public ScenarioParameter asScenarioParameter() {
        ScenarioParameterBuilder statusMessageParameterBuilder = new ScenarioParameterBuilder()
                .name(STATUS_MESSAGE_VAR)
                .label("Status Message")
                .optional()
                .textbox()
                .value(statusMessage);
        return statusMessageParameterBuilder.build();
    }

    @Override
    public String toString() {
        return "Status{" +
                "statusMessage='" + statusMessage + '\'' +
                '}';
    }

}
