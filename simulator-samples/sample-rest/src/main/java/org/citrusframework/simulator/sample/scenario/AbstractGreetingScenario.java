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
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.springframework.http.HttpStatus;

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractGreetingScenario extends AbstractSimulatorScenario {

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
                    .payload(String.format("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">%s</HelloResponse>",
                            getGreetingMessage())))
            );
    }

    abstract String getGreetingMessage();
}
