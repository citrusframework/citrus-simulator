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

package org.citrusframework.simulator.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.citrusframework.Citrus;
import org.citrusframework.annotations.CitrusAnnotations;
import org.citrusframework.context.TestContext;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.exception.SimulatorException;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Service capable of executing test executables. The service takes care on setting up the executable before execution. Service
 * gets a list of normalized parameters which has to be translated to setters on the test executable instance before execution.
 *
 * @author Christoph Deppisch
 */
@Service
public class ScenarioExecutorService implements DisposableBean, ApplicationListener<ContextClosedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioExecutorService.class);

    private final ActivityService activityService;
    private final ApplicationContext applicationContext;
    private final Citrus citrus;

    private final ExecutorService executorService;

    public ScenarioExecutorService(ActivityService activityService, ApplicationContext applicationContext, Citrus citrus, SimulatorConfigurationProperties properties) {
        this.activityService = activityService;
        this.applicationContext = applicationContext;
        this.citrus = citrus;

        this.executorService = Executors.newFixedThreadPool(
            properties.getExecutorThreads(),
            new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("execution-svc-thread-%d")
                .build()
        );
    }

    /**
     * Starts a new scenario instance using the collection of supplied parameters.
     *
     * @param name               the name of the scenario to start
     * @param scenarioParameters the list of parameters to pass to the scenario when starting
     */
    public final Long run(String name, List<ScenarioParameter> scenarioParameters) {
        return run(applicationContext.getBean(name, SimulatorScenario.class), name, scenarioParameters);
    }

    /**
     * Starts a new scenario instance using the collection of supplied parameters.
     *
     * @param scenario           the scenario to start
     * @param name               the name of the scenario to start
     * @param scenarioParameters the list of parameters to pass to the scenario when starting
     */
    public final Long run(SimulatorScenario scenario, String name, List<ScenarioParameter> scenarioParameters) {
        logger.info(String.format("Starting scenario : %s", name));

        ScenarioExecution scenarioExecution = activityService.createExecutionScenario(name, scenarioParameters);

        prepareBeforeExecution(scenario);

        startScenarioAsync(scenarioExecution.getExecutionId(), name, scenario, scenarioParameters);

        return scenarioExecution.getExecutionId();
    }

    private Future<?> startScenarioAsync(Long executionId, String name, SimulatorScenario scenario, List<ScenarioParameter> scenarioParameters) {
        return executorService.submit(() -> {
            try {
                TestContext context = citrus.getCitrusContext().createTestContext();
                ReflectionUtils.doWithMethods(scenario.getClass(), method -> {
                    if (method.getDeclaringClass().equals(SimulatorScenario.class)) {
                        // no need to execute the default run implementations
                        return;
                    }

                    if (method.getParameterCount() != 1) {
                        throw new SimulatorException("Invalid scenario method signature - expect single method parameter but got: " + method.getParameterCount());
                    }

                    Class<?> parameterType = method.getParameterTypes()[0];
                    if (parameterType.equals(ScenarioRunner.class)) {
                        ScenarioRunner runner = new ScenarioRunner(scenario.getScenarioEndpoint(), applicationContext, context);
                        if (scenarioParameters != null) {
                            scenarioParameters.forEach(p -> runner.variable(p.getName(), p.getValue()));
                        }

                        runner.variable(ScenarioExecution.EXECUTION_ID, executionId);
                        runner.name(String.format("Scenario(%s)", name));

                        CitrusAnnotations.injectAll(scenario, citrus, context);

                        try {
                            runner.start();
                            ReflectionUtils.invokeMethod(method, scenario, runner);
                        } finally {
                            runner.stop();
                        }
                    } else {
                        throw new SimulatorException("Invalid scenario method parameter type: " + parameterType);
                    }
                }, method -> method.getName().equals("run"));
                logger.debug(String.format("Scenario completed: '%s'", name));
            } catch (Exception e) {
                logger.error(String.format("Scenario completed with error: '%s'", name), e);
            }
        });
    }

    /**
     * Prepare scenario instance before execution. Subclasses can add custom preparation steps in here.
     *
     * @param scenario
     */
    protected void prepareBeforeExecution(SimulatorScenario scenario) {
    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdownNow();
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        executorService.shutdownNow();
    }
}
