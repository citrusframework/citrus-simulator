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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.citrusframework.Citrus;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * Provides an asynchronous implementation of the {@link org.citrusframework.simulator.service.ScenarioExecutorService}
 * for executing simulation scenarios. Unlike its superclass {@link DefaultScenarioExecutorService} that runs scenarios
 * synchronously, this service executes each scenario in a separate thread, allowing for scenario executions with
 * intermediate synchronous messaging.
 * <p>
 * This service is conditionally enabled when the {@code citrus.simulator.mode} property is set to {@code async},
 * providing a more flexible and non-blocking way to handle scenario executions. It leverages a fixed thread pool, the
 * size of which is determined by the {@code executorThreads} property from {@link SimulatorConfigurationProperties}, to
 * manage and execute scenario tasks.
 * <p>
 * This class also implements {@link ApplicationListener} for {@link ContextClosedEvent} and {@link DisposableBean} to
 * ensure proper shutdown of the executor service during application shutdown, preventing potential memory leaks or
 * hanging threads.
 * <p>
 * Use this service when scenario execution needs to be scalable and non-blocking, particularly useful when scenarios
 * are long-running and do not need to be executed in sequence.
 *
 * @see org.citrusframework.simulator.service.ScenarioExecutorService
 * @see ApplicationListener
 * @see DefaultScenarioExecutorService
 * @see DisposableBean
 */
@Service
@ConditionalOnProperty(name = "citrus.simulator.mode", havingValue = "async")
public class AsyncScenarioExecutorService extends DefaultScenarioExecutorService implements ApplicationListener<ContextClosedEvent>, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(AsyncScenarioExecutorService.class);

    private final ExecutorService executorService;

    public AsyncScenarioExecutorService(ApplicationContext applicationContext, Citrus citrus, ScenarioExecutionService scenarioExecutionService, SimulatorConfigurationProperties properties) {
        super(applicationContext, citrus, scenarioExecutionService);

        this.executorService = newFixedThreadPool(
            properties.getExecutorThreads(),
            new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("execution-svc-thread-%d")
                .build()
        );
    }

    /**
     * Initiates the shutdown of the executor service to release resources and stop all running threads gracefully upon
     * bean destruction.
     */
    @Override
    public void destroy() throws Exception {
        shutdownExecutor();
    }

    /**
     * Handles the {@link ContextClosedEvent} to initiate the shutdown of the executor service ensuring no tasks are left
     * running when the application context is closed.
     *
     * @param event the context closed event
     */
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        shutdownExecutor();
    }

    /**
     * Overrides the {@link DefaultScenarioExecutorService#startScenario(Long, String, SimulatorScenario, List)} method
     * to execute the scenario asynchronously using the executor service.
     *
     * @param executionId          the unique identifier for the scenario execution
     * @param name                 the name of the scenario to start
     * @param scenario             the scenario instance to execute
     * @param scenarioParameters   the list of parameters to pass to the scenario when starting
     */
    @Override
    public void startScenario(Long executionId, String name, SimulatorScenario scenario, List<ScenarioParameter> scenarioParameters) {
        startScenarioAsync(executionId, name, scenario, scenarioParameters);
    }

    /**
     * Submits the scenario execution task to the executor service for asynchronous execution.
     *
     * @param executionId          the unique identifier for the scenario execution
     * @param name                 the name of the scenario to start
     * @param scenario             the scenario instance to execute
     * @param scenarioParameters   the list of parameters to pass to the scenario when starting
     */
    private void startScenarioAsync(Long executionId, String name, SimulatorScenario scenario, List<ScenarioParameter> scenarioParameters) {
       runAsync(() -> super.startScenario(executionId, name, scenario, scenarioParameters), executorService)
           .exceptionally(scenario::registerException);
    }

    private void shutdownExecutor() {
        logger.debug("Request to shutdown executor");

        if (!executorService.isShutdown()) {
            logger.trace("Shutting down executor");
            executorService.shutdownNow();
        }
    }
}
