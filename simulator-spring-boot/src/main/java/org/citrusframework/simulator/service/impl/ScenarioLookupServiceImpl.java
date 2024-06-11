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

package org.citrusframework.simulator.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.citrusframework.simulator.events.ScenariosReloadedEvent;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioStarter;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.scenario.Starter;
import org.citrusframework.simulator.service.ScenarioLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Service for looking-up and accessing {@link Scenario}'s and {@link Starter}'s.
 */
@Service
public class ScenarioLookupServiceImpl implements InitializingBean, ScenarioLookupService {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioLookupServiceImpl.class);

    private final ApplicationContext applicationContext;

    /**
     * List of available scenarios
     */
    private Map<String, SimulatorScenario> scenarios;

    /**
     * List of available scenario starters
     */
    private Map<String, ScenarioStarter> scenarioStarters;

    private boolean scenarioListsInvalidated;

    public ScenarioLookupServiceImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private static Map<String, SimulatorScenario> getSimulatorScenarios(ApplicationContext context) {
        return context.getBeansOfType(SimulatorScenario.class).entrySet().stream()
            .filter(map -> !map.getValue().getClass().isAnnotationPresent(Starter.class))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Map<String, ScenarioStarter> getScenarioStarters(ApplicationContext context) {
        return context.getBeansOfType(ScenarioStarter.class).entrySet().stream()
            .filter(map -> map.getValue().getClass().isAnnotationPresent(Starter.class))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void afterPropertiesSet() {
        evictAndReloadScenarioCache();

        logger.info("Simulator initialized with Scenarios: {}", getScenarioNames());
        logger.info("Simulator initialized with Starters : {}", getStarterNames());
    }

    /**
     * Reloads the {@link SimulatorScenario} and {@link ScenarioStarter} from the current {@link ApplicationContext}
     */
    @Override
    public synchronized void evictAndReloadScenarioCache() {
        scenarios = getSimulatorScenarios(applicationContext);
        logger.debug("Scenarios found: {}", getScenarioNames());

        scenarioStarters = getScenarioStarters(applicationContext);
        logger.debug("Starters found: {}", getStarterNames());

        applicationContext.publishEvent(new ScenariosReloadedEvent(this));
    }

    /**
     * Returns a list containing the names of all scenarios.
     *
     * @return all scenario names
     */
    @Override
    public Set<String> getScenarioNames() {
        return scenarios.keySet();
    }

    /**
     * Returns a list containing the names of all starters
     *
     * @return all starter names
     */
    @Override
    public Set<String> getStarterNames() {
        return scenarioStarters.keySet();
    }

    /**
     * Returns the list of parameters that the scenario can be passed when started
     *
     * @param scenarioName the name of the {@link ScenarioStarter}
     * @return the {@link ScenarioParameter}'s of the {@link ScenarioStarter}
     */
    @Override
    public Collection<ScenarioParameter> lookupScenarioParameters(String scenarioName) {
        if (scenarioStarters.containsKey(scenarioName)) {
            return scenarioStarters.get(scenarioName).getScenarioParameters();
        }

        return Collections.emptyList();
    }

}
