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

import org.citrusframework.context.TestContext;
import org.citrusframework.endpoint.AbstractEndpoint;
import org.citrusframework.message.Message;
import org.citrusframework.messaging.Consumer;
import org.citrusframework.messaging.Producer;
import org.citrusframework.simulator.endpoint.EndpointMessageHandler;
import org.citrusframework.simulator.endpoint.SimulationFailedUnexpectedlyException;
import org.citrusframework.simulator.exception.SimulatorException;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.currentThread;
import static java.util.Collections.synchronizedMap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ScenarioEndpoint extends AbstractEndpoint implements Producer, Consumer {

    /**
     * Internal in-memory message channel.
     */
    private final LinkedBlockingQueue<Message> channel = new LinkedBlockingQueue<>();

    /**
     * Futures for messages added but not yet consumed by {@link #receive}.
     * Keyed by message identity so two messages with equal content never collide.
     */
    private final Map<Message, CompletableFuture<Message>> pendingFutures =
        synchronizedMap(new IdentityHashMap<>());

    /**
     * Futures for messages already consumed by {@link #receive}.
     * Keyed by the {@link TestContext} that received the message, binding each send/fail to the correct caller.
     */
    private final Map<TestContext, CompletableFuture<Message>> activeFutures =
        synchronizedMap(new IdentityHashMap<>());

    /**
     * FIFO queue of futures in arrival order, used by {@link #fail} which has no TestContext.
     * Populated in {@link #receive}; may already be completed by {@link #send} when consumed.
     */
    private final Queue<CompletableFuture<Message>> orderedFutures = new LinkedBlockingQueue<>();

    /**
     * Default constructor using endpoint configuration.
     *
     * @param endpointConfiguration
     */
    public ScenarioEndpoint(ScenarioEndpointConfiguration endpointConfiguration) {
        super(endpointConfiguration);
    }

    /**
     * Adds new message for direct message consumption.
     *
     * @param request
     */
    public void add(Message request, CompletableFuture<Message> future) {
        pendingFutures.put(request, future);
        channel.add(request);
        orderedFutures.add(future);
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
        try {
            Message message = channel.poll(timeout, MILLISECONDS);

            if (isNull(message)) {
                throw new SimulatorException("Failed to receive scenario inbound message");
            }

            CompletableFuture<Message> future = pendingFutures.remove(message);
            activeFutures.put(context, future);

            messageReceived(message, context);

            return message;
        } catch (InterruptedException e) {
            currentThread().interrupt();
            throw new SimulatorException(e);
        }
    }

    @Override
    public void send(Message message, TestContext context) {
        messageSent(message, context);

        CompletableFuture<Message> future = Optional.ofNullable(activeFutures.remove(context))
            .orElseGet(() -> Optional.ofNullable(orderedFutures.poll())
                .orElseThrow(() -> new SimulatorException("Failed to process scenario response message - missing response consumer!")));

        future.complete(message);
    }

    void fail(Throwable e) {
        // Clean up any unreceived message lingering in the channel
        Message unreceived = channel.poll();
        if (nonNull(unreceived)) {
            pendingFutures.remove(unreceived);
        }

        CompletableFuture<Message> future = orderedFutures.poll();
        if (nonNull(future)) {
            future.complete(new SimulationFailedUnexpectedlyException(e));
            return;
        }

        throw new SimulatorException("Failed to receive scenario inbound message");
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
