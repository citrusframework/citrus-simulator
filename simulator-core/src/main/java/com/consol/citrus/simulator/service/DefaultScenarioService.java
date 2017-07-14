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

import com.consol.citrus.dsl.design.TestDesigner;
import com.consol.citrus.dsl.endpoint.Executable;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.simulator.model.ScenarioExecution;
import com.consol.citrus.simulator.model.ScenarioParameter;
import com.consol.citrus.simulator.scenario.ScenarioStarter;
import com.consol.citrus.simulator.scenario.SimulatorScenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
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


    @PostConstruct
    private void init() {
        scenarios = applicationContext.getBeansOfType(SimulatorScenario.class);
        log.info(String.format("Scenarios discovered: \n%s", Arrays.toString(scenarios.keySet().toArray())));
        scenarioStarters = applicationContext.getBeansOfType(ScenarioStarter.class);
        log.info(String.format("Scenario Starters discovered: \n%s", Arrays.toString(scenarioStarters.keySet().toArray())));
    }

    @Override
    public final Long run(String name, List<ScenarioParameter> scenarioParameters) {
        log.info(String.format("Executing scenario : %s", name));

        Executable testExecutable = applicationContext.getBean(name, Executable.class);

        if (testExecutable instanceof ApplicationContextAware) {
            ((ApplicationContextAware) testExecutable).setApplicationContext(applicationContext);
        }

        prepare(testExecutable);

        if (scenarioParameters != null) {
            scenarioParameters.forEach(p -> addTestVariable(testExecutable, p.getName(), p.getValue()));
        }

        ScenarioExecution es = activityService.createExecutionScenario(name, scenarioParameters);
        addTestVariable(testExecutable, ScenarioExecution.EXECUTION_ID, es.getExecutionId());

        executeScenarioAsync(name, testExecutable);

        return es.getExecutionId();
    }

    private Future<?> executeScenarioAsync(String name, Executable testExecutable) {
        final ExecutorService executorService = Executors.newSingleThreadExecutor(
                r -> {
                    Thread t = ((ThreadFactory) r1 -> new Thread(r1, "Scenario:" + name)).newThread(r);
                    t.setDaemon(true);
                    return t;
                });

        return executorService.submit(() -> {
            log.debug(String.format("Starting scenario: '%s'", name));
            try {
                testExecutable.execute();
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
     * @param testExecutable
     * @param key            variable name
     * @param value          variable value
     */
    private void addTestVariable(Executable testExecutable, String key, Object value) {
        if (testExecutable instanceof TestDesigner) {
            ((TestDesigner) testExecutable).variable(key, value);
        }

        if (testExecutable instanceof TestRunner) {
            ((TestRunner) testExecutable).variable(key, value);
        }
    }

    /**
     * Prepare test executable instance before execution. Subclasses can add custom preparation steps in here.
     *
     * @param testExecutable
     */
    protected void prepare(Executable testExecutable) {
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
