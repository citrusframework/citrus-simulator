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

package org.citrusframework.simulator.scenario;

import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.citrusframework.context.TestContext;
import org.citrusframework.endpoint.AbstractEndpoint;
import org.citrusframework.message.Message;
import org.citrusframework.messaging.Consumer;
import org.citrusframework.messaging.Producer;
import org.citrusframework.simulator.endpoint.EndpointMessageHandler;
import org.citrusframework.simulator.endpoint.SimulationFailedUnexpectedlyExceptionMessage;
import org.citrusframework.simulator.exception.SimulatorException;
import org.citrusframework.simulator.service.ScenarioExecutorService;

import static java.lang.Thread.currentThread;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.citrusframework.simulator.service.runner.DefaultScenarioExecutorService.REQUEST_RESPONSE_MAPPING_VARIABLE_NAME;

@Slf4j
public class ScenarioEndpoint extends AbstractEndpoint implements Producer, Consumer {

    @VisibleForTesting
    static final String NO_REQUEST_RESPONSE_MAPPING_IN_TEST_CONTEXT_MESSAGE = "No request-response mapping found in test context! This may happen if you're using the deprecated `ScenarioEndpoint#fail(Throwable)` API.";

    @VisibleForTesting
    static final String NO_RESPONSE_FUTURE_IN_TEST_CONTEXT_MESSAGE = "Failed to match response futures to test context! This may happen if you're using the deprecated `ScenarioEndpoint#fail(Throwable)` API.";

    private static @Nonnull ScenarioExecutorService.ExecutionRequestAndResponse getExecutionRequestAndResponse(TestContext testContext) {
        var requestResponseMapping = testContext.getVariables().get(REQUEST_RESPONSE_MAPPING_VARIABLE_NAME);

        if (nonNull(requestResponseMapping) &&
            requestResponseMapping instanceof ScenarioExecutorService.ExecutionRequestAndResponse executionRequestAndResponse) {
            return executionRequestAndResponse;
        }

        throw new SimulatorException(NO_REQUEST_RESPONSE_MAPPING_IN_TEST_CONTEXT_MESSAGE);
    }

    public ScenarioEndpoint(ScenarioEndpointConfiguration endpointConfiguration) {
        super(endpointConfiguration);
    }

    @Override
    public Producer createProducer() {
        return this;
    }

    @Override
    public Consumer createConsumer() {
        return this;
    }

    @Override
    public Message receive(TestContext context) {
        return receive(context, getEndpointConfiguration().getTimeout());
    }

    @Override
    public Message receive(TestContext context, long timeout) {
        var message = pollMessageForExecution(context, timeout);
        messageReceived(message, context);

        return message;
    }

    @Override
    public void send(Message message, TestContext testContext) {
        messageSent(message, testContext);
        completeNextResponseFuture(message, testContext);
    }

    void fail(Throwable e, TestContext testContext) {
        completeNextResponseFuture(new SimulationFailedUnexpectedlyExceptionMessage(e), testContext);
    }

    private Message pollMessageForExecution(TestContext testContext, long timeout) {
        try {
            return receiveNextMessageFromChannel(testContext);
        } catch (InterruptedException e) {
            currentThread().interrupt();
            throw new SimulatorException(e);
        }
    }

    private Message receiveNextMessageFromChannel(TestContext testContext) throws InterruptedException {
        return getExecutionRequestAndResponse(testContext).requestMessage();
    }

    private void completeNextResponseFuture(Message message, TestContext testContext) {
        var responseFuture = getExecutionRequestAndResponse(testContext).responseFuture();

        if (isNull(responseFuture)) {
            throw new SimulatorException(NO_RESPONSE_FUTURE_IN_TEST_CONTEXT_MESSAGE);
        }

        responseFuture.complete(message);
    }

    private void messageSent(Message message, TestContext context) {
        getEndpointMessageHandler(context).handleSentMessage(message, context);
    }

    private void messageReceived(Message message, TestContext context) {
        getEndpointMessageHandler(context).handleReceivedMessage(message, context);
    }

    private EndpointMessageHandler getEndpointMessageHandler(TestContext context) {
        return context.getReferenceResolver().resolve(EndpointMessageHandler.class);
    }
}
