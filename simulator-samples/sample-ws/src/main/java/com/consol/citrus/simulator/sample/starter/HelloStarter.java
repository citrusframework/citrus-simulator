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
@Starter("HelloStarter")
public class HelloStarter extends AbstractScenarioStarter {

    @Override
    protected void configure() {
        echo("${title} ${firstname} ${lastname} ");
        echo("${greeting}");
    }

    @Override
    public Collection<TestParameter> getScenarioParameters() { // TODO rename
        List<TestParameter> testParameters = new ArrayList<>();

        // title (dropdown)
        testParameters.add(new TestParameterBuilder()
                .name("title")
                .label("Title")
                .required()
                .dropdown()
                .addOption("Mr", "Mr.")
                .addOption("Mrs", "Mrs.")
                .addOption("Miss", "Miss")
                .value("Miss")
                .build());

        // firstname (text box)
        testParameters.add(new TestParameterBuilder()
                .name("firstname")
                .label("First Name")
                .required()
                .textbox()
                .value("Mickey")
                .build());

        // lastname (text box)
        testParameters.add(new TestParameterBuilder()
                .name("lastname")
                .label("Last Name")
                .required()
                .textbox()
                .value("Mouse")
                .build());


        // greeting (text area)
        testParameters.add(new TestParameterBuilder()
                .name("greeting")
                .label("Greeting")
                .required()
                .textarea()
                .value("Hey there Mini")
                .build());

        return testParameters;
    }
}
