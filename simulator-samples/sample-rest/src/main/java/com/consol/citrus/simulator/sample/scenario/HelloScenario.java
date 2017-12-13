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

package com.consol.citrus.simulator.sample.scenario;

import com.consol.citrus.simulator.scenario.AbstractSimulatorScenario;
import com.consol.citrus.simulator.scenario.Scenario;
import com.consol.citrus.simulator.scenario.ScenarioRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Christoph Deppisch
 */
@Scenario("Hello")
@RequestMapping(value = "/services/rest/simulator/hello", method = RequestMethod.POST)
public class HelloScenario extends AbstractSimulatorScenario {

    @Override
    public void run(ScenarioRunner scenario) {
        scenario.echo("Simulator: ${simulator.name}");

        scenario
            .http()
            .receive(builder -> builder
                    .post()
                    .payload("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say Hello!" +
                            "</Hello>")
                    .extractFromPayload("//hello:Hello", "greeting")
            );

        scenario.echo("Received greeting: ${greeting}");

        scenario
            .http()
            .send((builder -> builder
                    .response(HttpStatus.OK)
                    .payload("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Hi there!" +
                            "</HelloResponse>"))
            );
    }
}
