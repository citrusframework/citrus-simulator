/*
 * Copyright 2024 the original author or authors.
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

package org.citrusframework.simulator.service.runner;

import jakarta.annotation.Nullable;
import org.citrusframework.Citrus;
import org.citrusframework.context.TestContext;
import org.citrusframework.exceptions.TestCaseFailedException;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.citrusframework.annotations.CitrusAnnotations.injectAll;
import static org.citrusframework.simulator.model.ScenarioExecution.EXECUTION_ID;

/**
 * Provides a default, synchronous implementation of the {@link ScenarioExecutorService} interface for executing
 * simulation scenarios within the Citrus framework. This implementation ensures that all scenarios are executed
 * in a single thread, one at a time, allowing for straightforward debugging and ensuring that data consistency
 * is maintained throughout the execution of each scenario.
 * <p>
 * The service is activated by default but can be overridden by setting the {code citrus.simulator.mode} property to a
 * different execution mode (e.g., {@code async} for {@link AsyncScenarioExecutorService}). When in synchronous mode,
 * this service ensures that all messages are processed and all data persistence operations are completed before
 * moving on to the next scenario, providing a predictable and linear execution flow.
 * <p>
 * Scenarios are started by looking up {@link SimulatorScenario} beans by their names in the Spring application context,
 * passing them a collection of {@link ScenarioParameter}s, and then executing them within the Citrus test context.
 * Custom preparation steps for scenarios can be implemented by overriding the {@code prepareBeforeExecution}
 * method.
 *
 * @see ScenarioExecutorService
 * @see AsyncScenarioExecutorService
 * @see SimulatorScenario
 */
@Service
@ConditionalOnProperty(name = "citrus.simulator.mode", havingValue = "sync", matchIfMissing = true)
public class DefaultScenarioExecutorService implements ScenarioExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultScenarioExecutorService.class);

    private final ApplicationContext applicationContext;
    private final Citrus citrus;
    private final ScenarioExecutionService scenarioExecutionService;

    public DefaultScenarioExecutorService(ApplicationContext applicationContext, Citrus citrus, ScenarioExecutionService scenarioExecutionService) {
        this.applicationContext = applicationContext;
        this.citrus = citrus;
        this.scenarioExecutionService = scenarioExecutionService;
    }

    /**
     * Starts a new scenario instance by looking up a {@link SimulatorScenario} bean by name and executing it with
     * the provided parameters. This method serves as an entry point for scenario execution, handling the entire
     * lifecycle from scenario lookup to execution completion.
     *
     * @param name               the name of the scenario to execute, used to look up the corresponding {@link SimulatorScenario} bean
     * @param scenarioParameters a list of {@link ScenarioParameter}s to pass to the scenario, may be {@code null}
     * @return the unique identifier of the scenario execution, used for tracking and management purposes
     */
    @Override
    public final Long run(String name, @Nullable List<ScenarioParameter> scenarioParameters) {
        return run(applicationContext.getBean(name, SimulatorScenario.class), name, scenarioParameters);
    }

    /**
     * Executes the given {@link SimulatorScenario} with the provided name and parameters. This method orchestrates
     * the scenario execution process, including pre-execution preparation, scenario execution, and post-execution
     * cleanup, ensuring a consistent execution environment for each scenario.
     *
     * @param scenario           the {@link SimulatorScenario} to execute
     * @param name               the name of the scenario, used for logging and tracking purposes
     * @param scenarioParameters a list of {@link ScenarioParameter}s to pass to the scenario, may be {@code null}
     * @return the unique identifier of the scenario execution
     */
    @Override
    public final Long run(SimulatorScenario scenario, String name, @Nullable List<ScenarioParameter> scenarioParameters) {
        ScenarioExecution scenarioExecution = scenarioExecutionService.createAndSaveExecutionScenario(name, scenarioParameters);

        prepareBeforeExecution(scenario);

        startScenario(scenarioExecution.getExecutionId(), name, scenario, scenarioParameters);

        return scenarioExecution.getExecutionId();
    }

    protected void startScenario(Long executionId, String name, SimulatorScenario scenario, List<ScenarioParameter> scenarioParameters) {
        logger.info("Starting scenario : {}", name);

        var context = createTestContext();
        createAndRunScenarioRunner(context, executionId, name, scenario, scenarioParameters);

        logger.debug("Scenario completed: {}", name);
    }

    /**
     * Prepare {@link SimulatorScenario} before execution. Subclasses can add custom preparation steps in here.
     *
     * @param scenario the scenario soon to be executed.
     */
    protected void prepareBeforeExecution(SimulatorScenario scenario) {
    }

    private TestContext createTestContext() {
        return citrus.getCitrusContext().createTestContext();
    }

    private void createAndRunScenarioRunner(TestContext context, Long executionId, String name, SimulatorScenario scenario, List<ScenarioParameter> scenarioParameters) {
        var runner = new ScenarioRunner(scenario.getScenarioEndpoint(), applicationContext, context);
        if (scenarioParameters != null) {
            scenarioParameters.forEach(p -> runner.variable(p.getName(), p.getValue()));
        }

        runner.variable(EXECUTION_ID, executionId);
        runner.name(format("Scenario(%s)", name));

        injectAll(scenario, citrus);

        try {
            runner.start();
            scenario.setTestCaseRunner(runner.getTestCaseRunner());
            scenario.run(runner);
        } catch (TestCaseFailedException e) {
            logger.error("Registered forced failure of scenario: {}!", name, e);
        } catch (Exception e) {
            logger.error("Scenario completed with error: {}!", name, e);
            scenario.registerException(e);
            throw e;
        } finally {
            runner.stop();
        }
    }
}
