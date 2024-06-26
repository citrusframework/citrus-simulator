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

package org.citrusframework.simulator.http;

import org.citrusframework.http.actions.HttpServerResponseActionBuilder;
import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.simulator.scenario.SimulatorScenario;

/**
 * Interface for providing an {@link HttpServerResponseActionBuilder} based on a {@link SimulatorScenario} and a received message.
 */
public interface HttpResponseActionBuilderProvider {

    HttpServerResponseActionBuilder provideHttpServerResponseActionBuilder(
        ScenarioRunner scenarioRunner, SimulatorScenario simulatorScenario, HttpMessage receivedMessage);

}
