/*
 * Copyright 2006-2024 the original author or authors.
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

import org.citrusframework.exceptions.CitrusRuntimeException;

import static java.lang.String.format;
import static org.citrusframework.simulator.scenario.ScenarioUtils.getAnnotationFromClassHierarchy;

/**
 * @author Christoph Deppisch
 */
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

    /**
     * Retrieves the name of a scenario from its {@link Scenario} annotation.
     *
     * @return the name of the scenario as specified by the {@link Scenario} annotation's value.
     * @throws CitrusRuntimeException if the {@link Scenario} annotation is not found on this
     *                                scenario, its proxied objects or superclasses.
     */
    private String getNameFromScenarioAnnotation() {
        Scenario scenarioAnnotation = getAnnotationFromClassHierarchy(this, Scenario.class);

        if (scenarioAnnotation == null) {
            throw new CitrusRuntimeException(
                format("Missing scenario annotation at class: %s - even searched class hierarchy", getClass())
            );
        }
        return scenarioAnnotation.value();
    }
}
