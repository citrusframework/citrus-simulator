/*
 * Copyright 2023 the original author or authors.
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
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.List;

/**
 * Service capable of executing test executables. The service takes care on setting up the
 * executable before execution. Service gets a list of normalized parameters which has to be
 * translated to setters on the test executable instance before execution.
 * <p>
 * Careful, this service is not to be confused with the {@link ScenarioExecutionService}. That is
 * the "CRUD Service" for {@link org.citrusframework.simulator.model.ScenarioExecution} and has
 * nothing to do with actual {@link SimulatorScenario} execution.
 */
public interface ScenarioExecutorService extends DisposableBean, ApplicationListener<ContextClosedEvent> {

    /**
     * Starts a new scenario instance using the collection of supplied parameters.
     *
     * @param name               the name of the scenario to start
     * @param scenarioParameters the list of parameters to pass to the scenario when starting
     * @return the scenario execution id
     */
    public Long run(String name, @Nullable List<ScenarioParameter> scenarioParameters);

    /**
     * Starts a new scenario instance using the collection of supplied parameters.
     *
     * @param scenario           the scenario to start
     * @param name               the name of the scenario to start
     * @param scenarioParameters the list of parameters to pass to the scenario when starting
     * @return the scenario execution id
     */
    public Long run(SimulatorScenario scenario, String name, @Nullable List<ScenarioParameter> scenarioParameters);
}
