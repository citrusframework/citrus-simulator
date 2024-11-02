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

package org.citrusframework.simulator.scenario;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.citrusframework.actions.EchoAction.Builder.echo;
import static org.citrusframework.dsl.MessageSupport.MessageBodySupport.fromBody;

@Scenario("StandaloneHelloScenario")
@RequestMapping(value = "/services/rest/simulator/hello", method = RequestMethod.POST)
public class StandaloneHelloScenario extends SimpleInitializedSimulatorScenario {

    @Override
    public void run(ScenarioRunner scenario) {
        scenario.$(scenario.http()
            .receive()
            .post()
            .message()
            .body("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                "Say Hello!" +
                "</Hello>")
            .extract(fromBody().expression("//hello:Hello", "greeting")));

        scenario.$(echo("Received greeting: ${greeting}"));

        scenario.$(scenario.http()
            .send()
            .response(HttpStatus.OK)
            .message()
            .body("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">Hi there!</HelloResponse>"));
    }
}
