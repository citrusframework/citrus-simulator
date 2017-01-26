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
import com.consol.citrus.simulator.model.TestParameterOption;
import com.consol.citrus.simulator.scenario.*;

import java.util.*;

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
    public String getDisplayName() {
        return "Hello";
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public List<String> getMessageTemplates() {
        return Arrays.asList(new String[] {"Hello"});
    }

    @Override
    public List<ScenarioParameter> getScenarioParameter() {
        List<ScenarioParameter> parameters = new ArrayList<>();
        parameters.add(new ScenarioParameter("greeting", "Greeting Text", "Hi there!").addScenarioFilter(HelloStarter.class));

        return parameters;
    }

    @Override
    public Collection<TestParameter> getLaunchableTestParameters() {
        List<TestParameter> testParameters = new ArrayList<>();
        TestParameter tp;
        List<TestParameterOption> tpOptions;

        // title (dropdown)
        tpOptions = new ArrayList<>();
        tpOptions.add(new TestParameterOption("Mr", "Mr."));
        tpOptions.add(new TestParameterOption("Mrs", "Mrs."));
        tpOptions.add(new TestParameterOption("Miss", "Miss"));

        tp = new TestParameter();
        tp.setName("title");
        tp.setLabel("Title");
        tp.setRequired(false);
        tp.setControlType(TestParameter.ControlType.DROPDOWN);
        tp.setValue("Miss");
        tp.setOptions(tpOptions);
        testParameters.add(tp);

        // firstname (text box)
        tp = new TestParameter();
        tp.setName("firstname");
        tp.setLabel("First Name");
        tp.setRequired(true);
        tp.setControlType(TestParameter.ControlType.TEXTBOX);
        tp.setValue("Mickey");
        testParameters.add(tp);

        // lastname (text area)
        tp = new TestParameter();
        tp.setName("lastname");
        tp.setLabel("Last Name");
        tp.setRequired(true);
        tp.setControlType(TestParameter.ControlType.TEXTBOX);
        tp.setValue("Mouse");
        testParameters.add(tp);

        // greeting (text area)
        tp = new TestParameter();
        tp.setName("greeting");
        tp.setLabel("Greeting");
        tp.setRequired(true);
        tp.setControlType(TestParameter.ControlType.TEXTAREA);
        tp.setValue("Hey there Mini");
        testParameters.add(tp);

        return testParameters;
    }
}
