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

import java.util.UUID;

import static org.citrusframework.simulator.sample.jms.async.variables.Variables.REFERENCE_ID_VAR;

/**
 * Helper class for the Reference ID variable.
 *
 * @author Martin Maher
 */
public class ReferenceId {
    private final String referenceId;

    public ReferenceId() {
        this(UUID.randomUUID().toString());
    }

    public ReferenceId(final String referenceId) {
        this.referenceId = referenceId;
    }

    public String getValue() {
        return referenceId;
    }

    public ScenarioParameter asScenarioParameter() {
        return new ScenarioParameterBuilder()
                .name(REFERENCE_ID_VAR)
                .label("Reference Id")
                .required()
                .textbox()
                .value(referenceId)
                .build();
    }

    @Override
    public String toString() {
        return "ReferenceId{" +
                "referenceId='" + referenceId + '\'' +
                '}';
    }
}
