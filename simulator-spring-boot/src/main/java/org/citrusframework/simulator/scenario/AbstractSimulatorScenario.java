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

import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import org.citrusframework.TestCaseRunner;
import org.citrusframework.simulator.correlation.CorrelationHandler;
import org.citrusframework.simulator.correlation.CorrelationHandlerBuilder;

public abstract class AbstractSimulatorScenario implements CorrelationHandler, SimulatorScenario {

    private ScenarioEndpoint scenarioEndpoint;

    private @Nullable TestCaseRunner testCaseRunner;

    @PostConstruct
    public void init() {
        scenarioEndpoint = new ScenarioEndpoint(new ScenarioEndpointConfiguration());
    }

    @Override
    public ScenarioEndpoint getScenarioEndpoint() {
        return scenarioEndpoint;
    }

    @Override
    public @Nullable TestCaseRunner getTestCaseRunner() {
        return testCaseRunner;
    }

    @Override
    public void setTestCaseRunner(TestCaseRunner testCaseRunner) {
        this.testCaseRunner = testCaseRunner;
    }

    /**
     * Start new message correlation so scenario is provided with additional inbound messages.
     */
    public CorrelationHandlerBuilder correlation() {
        return new CorrelationHandlerBuilder(scenarioEndpoint);
    }
}
