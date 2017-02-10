/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.simulator.sample.starter;

import com.consol.citrus.simulator.model.TestParameter;
import com.consol.citrus.simulator.model.TestParameterBuilder;
import com.consol.citrus.simulator.scenario.AbstractScenarioStarter;
import com.consol.citrus.simulator.scenario.Starter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Starter("GoodByeStarter")
public class GoodByeStarter extends AbstractScenarioStarter {

    @Override
    protected void configure() {
        echo("GoodBye starter was executed!");
        echo("${payload}");
    }

    @Override
    public Collection<TestParameter> getScenarioParameters() {
        List<TestParameter> testParameters = new ArrayList<>();

        // name (text box)
        testParameters.add(new TestParameterBuilder()
                .name("name")
                .label("Name")
                .required()
                .textbox()
                .value("John Doe")
                .build());

        // greeting (text area)
        testParameters.add(new TestParameterBuilder()
                .name("payload")
                .label("Payload")
                .required()
                .textarea()
                .value(getMessageTemplate("Goodbye"))
                .build());

        return testParameters;
    }

}
