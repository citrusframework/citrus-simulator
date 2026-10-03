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
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.currentThread;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.citrusframework.simulator.endpoint.SimulationFailedUnexpectedlyException.EXCEPTION_TYPE;
import static org.citrusframework.util.StringUtils.hasText;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

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

    private static ResponseStatusException getServiceUnavailableException(RejectedExecutionException e) {
        return new ResponseStatusException(SERVICE_UNAVAILABLE, "Simulator is busy, no scenario executor available!", e);
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

        return awaitResponseOrThrowException(responseFuture, handler.getScenarioEndpoint().getName(), () -> handler.getScenarioEndpoint().cancel(responseFuture));
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

        if (scenarioExecutorService.supportsTestContextInitialization()) {
            return dispatchMessageToExecution(message, scenario, scenarioName);
        }

        return dispatchMessageThroughChannel(message, scenario, scenarioName);
    }

    /**
     * Hands the request to its scenario execution, bound to the test context of that execution. The executor releases
     * the request once the execution has ended, so the response future is completed at the latest then - nobody waits
     * for a response that will never be sent. For synchronous executions it has already been completed when the
     * executor returns.
     */
    private @Nullable Message dispatchMessageToExecution(Message request, SimulatorScenario scenario, String scenarioName) {
        ScenarioEndpoint scenarioEndpoint = scenario.getScenarioEndpoint();
        CompletableFuture<Message> responseFuture = new CompletableFuture<>();
        AtomicReference<TestContext> executionContext = new AtomicReference<>();

        try {
            scenarioExecutorService.run(scenario, scenarioName, emptyList(), context -> {
                executionContext.set(context);
                scenarioEndpoint.bind(context, request, responseFuture);
            });
        } catch (RejectedExecutionException e) {
            throw getServiceUnavailableException(e);
        } catch (Exception e) {
            scenarioEndpoint.release(executionContext.get());
            throw getResponseStatusException(e);
        }

        // Late responses after a timeout are discarded with the request, once the execution releases it
        return awaitResponseOrThrowException(responseFuture, scenarioName, () -> {
        });
    }

    /**
     * Hands the request to its scenario execution through the message channel of the scenario endpoint. Used for
     * executors not supporting {@link ScenarioExecutorService#supportsTestContextInitialization() test context
     * initialization}.
     */
    private @Nullable Message dispatchMessageThroughChannel(Message request, SimulatorScenario scenario, String scenarioName) {
        ScenarioEndpoint scenarioEndpoint = scenario.getScenarioEndpoint();
        CompletableFuture<Message> responseFuture = new CompletableFuture<>();
        scenarioEndpoint.add(request, responseFuture);

        try {
            scenarioExecutorService.run(scenario, scenarioName, emptyList());
        } catch (RejectedExecutionException e) {
            scenarioEndpoint.cancel(responseFuture);
            throw getServiceUnavailableException(e);
        } catch (Exception e) {
            scenarioEndpoint.cancel(responseFuture);
            throw getResponseStatusException(e);
        }

        if (scenarioExecutorService.isSynchronous() && !responseFuture.isDone()) {
            // The scenario has already completed without responding, waiting would only block the calling thread
            scenarioEndpoint.cancel(responseFuture);
            logger.warn("No response for scenario '{}'", scenarioName);
            return null;
        }

        return awaitResponseOrThrowException(responseFuture, scenarioName, () -> scenarioEndpoint.cancel(responseFuture));
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

    private @Nullable Message awaitResponseOrThrowException(CompletableFuture<Message> responseFuture, String scenarioName, Runnable cancellation) {
        try {
            if (handleResponse) {
                var message = responseFuture.get(simulatorConfiguration.getDefaultTimeout(), MILLISECONDS);

                if (isNull(message)) {
                    logger.warn("No response for scenario '{}'", scenarioName);
                    return null;
                }

                if (EXCEPTION_TYPE.equals(message.getType())) {
                    return handleSimulationFailure(message.getPayload(Throwable.class));
                }

                return message;
            } else {
                return null;
            }
        } catch (TimeoutException e) {
            cancellation.run();
            logger.warn("No response for scenario '{}'", scenarioName);
            return null;
        } catch (InterruptedException e) {
            cancellation.run();
            currentThread().interrupt();
            throw new SimulatorException(e);
        } catch (ExecutionException e) {
            cancellation.run();
            throw new SimulatorException(e);
        }
    }
}
