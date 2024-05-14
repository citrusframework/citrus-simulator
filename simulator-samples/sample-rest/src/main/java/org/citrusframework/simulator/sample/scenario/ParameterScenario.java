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

package org.citrusframework.simulator.sample.scenario;

import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

import static org.citrusframework.actions.EchoAction.Builder.echo;

@Scenario("Parameter")
@RequestMapping(value = "/services/rest/simulator/parameter", method = RequestMethod.GET)
public class ParameterScenario extends AbstractSimulatorScenario {

    @Override
    public void run(ScenarioRunner scenario) {
        scenario.$(scenario.http()
            .receive()
            .get()
            .getMessageBuilderSupport()
            .extract((message, testContext) -> {
                if (message instanceof HttpMessage httpMessage) {
                    testContext.addVariables(Map.of("pulver", httpMessage.getQueryParams().get("exploded")));
                }
            }));

        scenario.$(echo("Extracted dangerous parameters: ${pulver}"));

        scenario.$(scenario.http()
            .send()
            .response(HttpStatus.OK)
            .message()
            .body(
                // language=json
                """
                    {
                        "pulver": ${pulver}
                    }
                    """
            ));
    }
}
