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

import lombok.extern.slf4j.Slf4j;
import org.citrusframework.context.TestContext;
import org.citrusframework.endpoint.AbstractEndpoint;
import org.citrusframework.message.Message;
import org.citrusframework.messaging.Consumer;
import org.citrusframework.messaging.Producer;
import org.citrusframework.simulator.endpoint.EndpointMessageHandler;
import org.citrusframework.simulator.endpoint.SimulationFailedUnexpectedlyException;
import org.citrusframework.simulator.exception.SimulatorException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.currentThread;
import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Slf4j
public class ScenarioEndpoint extends AbstractEndpoint implements Producer, Consumer {

    /**
     * Internal im memory message channel
     */
    private final LinkedBlockingQueue<ExecutionParams> channel = new LinkedBlockingQueue<>();

    private final ConcurrentHashMap<TestContext, CompletableFuture<Message>> pendingResponseFutures = new ConcurrentHashMap<>();

    /**
     * Default constructor using endpoint configuration.
     */
    public ScenarioEndpoint(ScenarioEndpointConfiguration endpointConfiguration) {
        super(endpointConfiguration);
    }

    /**
     * Adds a new message for direct message consumption.
     */
    public void add(Message requestMessage, CompletableFuture<Message> responseFuture) {
        channel.add(new ExecutionParams(requestMessage, responseFuture));
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
        completeNextResponseFuture(new SimulationFailedUnexpectedlyException(e), testContext);
    }

    private Message pollMessageForExecution(TestContext testContext, long timeout) {
        try {
            return receiveNextMessageFromChannel(testContext, timeout);
        } catch (InterruptedException e) {
            currentThread().interrupt();
            throw new SimulatorException(e);
        }
    }

    private Message receiveNextMessageFromChannel(TestContext testContext, long timeout) throws InterruptedException {
        var executionParams = channel.poll(timeout, MILLISECONDS);
        if (isNull(executionParams)) {
            throw new SimulatorException("Failed to receive scenario inbound message");
        }

        pendingResponseFutures.put(testContext, executionParams.responseFuture());

        return executionParams.requestMessage();
    }

    private void completeNextResponseFuture(Message message, TestContext testContext) {
        if (pendingResponseFutures.isEmpty()) {
            logger.debug("Failed to process scenario response message, response consumer for testContext is missing; will poll next: {}", testContext);
            receive(testContext);
        }

        pendingResponseFutures.get(testContext).complete(message);
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

    public record ExecutionParams(Message requestMessage, CompletableFuture<Message> responseFuture) {
    }
}
