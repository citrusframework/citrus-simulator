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

package org.citrusframework.simulator.sample.starter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.scenario.AbstractScenarioStarter;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.simulator.scenario.Starter;

import static org.citrusframework.actions.EchoAction.Builder.echo;

/**
 * @author Christoph Deppisch
 */
@Starter("HelloStarter")
public class HelloStarter extends AbstractScenarioStarter {

    @Override
    public void run(ScenarioRunner scenario) {
        scenario.$(echo("${title} ${firstname} ${lastname} "));
        scenario.$(echo("${greeting}"));
    }

    @Override
    public Collection<ScenarioParameter> getScenarioParameters() {
        List<ScenarioParameter> scenarioParameter = new ArrayList<>();

        // title (dropdown)
        scenarioParameter.add(ScenarioParameter.builder()
                .name("title")
                .dropdown()
                .addOption("Mr", "Mr.")
                .addOption("Mrs", "Mrs.")
                .addOption("Miss", "Miss")
                .value("Miss")
                .build());

        // firstname (text box)
        scenarioParameter.add(ScenarioParameter.builder()
                .name("firstname")
                .textbox()
                .value("Mickey")
                .build());

        // lastname (text box)
        scenarioParameter.add(ScenarioParameter.builder()
                .name("lastname")
                .textbox()
                .value("Mouse")
                .build());


        // greeting (text area)
        scenarioParameter.add(ScenarioParameter.builder()
                .name("greeting")
                .textarea()
                .value("Hey there Mini")
                .build());

        return scenarioParameter;
    }
}
