/*
 * Copyright 2006-2017 the original author or authors.
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

package com.consol.citrus.simulator.service;

import com.consol.citrus.simulator.model.ScenarioParameter;

import java.util.Collection;
import java.util.List;

/**
 * Service interface capable of executing test executables. The service takes care on setting up the executable before execution. Service
 * gets a list of normalized parameters which has to be translated to setters on the test executable instance before execution.
 *
 * @author Christoph Deppisch
 */
public interface ScenarioService {

    /**
     * Starts a new scenario instance using the collection of supplied parameters.
     *
     * @param name               the name of the scenario to start
     * @param scenarioParameters the list of parameters to pass to the scenario when starting
     */
    Long run(String name, List<ScenarioParameter> scenarioParameters);

    /**
     * Returns a list containing the names of all scenarios.
     *
     * @return all scenario names
     */
    Collection<String> getScenarioNames();

    /**
     * Returns a list containing the names of all starters
     *
     * @return all starter names
     */
    Collection<String> getStarterNames();

    /**
     * Returns the list of parameters that the scenario can be passed when started
     *
     * @param scenarioName
     * @return
     */
    Collection<ScenarioParameter> lookupScenarioParameters(String scenarioName);
}
