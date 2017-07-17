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

import com.consol.citrus.Citrus;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.design.DefaultTestDesigner;
import com.consol.citrus.dsl.design.TestDesigner;
import com.consol.citrus.dsl.endpoint.Executable;
import com.consol.citrus.dsl.runner.DefaultTestRunner;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.simulator.exception.SimulatorException;
import com.consol.citrus.simulator.model.ScenarioExecution;
import com.consol.citrus.simulator.model.ScenarioParameter;
import com.consol.citrus.simulator.scenario.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Default test builder service. Service is called to invoke a test executable manually. The service has to translate
 * normalized parameters to setter on test executable before execution. Service defines a list of supported parameters with default
 * values.
 *
 * @author Christoph Deppisch
 */
@Service
public class DefaultScenarioService implements ScenarioService {

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(DefaultScenarioService.class);

    /**
     * List of available scenario starters
     */
    private Map<String, ScenarioStarter> scenarioStarters;

    /**
     * List of available scenarios
     */
    private Map<String, SimulatorScenario> scenarios;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Citrus citrus;

    @PostConstruct
    private void init() {
        scenarios = applicationContext.getBeansOfType(SimulatorScenario.class);
        log.info(String.format("Scenarios discovered: \n%s", Arrays.toString(scenarios.keySet().toArray())));
        scenarioStarters = applicationContext.getBeansOfType(ScenarioStarter.class);
        log.info(String.format("Scenario Starters discovered: \n%s", Arrays.toString(scenarioStarters.keySet().toArray())));
    }

    @Override
    public final Long run(String name, List<ScenarioParameter> scenarioParameters) {
        log.info(String.format("Starting scenario : %s", name));

        ScenarioExecution es = activityService.createExecutionScenario(name, scenarioParameters);
        ScenarioStarter scenarioStarter = applicationContext.getBean(name, ScenarioStarter.class);

        if (scenarioStarter instanceof ApplicationContextAware) {
            ((ApplicationContextAware) scenarioStarter).setApplicationContext(applicationContext);
        }

        prepare(scenarioStarter);

        startScenarioAsync(es.getExecutionId(), name, scenarioStarter, scenarioParameters);

        return es.getExecutionId();
    }

    private Future<?> startScenarioAsync(Long executionId, String name, ScenarioStarter scenarioStarter, List<ScenarioParameter> scenarioParameters) {
        final ExecutorService executorService = Executors.newSingleThreadExecutor(
                r -> {
                    Thread t = ((ThreadFactory) r1 -> new Thread(r1, "Scenario:" + name)).newThread(r);
                    t.setDaemon(true);
                    return t;
                });

        return executorService.submit(() -> {
            try {
                if (scenarioStarter instanceof Executable) {
                    if (scenarioParameters != null) {
                        scenarioParameters.forEach(p -> addVariable(scenarioStarter, p.getName(), p.getValue()));
                    }

                    addVariable(scenarioStarter, ScenarioExecution.EXECUTION_ID, executionId);

                    ((Executable) scenarioStarter).execute();
                } else {
                    TestContext context = citrus.createTestContext();
                    ReflectionUtils.doWithLocalMethods(scenarioStarter.getClass(), m -> {
                        if (!m.getName().equals("run")) {
                            return;
                        }

                        if (m.getParameterCount() != 1) {
                            throw new SimulatorException("Invalid scenario starter method signature - expect single method parameter but got: " + m.getParameterCount());
                        }

                        Class<?> parameterType = m.getParameterTypes()[0];
                        if (parameterType.equals(TestDesigner.class)) {
                            TestDesigner designer = new DefaultTestDesigner(citrus.getApplicationContext(), context);
                            if (scenarioParameters != null) {
                                scenarioParameters.forEach(p -> designer.variable(p.getName(), p.getValue()));
                            }

                            designer.variable(ScenarioExecution.EXECUTION_ID, executionId);

                            ReflectionUtils.invokeMethod(m, scenarioStarter, designer);
                            citrus.run(designer.getTestCase(), context);
                        } else if (parameterType.equals(TestRunner.class)) {
                            TestRunner runner = new DefaultTestRunner(citrus.getApplicationContext(), context);
                            if (scenarioParameters != null) {
                                scenarioParameters.forEach(p -> runner.variable(p.getName(), p.getValue()));
                            }

                            runner.variable(ScenarioExecution.EXECUTION_ID, executionId);

                            try {
                                runner.start();
                                ReflectionUtils.invokeMethod(m, scenarioStarter, runner);
                            } finally {
                                runner.stop();
                            }
                        } else {
                            throw new SimulatorException("Invalid scenario starter method parameter type: " + parameterType);
                        }
                    });
                }
                log.debug(String.format("Scenario completed: '%s'", name));
            } catch (Exception e) {
                log.error(String.format("Scenario completed with error: '%s'", name), e);
            }
            executorService.shutdownNow();
        });
    }

    /**
     * Adds a new variable to the testExecutable using the supplied key an value.
     *
     * @param scenarioStarter
     * @param key            variable name
     * @param value          variable value
     */
    private void addVariable(ScenarioStarter scenarioStarter, String key, Object value) {
        if (scenarioStarter instanceof TestDesigner) {
            ((TestDesigner) scenarioStarter).variable(key, value);
        }

        if (scenarioStarter instanceof TestRunner) {
            ((TestRunner) scenarioStarter).variable(key, value);
        }
    }

    /**
     * Prepare scenario starter instance before execution. Subclasses can add custom preparation steps in here.
     *
     * @param starter
     */
    protected void prepare(ScenarioStarter starter) {
    }

    @Override
    public Collection<String> getScenarioNames() {
        return scenarios.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public Collection<String> getStarterNames() {
        return scenarioStarters.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ScenarioParameter> lookupScenarioParameters(String scenarioName) {
        if (scenarioStarters.containsKey(scenarioName)) {
            return scenarioStarters.get(scenarioName).getScenarioParameters();
        }
        return Collections.EMPTY_LIST;
    }

}
