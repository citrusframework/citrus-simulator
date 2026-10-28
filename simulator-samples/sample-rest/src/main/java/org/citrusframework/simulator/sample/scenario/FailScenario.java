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

import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.citrusframework.actions.EchoAction.Builder.echo;
import static org.citrusframework.actions.FailAction.Builder.fail;

/**
 * This scenario fails on purpose, using the {@link org.citrusframework.actions.FailAction.Builder#fail(String)}
 * method. Like any failing test action (e.g. a failed message validation), this results in a failed
 * {@link SimulatorScenario}, answered with the custom HTTP status code 555.
 * <p>
 * See {@link ThrowScenario} for a scenario failing with an exception at runtime, which results in the same response.
 */
@Scenario("Fail")
@RequestMapping(value = "/services/rest/simulator/fail", method = RequestMethod.GET)
public class FailScenario extends AbstractSimulatorScenario {

    @Override
    public void run(ScenarioRunner scenario) {
        scenario.$(scenario.http()
            .receive()
            .get());

        scenario.$(echo("Careful - I am gonna fail successfully (:"));

        scenario.$(fail("It is the courage to continue that counts."));
    }
}
