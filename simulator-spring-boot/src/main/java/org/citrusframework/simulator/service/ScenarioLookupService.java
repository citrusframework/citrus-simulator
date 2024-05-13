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

import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioStarter;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.scenario.Starter;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.Set;

/**
 * Service for looking-up and accessing {@link Scenario}'s and {@link Starter}'s.
 */
public interface ScenarioLookupService {

    /**
     * Reloads the {@link SimulatorScenario} and {@link ScenarioStarter} from the current
     * {@link ApplicationContext}.
     * <p>
     * Note that this method is expected to publish a
     * {@link org.citrusframework.simulator.events.ScenariosReloadedEvent} to the
     * {@link ApplicationContext} after successful reload.
     */
    void evictAndReloadScenarioCache();

    /**
     * Returns a list containing the names of all scenarios.
     *
     * @return all scenario names
     */
    Set<String> getScenarioNames();

    /**
     * Returns a list containing the names of all starters
     *
     * @return all starter names
     */
    Set<String> getStarterNames();

    /**
     * Returns the list of parameters that the scenario can be passed when started
     *
     * @param scenarioName the name of the {@link ScenarioStarter}
     * @return the {@link ScenarioParameter}'s of the {@link ScenarioStarter}
     */
    Collection<ScenarioParameter> lookupScenarioParameters(String scenarioName);
}
