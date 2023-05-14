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

package org.citrusframework.simulator.sample.scenario;

import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioDesigner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.citrusframework.actions.EchoAction.Builder.echo;
import static org.citrusframework.validation.xml.XpathPayloadVariableExtractor.Builder.fromXpath;

/**
 * @author Christoph Deppisch
 */
@Scenario("Hello")
@RequestMapping(value = "/services/rest/simulator/hello", method = RequestMethod.POST)
public class HelloScenario extends AbstractSimulatorScenario {

    @Override
    public void run(ScenarioDesigner scenario) {
        scenario.run(echo("Simulator: ${simulator.name}"));

        scenario
            .receive()
            .getMessageBuilderSupport()
            .body("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                    "Say Hello!" +
                    "</Hello>")
            .extract(
                fromXpath()
                    .expression("//hello:Hello", "greeting")
            );

        scenario.run(echo("Received greeting: ${greeting}"));

        scenario
            .send()
            .getMessageBuilderSupport()
            .body("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                    "Hi there!" +
                    "</HelloResponse>");
    }
}
