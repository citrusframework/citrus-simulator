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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import com.consol.citrus.Citrus;
import com.consol.citrus.annotations.CitrusAnnotations;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.design.TestDesigner;
import com.consol.citrus.dsl.endpoint.Executable;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.simulator.exception.SimulatorException;
import com.consol.citrus.simulator.model.ScenarioExecution;
import com.consol.citrus.simulator.model.ScenarioParameter;
import com.consol.citrus.simulator.scenario.ScenarioDesigner;
import com.consol.citrus.simulator.scenario.ScenarioRunner;
import com.consol.citrus.simulator.scenario.SimulatorScenario;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

/**
 * Service capable of executing test executables. The service takes care on setting up the executable before execution. Service
 * gets a list of normalized parameters which has to be translated to setters on the test executable instance before execution.
 *
 * @author Christoph Deppisch
 */
@Service
public class ScenarioExecutionService implements DisposableBean, ApplicationListener<ContextClosedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(ScenarioExecutionService.class);

    private final ActivityService activityService;
    private final ApplicationContext applicationContext;
    private final Citrus citrus;

    private final ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("execution-svc-thread-%d")
            .build();

    private ExecutorService executorService = Executors.newFixedThreadPool(10, threadFactory);

    @Autowired
    public ScenarioExecutionService(final ActivityService activityService, final ApplicationContext applicationContext, final Citrus citrus) {
        this.activityService = activityService;
        this.applicationContext = applicationContext;
        this.citrus = citrus;
    }

    /**
     * Starts a new scenario instance using the collection of supplied parameters.
     *
     * @param name               the name of the scenario to start
     * @param scenarioParameters the list of parameters to pass to the scenario when starting
     */
    public final Long run(final String name, final List<ScenarioParameter> scenarioParameters) {
        return run(applicationContext.getBean(name, SimulatorScenario.class), name, scenarioParameters);
    }

    /**
     * Starts a new scenario instance using the collection of supplied parameters.
     *
     * @param scenario           the scenario to start
     * @param name               the name of the scenario to start
     * @param scenarioParameters the list of parameters to pass to the scenario when starting
     */
    public final Long run(final SimulatorScenario scenario, final String name, final List<ScenarioParameter> scenarioParameters) {
        LOG.info(String.format("Starting scenario : %s", name));

        final ScenarioExecution es = activityService.createExecutionScenario(name, scenarioParameters);

        prepare(scenario);

        startScenarioAsync(es.getExecutionId(), name, scenario, scenarioParameters);

        return es.getExecutionId();
    }

    private Future<?> startScenarioAsync(final Long executionId, final String name, final SimulatorScenario scenario, final List<ScenarioParameter> scenarioParameters) {
        return executorService.submit(() -> startAsyncScenario(executionId, name, scenario, scenarioParameters));
    }

    private void startAsyncScenario(final Long executionId, final String name, final SimulatorScenario scenario, final List<ScenarioParameter> scenarioParameters) {
        try {
            if (scenario instanceof Executable) {
                startExecutable(executionId, scenario, scenarioParameters);
            } else {
                final TestContext context = citrus.createTestContext();
                ReflectionUtils.doWithMethods(scenario.getClass(), m -> {
                    if (m.getDeclaringClass().equals(SimulatorScenario.class)) {
                        // no need to execute the default run implementations
                        return;
                    }

                    if (m.getParameterCount() != 1) {
                        throw new SimulatorException("Invalid scenario method signature - expect single method parameter but got: " + m.getParameterCount());
                    }

                    final Class<?> parameterType = m.getParameterTypes()[0];
                    if (parameterType.equals(ScenarioDesigner.class)) {
                        final ScenarioDesigner designer = new ScenarioDesigner(scenario.getScenarioEndpoint(), citrus.getApplicationContext(), context);
                        if (scenarioParameters != null) {
                            scenarioParameters.forEach(p -> designer.variable(p.getName(), p.getValue()));
                        }

                        designer.variable(ScenarioExecution.EXECUTION_ID, executionId);

                        CitrusAnnotations.injectAll(scenario, citrus, context);

                        ReflectionUtils.invokeMethod(m, scenario, designer);
                        citrus.run(designer.getTestCase(), context);
                    } else if (parameterType.equals(ScenarioRunner.class)) {
                        final ScenarioRunner runner = new ScenarioRunner(scenario.getScenarioEndpoint(), citrus.getApplicationContext(), context);
                        if (scenarioParameters != null) {
                            scenarioParameters.forEach(p -> runner.variable(p.getName(), p.getValue()));
                        }

                        runner.variable(ScenarioExecution.EXECUTION_ID, executionId);

                        CitrusAnnotations.injectAll(scenario, citrus, context);

                        try {
                            runner.start();
                            ReflectionUtils.invokeMethod(m, scenario, runner);
                        } finally {
                            runner.stop();
                        }
                    } else {
                        throw new SimulatorException("Invalid scenario method parameter type: " + parameterType);
                    }
                }, method -> method.getName().equals("run"));
            }
            LOG.debug(String.format("Scenario completed: '%s'", name));
        } catch (final Exception e) {
            LOG.error(String.format("Scenario completed with error: '%s'", name), e);
        }
    }

    private void startExecutable(final Long executionId, final SimulatorScenario scenario, final List<ScenarioParameter> scenarioParameters) {
        if (scenarioParameters != null) {
            scenarioParameters.forEach(p -> addVariable(scenario, p.getName(), p.getValue()));
        }

        addVariable(scenario, ScenarioExecution.EXECUTION_ID, executionId);

        ((Executable) scenario).execute();
    }

    /**
     * Adds a new variable to the testExecutable using the supplied key an value.
     *
     * @param scenario
     * @param key      variable name
     * @param value    variable value
     */
    private void addVariable(final SimulatorScenario scenario, final String key, final Object value) {
        if (scenario instanceof TestDesigner) {
            ((TestDesigner) scenario).variable(key, value);
        }

        if (scenario instanceof TestRunner) {
            ((TestRunner) scenario).variable(key, value);
        }
    }

    /**
     * Prepare scenario instance before execution. Subclasses can add custom preparation steps in here.
     *
     * @param scenario
     */
    protected void prepare(final SimulatorScenario scenario) {
    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdownNow();
    }

    @Override
    public void onApplicationEvent(final ContextClosedEvent event) {
        executorService.shutdownNow();
    }
}
