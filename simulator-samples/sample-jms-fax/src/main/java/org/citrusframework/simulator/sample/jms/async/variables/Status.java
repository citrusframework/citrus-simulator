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
import org.citrusframework.simulator.sample.jms.async.model.FaxStatusEnumType;

/**
 * Helper class for the Status variable.
 *
 * @author Martin Maher
 */
public class Status {
    private final FaxStatusEnumType status;

    public Status(final FaxStatusEnumType status) {
        this.status = status;
    }

    public FaxStatusEnumType getValue() {
        return status;
    }

    public ScenarioParameter asScenarioParameter() {
        ScenarioParameterBuilder statusParameterBuilder = new ScenarioParameterBuilder()
                .name(Variables.STATUS_VAR)
                .label("Fax Status")
                .required()
                .dropdown()
                .value(status.value());

        for (FaxStatusEnumType value : FaxStatusEnumType.values()) {
            statusParameterBuilder.addOption(value.value(), value.value());
        }
        return statusParameterBuilder.build();
    }

    @Override
    public String toString() {
        return "Status{" +
                "status='" + status + '\'' +
                '}';
    }

}
