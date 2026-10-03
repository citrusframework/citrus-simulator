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

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.citrusframework.base.endpoint.adapter.EmptyResponseEndpointAdapter;
import org.citrusframework.base.endpoint.adapter.RequestDispatchingEndpointAdapter;
import org.citrusframework.context.TestContext;
import org.citrusframework.endpoint.EndpointAdapter;
import org.citrusframework.exceptions.TestCaseFailedException;
import org.citrusframework.message.Message;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.correlation.CorrelationHandler;
import org.citrusframework.simulator.correlation.CorrelationHandlerRegistry;
import org.citrusframework.simulator.exception.SimulatorException;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.currentThread;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
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

        return awaitResponseOrThrowException(responseFuture, handler.getScenarioEndpoint().getName(), handler.getScenarioEndpoint());
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

        if (scenarioExecutorService.isSynchronous()) {
            return dispatchMessageSynchronously(message, scenario, scenarioName);
        }

        CompletableFuture<Message> responseFuture = new CompletableFuture<>();
        scenario.getScenarioEndpoint().add(message, responseFuture);

        try {
            scenarioExecutorService.run(scenario, scenarioName, emptyList());
        } catch (Exception e) {
            scenario.getScenarioEndpoint().cancel(responseFuture);
            throw getResponseStatusException(e);
        }

        return awaitResponseOrThrowException(responseFuture, scenarioName, scenario.getScenarioEndpoint());
    }

    /**
     * Executes the scenario on the calling thread, handing over the request bound to the test context of the execution.
     * The scenario has completed once the executor returns, so its response is available right away - no message
     * queue, no future and no waiting involved.
     */
    private @Nullable Message dispatchMessageSynchronously(Message request, SimulatorScenario scenario, String scenarioName) {
        ScenarioEndpoint scenarioEndpoint = scenario.getScenarioEndpoint();
        AtomicReference<TestContext> executionContext = new AtomicReference<>();

        Message response;
        try {
            scenarioExecutorService.run(scenario, scenarioName, emptyList(), context -> {
                executionContext.set(context);
                scenarioEndpoint.bind(context, request);
            });
        } catch (Exception e) {
            throw getResponseStatusException(e);
        } finally {
            response = scenarioEndpoint.unbind(executionContext.get());
        }

        if (!handleResponse) {
            return null;
        }

        if (isNull(response)) {
            logger.warn("No response for scenario '{}'", scenarioName);
            return null;
        }

        if (EXCEPTION_TYPE.equals(response.getType())) {
            return handleSimulationFailure(response.getPayload(Throwable.class));
        }

        return response;
    }

    /**
     * Answers a failed simulation with the custom HTTP status code 555. Failed test actions (e.g. a failed request
     * validation) are handed to a custom fallback endpoint adapter instead, if one has been configured: returning no
     * response makes {@link org.citrusframework.base.endpoint.AbstractEndpointAdapter#handleMessage} delegate to it.
     * Exceptions thrown by the scenario code itself always answer 555.
     *
     * @param cause the cause of the failed simulation
     * @return {@code null}, if the request is to be handled by the fallback endpoint adapter
     * @throws ResponseStatusException with status code 555 otherwise
     */
    private @Nullable Message handleSimulationFailure(Throwable cause) {
        if (cause instanceof TestCaseFailedException && hasCustomFallbackEndpointAdapter()) {
            logger.debug("Simulation failed with a failing test action - delegating to fallback endpoint adapter", cause);
            return null;
        }

        throw getResponseStatusException(cause);
    }

    private boolean hasCustomFallbackEndpointAdapter() {
        EndpointAdapter fallbackEndpointAdapter = getFallbackEndpointAdapter();
        return nonNull(fallbackEndpointAdapter) && !(fallbackEndpointAdapter instanceof EmptyResponseEndpointAdapter);
    }

    private Message awaitResponseOrThrowException(CompletableFuture<Message> responseFuture, String scenarioName, ScenarioEndpoint scenarioEndpoint) {
        try {
            if (handleResponse) {
                var message = responseFuture.get(simulatorConfiguration.getDefaultTimeout(), MILLISECONDS);

                if (EXCEPTION_TYPE.equals(message.getType())) {
                    return handleSimulationFailure(message.getPayload(Throwable.class));
                }

                return message;
            } else {
                return null;
            }
        } catch (TimeoutException e) {
            scenarioEndpoint.cancel(responseFuture);
            logger.warn("No response for scenario '{}'", scenarioName);
            return null;
        } catch (InterruptedException e) {
            scenarioEndpoint.cancel(responseFuture);
            currentThread().interrupt();
            throw new SimulatorException(e);
        } catch (ExecutionException e) {
            scenarioEndpoint.cancel(responseFuture);
            throw new SimulatorException(e);
        }
    }
}
