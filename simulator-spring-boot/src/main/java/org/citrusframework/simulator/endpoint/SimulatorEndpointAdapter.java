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

package org.citrusframework.simulator.endpoint;

import lombok.Getter;
import lombok.Setter;
import org.citrusframework.endpoint.adapter.RequestDispatchingEndpointAdapter;
import org.citrusframework.message.Message;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.correlation.CorrelationHandler;
import org.citrusframework.simulator.correlation.CorrelationHandlerRegistry;
import org.citrusframework.simulator.exception.SimulatorException;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.lang.Thread.currentThread;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.citrusframework.simulator.endpoint.SimulationFailedUnexpectedlyException.EXCEPTION_TYPE;
import static org.citrusframework.util.StringUtils.hasText;

public class SimulatorEndpointAdapter extends RequestDispatchingEndpointAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SimulatorEndpointAdapter.class);

    private final ApplicationContext applicationContext;
    private final CorrelationHandlerRegistry handlerRegistry;
    private final ScenarioExecutorService scenarioExecutorService;
    private final SimulatorConfigurationProperties simulatorConfiguration;

    @Getter
    @Setter
    private boolean handleResponse = true;

    public SimulatorEndpointAdapter(ApplicationContext applicationContext, CorrelationHandlerRegistry handlerRegistry, ScenarioExecutorService scenarioExecutorService, SimulatorConfigurationProperties simulatorConfiguration) {
        this.applicationContext = applicationContext;
        this.handlerRegistry = handlerRegistry;
        this.scenarioExecutorService = scenarioExecutorService;
        this.simulatorConfiguration = simulatorConfiguration;
    }

    private static ResponseStatusException getResponseStatusException(Throwable e) {
        return new ResponseStatusException(555, "Simulation failed with an Exception!", e);
    }

    @Override
    protected Message handleMessageInternal(Message message) {
        CorrelationHandler handler = handlerRegistry.findHandlerFor(message);

        if (nonNull(handler)) {
            return handleMessageWithCorrelation(message, handler);
        } else {
            return super.handleMessageInternal(message);
        }
    }

    private Message handleMessageWithCorrelation(Message request, CorrelationHandler handler) {
        CompletableFuture<Message> responseFuture = new CompletableFuture<>();
        handler.getScenarioEndpoint().add(request, responseFuture);

        return awaitResponseOrThrowException(responseFuture, handler.getScenarioEndpoint().getName());
    }

    @Override
    public Message dispatchMessage(Message message, String mappingName) {
        String scenarioName = mappingName;

        SimulatorScenario scenario;
        if (!hasText(scenarioName) || !applicationContext.containsBean(scenarioName)) {
            scenarioName = simulatorConfiguration.getDefaultScenario();
            logger.info("Unable to find scenario for mapping '{}' - using default scenario '{}'", mappingName, scenarioName);
        }
        scenario = applicationContext.getBean(scenarioName, SimulatorScenario.class);

        scenario.getScenarioEndpoint().setName(scenarioName);

        CompletableFuture<Message> responseFuture = new CompletableFuture<>();
        scenario.getScenarioEndpoint().add(message, responseFuture);

        try {
            scenarioExecutorService.run(scenario, scenarioName, emptyList());
        } catch (Exception e) {
            throw getResponseStatusException(e);
        }

        return awaitResponseOrThrowException(responseFuture, scenarioName);
    }

    private Message awaitResponseOrThrowException(CompletableFuture<Message> responseFuture, String scenarioName) {
        try {
            if (handleResponse) {
                var message = responseFuture.get(simulatorConfiguration.getDefaultTimeout(), MILLISECONDS);

                if (EXCEPTION_TYPE.equals(message.getType())) {
                    throw getResponseStatusException(message.getPayload(Throwable.class));
                }

                return message;
            } else {
                return null;
            }
        } catch (TimeoutException e) {
            logger.warn("No response for scenario '{}'", scenarioName);
            return null;
        } catch (InterruptedException e) {
            currentThread().interrupt();
            throw new SimulatorException(e);
        } catch (ExecutionException e) {
            throw new SimulatorException(e);
        }
    }
}
