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

package org.citrusframework.simulator.service;

import jakarta.annotation.Nullable;
import org.citrusframework.TestCaseRunner;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.simulator.scenario.SimulatorScenario;

import java.util.concurrent.atomic.AtomicBoolean;

public class ScenarioWithoutEndpoint implements SimulatorScenario {

    private @Nullable ScenarioEndpoint scenarioEndpoint;

    private @Nullable TestCaseRunner testCaseRunner;

    private final AtomicBoolean hasRun = new AtomicBoolean(false);

    @Override
    public void run(ScenarioRunner scenario) {
        hasRun.set(true);
    }

    @Override
    @Nullable
    public ScenarioEndpoint getScenarioEndpoint() {
        return scenarioEndpoint;
    }

    public void setScenarioEndpoint(@Nullable ScenarioEndpoint scenarioEndpoint) {
        this.scenarioEndpoint = scenarioEndpoint;
    }

    @Nullable
    @Override
    public TestCaseRunner getTestCaseRunner() {
        return testCaseRunner;
    }

    @Override
    public void setTestCaseRunner(@Nullable TestCaseRunner testCaseRunner) {
        this.testCaseRunner = testCaseRunner;
    }

    public AtomicBoolean hasRun() {
        return hasRun;
    }
}
