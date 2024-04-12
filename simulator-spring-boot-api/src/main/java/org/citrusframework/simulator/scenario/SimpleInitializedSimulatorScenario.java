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
import org.citrusframework.TestCaseRunner;
import org.citrusframework.simulator.correlation.CorrelationHandler;

public abstract class SimpleInitializedSimulatorScenario implements CorrelationHandler, SimulatorScenarioWithEndpoint {

    private @Nullable ScenarioEndpoint scenarioEndpoint;

    private @Nullable TestCaseRunner testCaseRunner;

    @Override
    public @Nullable ScenarioEndpoint getScenarioEndpoint() {
        return scenarioEndpoint;
    }

    @Override
    public void setScenarioEndpoint(@Nullable ScenarioEndpoint scenarioEndpoint) {
        this.scenarioEndpoint = scenarioEndpoint;
    }

    @Override
    public @Nullable TestCaseRunner getTestCaseRunner() {
        return testCaseRunner;
    }

    @Override
    public void setTestCaseRunner(TestCaseRunner testCaseRunner) {
        this.testCaseRunner = testCaseRunner;
    }
}
