/*
 * Copyright the original author or authors.
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
import java.util.List;

import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.scenario.AbstractScenarioStarter;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.simulator.scenario.Starter;
import org.citrusframework.simulator.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.citrusframework.actions.EchoAction.Builder.echo;

/**
 * @author Christoph Deppisch
 */
@Starter("HelloStarter")
public class HelloStarter extends AbstractScenarioStarter {

    @Autowired
    private TemplateService templateService;

    @Override
    public void run(ScenarioRunner scenario) {
        scenario.$(echo("${greeting}"));
        scenario.$(echo("${payload}"));
    }

    @Override
    public List<ScenarioParameter> getScenarioParameters() {
        List<ScenarioParameter> scenarioParameters = new ArrayList<>();

        // name (text box)
        scenarioParameters.add(ScenarioParameter.builder()
                .name("greeting")
                .textbox()
                .value("Hi there!")
                .build());

        // greeting (text area)
        scenarioParameters.add(ScenarioParameter.builder()
                .name("payload")
                .textarea()
                .value(templateService.getXmlMessageTemplate("Hello"))
                .build());

        return scenarioParameters;
    }
}
