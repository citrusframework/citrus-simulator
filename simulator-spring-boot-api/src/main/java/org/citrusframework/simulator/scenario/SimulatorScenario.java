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

package org.citrusframework.simulator.scenario;import jakarta.annotation.Nullable;
import org.citrusframework.DefaultTestCaseRunner;
import org.citrusframework.TestCaseRunner;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.simulator.exception.SimulatorException;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.citrusframework.simulator.scenario.ScenarioUtils.getAnnotationFromClassHierarchy;

public interface SimulatorScenario {

    /**
     * Gets the scenario endpoint explicitly set to handle messages for this scenario.
     */
    ScenarioEndpoint getScenarioEndpoint();

    /**
     * Default starter body method with provided scenario runner.
     */
    default void run(ScenarioRunner runner) {
    }

    default String getName() {
        return getNameFromScenarioAnnotation();
    }

    @Nullable
    TestCaseRunner getTestCaseRunner();

    void setTestCaseRunner(TestCaseRunner testCaseRunner);

    default Void registerException(Throwable e) {
        if (nonNull(getTestCaseRunner()) && getTestCaseRunner() instanceof DefaultTestCaseRunner defaultTestCaseRunner) {
            defaultTestCaseRunner.getContext().addException(new CitrusRuntimeException(e));
        }

        getScenarioEndpoint().fail(e);

        return null;
    }

    /**
     * Retrieves the name of a scenario from its {@link Scenario} annotation.
     *
     * @return the name of the scenario as specified by the {@link Scenario} annotation's value.
     * @throws SimulatorException if the {@link Scenario} annotation is not found on this scenario, its proxied objects or superclasses.
     */
    private String getNameFromScenarioAnnotation() {
        Scenario scenarioAnnotation = getAnnotationFromClassHierarchy(this, Scenario.class);

        if (scenarioAnnotation == null) {
            throw new SimulatorException(
                format("Missing scenario annotation at class: %s - even searched class hierarchy!", getClass())
            );
        }

        return scenarioAnnotation.value();
    }
}
