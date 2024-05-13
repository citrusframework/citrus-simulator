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

import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.citrusframework.actions.EchoAction.Builder.echo;

/**
 * This scenario fails at runtime, unexpectedly. It must therefore be reported as failed {@link SimulatorScenario}.
 * <p>
 * On the other hand, if a simulation fails on purpose, see {@link FailScenario}, it must be a "successful simulation".
 */
@Scenario("Throw")
@RequestMapping(value = "/services/rest/simulator/throw", method = RequestMethod.GET)
public class ThrowScenario extends AbstractSimulatorScenario {

    @Override
    public void run(ScenarioRunner scenario) {
        scenario.$(scenario.http()
            .receive()
            .get());

        scenario.$(echo("Careful - I am gonna fail (:"));

        throw new CitrusRuntimeException("Every failure is a step to success.");
    }
}
