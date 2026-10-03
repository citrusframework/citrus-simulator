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
import org.citrusframework.context.TestContext;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.scenario.SimulatorScenario;

import java.util.List;
import java.util.function.Consumer;

/**
 * Service capable of executing test executables. It takes care on setting up the executable before execution. The given
 * list of normalized parameters has to be translated to setters on the test executable instance before execution.
 * <p>
 * Careful, this service is not to be confused with the {@link ScenarioExecutionService}. That is the "CRUD Service"
 * for {@link org.citrusframework.simulator.model.ScenarioExecution} and has nothing to do with the
 * actual {@link SimulatorScenario} execution.
 */
public interface ScenarioExecutorService  {

    /**
     * Starts a new scenario instance using the collection of supplied parameters. The {@link SimulatorScenario} will
     * be constructed based on the given {@code name}.
     *
     * @param name               the name of the scenario to start
     * @param scenarioParameters the list of parameters to pass to the scenario when starting
     * @return the scenario execution id
     */
    Long run(String name, @Nullable List<ScenarioParameter> scenarioParameters);

    /**
     * Starts a new scenario instance using the collection of supplied parameters.
     *
     * @param scenario           the scenario to start
     * @param name               the name of the scenario to start
     * @param scenarioParameters the list of parameters to pass to the scenario when starting
     * @return the scenario execution id
     */
    Long run(SimulatorScenario scenario, String name, @Nullable List<ScenarioParameter> scenarioParameters);

    /**
     * Starts a new scenario instance like {@link #run(SimulatorScenario, String, List)}, but lets the caller initialize
     * the {@link TestContext} of the execution before the scenario runs. Used to hand inbound requests to synchronous
     * executions, see {@link #isSynchronous()}.
     *
     * @param scenario               the scenario to start
     * @param name                   the name of the scenario to start
     * @param scenarioParameters     the list of parameters to pass to the scenario when starting
     * @param testContextInitializer invoked with the test context of the execution, before the scenario runs
     * @return the scenario execution id
     * @throws UnsupportedOperationException if this executor does not support test context initialization
     */
    default Long run(SimulatorScenario scenario, String name, @Nullable List<ScenarioParameter> scenarioParameters, Consumer<TestContext> testContextInitializer) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support test context initialization");
    }

    /**
     * Indicates whether {@link #run} only returns once the scenario has completed. Callers may then rely on any
     * response being available as soon as {@link #run} returns, instead of waiting for it.
     * <p>
     * Implementations returning {@code true} must support
     * {@link #run(SimulatorScenario, String, List, Consumer) test context initialization}.
     *
     * @return {@code true} if scenarios are executed on the calling thread, {@code false} otherwise
     */
    default boolean isSynchronous() {
        return false;
    }
}
