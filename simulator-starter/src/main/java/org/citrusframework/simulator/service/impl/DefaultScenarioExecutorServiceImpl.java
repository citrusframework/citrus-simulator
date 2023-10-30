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

package org.citrusframework.simulator.service.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import jakarta.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.citrusframework.Citrus;
import org.citrusframework.annotations.CitrusAnnotations;
import org.citrusframework.context.TestContext;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.exception.SimulatorException;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

/**
 * {@inheritDoc}
 */
@Service
public class DefaultScenarioExecutorServiceImpl implements ScenarioExecutorService {

    private static final Logger logger = LoggerFactory.getLogger( DefaultScenarioExecutorServiceImpl.class);

    private final ApplicationContext applicationContext;
    private final Citrus citrus;
    private final ScenarioExecutionService scenarioExecutionService;

    private final ExecutorService executorService;

    public DefaultScenarioExecutorServiceImpl(ApplicationContext applicationContext, Citrus citrus, ScenarioExecutionService scenarioExecutionService, SimulatorConfigurationProperties properties) {
        this.applicationContext = applicationContext;
        this.citrus = citrus;
        this.scenarioExecutionService = scenarioExecutionService;

        this.executorService = Executors.newFixedThreadPool(
            properties.getExecutorThreads(),
            new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("execution-svc-thread-%d")
                .build()
        );
    }

    @Override
    public void destroy() throws Exception {
        shutdownExecutor();
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        shutdownExecutor();
    }

    @Override
    public final Long run(String name, @Nullable List<ScenarioParameter> scenarioParameters) {
        return run(applicationContext.getBean(name, SimulatorScenario.class), name, scenarioParameters);
    }

    @Override
    public final Long run(SimulatorScenario scenario, String name, @Nullable List<ScenarioParameter> scenarioParameters) {
        logger.info("Starting scenario : {}", name);

        ScenarioExecution scenarioExecution = scenarioExecutionService.createAndSaveExecutionScenario(name, scenarioParameters);

        prepareBeforeExecution(scenario);

        startScenarioAsync(scenarioExecution.getExecutionId(), name, scenario, scenarioParameters);

        return scenarioExecution.getExecutionId();
    }

    private Future<?> startScenarioAsync(Long executionId, String name, SimulatorScenario scenario, List<ScenarioParameter> scenarioParameters) {
        return executorService.submit(() -> startScenarioSync(executionId, name, scenario, scenarioParameters));
    }

    private void startScenarioSync(Long executionId, String name, SimulatorScenario scenario, List<ScenarioParameter> scenarioParameters) {
        try {
            TestContext context = citrus.getCitrusContext().createTestContext();
            ReflectionUtils.doWithMethods(
                scenario.getClass(),
                method -> createAndRunScenarioRunner(context, method, executionId, name, scenario, scenarioParameters),
                method -> method.getName().equals("run")
            );
            logger.debug("Scenario completed: {}", name);
        } catch (Exception e) {
            logger.error("Scenario completed with error: {}!", name, e);
        }
    }

    private void createAndRunScenarioRunner(TestContext context, Method method, Long executionId, String name, SimulatorScenario scenario, List<ScenarioParameter> scenarioParameters) {
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
    }

    /**
     * Prepare scenario instance before execution. Subclasses can add custom preparation steps in
     * here.
     *
     * @param scenario
     */
    protected void prepareBeforeExecution(SimulatorScenario scenario) {
    }

    private void shutdownExecutor() {
        logger.debug("Request to shutdown executor");

        if (!executorService.isShutdown()) {
            logger.trace("Shutting down executor");
            executorService.shutdownNow();
        }
    }
}
