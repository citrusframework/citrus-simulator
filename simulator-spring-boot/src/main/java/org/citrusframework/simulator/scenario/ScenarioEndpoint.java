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

import jakarta.annotation.Nullable;
import org.citrusframework.context.TestContext;
import org.citrusframework.endpoint.AbstractEndpoint;
import org.citrusframework.message.Message;
import org.citrusframework.messaging.Consumer;
import org.citrusframework.messaging.Producer;
import org.citrusframework.simulator.endpoint.EndpointMessageHandler;
import org.citrusframework.simulator.endpoint.SimulationFailedUnexpectedlyException;
import org.citrusframework.simulator.exception.SimulatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(ScenarioEndpoint.class);

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
     * Populated in {@link #add}; may already be completed by {@link #send} when consumed.
     */
    private final Queue<CompletableFuture<Message>> orderedFutures = new LinkedBlockingQueue<>();

    /**
     * Requests bound to the {@link TestContext} of their scenario execution, see {@link #bind}. These bypass the message
     * channel and the futures above, which remain in use for intermediate messages only.
     */
    private final Map<TestContext, Exchange> exchanges = synchronizedMap(new IdentityHashMap<>());

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

    /**
     * Binds a request to the {@link TestContext} of the scenario execution handling it. The first {@link #receive} within
     * that context consumes the request, the first {@link #send} completes the {@code responseFuture} with the
     * response, {@link #fail(TestContext, Throwable)} completes it with a {@link SimulationFailedUnexpectedlyException}.
     * Once the execution has ended, {@link #release} completes it with {@code null} if it has not been answered.
     * <p>
     * Requests are matched by context identity, so concurrent executions sharing this endpoint never see each other's
     * messages.
     *
     * @param context        the test context of the scenario execution
     * @param request        the request to be handled by the scenario execution
     * @param responseFuture completed with the response to the request
     */
    public void bind(TestContext context, Message request, CompletableFuture<Message> responseFuture) {
        exchanges.put(context, new Exchange(request, responseFuture));
    }

    /**
     * Releases all requests of the given {@link TestContext}, once its scenario execution has ended: the request bound
     * using {@link #bind} as well as an intermediate message received from the message channel. Requests that have
     * not been answered are completed with {@code null}, i.e. "no response", so that nobody waits for a response that
     * will never be sent.
     *
     * @param context the test context of the ended scenario execution, may be {@code null}
     */
    public void release(@Nullable TestContext context) {
        if (isNull(context)) {
            return;
        }

        Optional.ofNullable(activeFutures.remove(context))
            .ifPresent(future -> {
                cancel(future);
                future.complete(null);
            });

        Optional.ofNullable(exchanges.remove(context))
            .ifPresent(Exchange::release);
    }

    /**
     * Removes an in-flight response future from all internal collections.
     *
     * @param future future to remove
     */
    public void cancel(CompletableFuture<Message> future) {
        if (isNull(future)) {
            return;
        }

        orderedFutures.remove(future);

        synchronized (pendingFutures) {
            pendingFutures.entrySet().removeIf(entry -> {
                if (entry.getValue() == future) {
                    channel.removeIf(message -> message == entry.getKey());
                    return true;
                }

                return false;
            });
        }

        synchronized (activeFutures) {
            activeFutures.entrySet().removeIf(entry -> entry.getValue() == future);
        }
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
        Message boundRequest = Optional.ofNullable(exchanges.get(context))
            .map(Exchange::receive)
            .orElse(null);

        if (nonNull(boundRequest)) {
            messageReceived(boundRequest, context);
            return boundRequest;
        }

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

        // Intermediate messages received from the channel take precedence over the bound request
        CompletableFuture<Message> future = activeFutures.remove(context);
        if (nonNull(future)) {
            future.complete(message);
            return;
        }

        Exchange exchange = exchanges.get(context);
        if (nonNull(exchange)) {
            if (!exchange.respond(message)) {
                logger.debug("Request bound to scenario execution has already been answered, ignoring response");
            }

            return;
        }

        Optional.ofNullable(orderedFutures.poll())
            .orElseThrow(() -> new SimulatorException("Failed to process scenario response message - missing response consumer!"))
            .complete(message);
    }

    void fail(Throwable e) {
        CompletableFuture<Message> future = orderedFutures.poll();
        if (nonNull(future)) {
            cancel(future);
            future.complete(new SimulationFailedUnexpectedlyException(e));
            return;
        }

        throw new SimulatorException("Failed to receive scenario inbound message");
    }

    /**
     * Fails the requests handled within the given {@link TestContext}: the request bound using {@link #bind} as well as
     * an intermediate message received from the message channel. Requests that have already been answered are not
     * affected, neither are concurrent executions sharing this endpoint.
     *
     * @param context the test context of the failed scenario execution
     * @param e       the cause of the failure
     * @return {@code true} if the failure could be attributed to a request of the given context, {@code false} if no
     * such request is known
     */
    public boolean fail(TestContext context, Throwable e) {
        boolean attributed = false;

        CompletableFuture<Message> future = activeFutures.remove(context);
        if (nonNull(future)) {
            cancel(future);
            future.complete(new SimulationFailedUnexpectedlyException(e));
            attributed = true;
        }

        Exchange exchange = exchanges.get(context);
        if (nonNull(exchange)) {
            exchange.respond(new SimulationFailedUnexpectedlyException(e));
            attributed = true;
        }

        return attributed;
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

    /**
     * A request bound to a scenario execution and the future of its response. Synchronized, because test action
     * containers may execute actions of the same {@link TestContext} on different threads.
     */
    private static final class Exchange {

        private final Message request;
        private final CompletableFuture<Message> responseFuture;

        private boolean received = false;

        private Exchange(Message request, CompletableFuture<Message> responseFuture) {
            this.request = request;
            this.responseFuture = responseFuture;
        }

        /**
         * @return the request on the first invocation, {@code null} afterwards
         */
        synchronized @Nullable Message receive() {
            if (received) {
                return null;
            }

            received = true;
            return request;
        }

        /**
         * @return {@code true} if the response has been accepted, {@code false} if the request has already been answered
         */
        boolean respond(Message response) {
            return responseFuture.complete(response);
        }

        /**
         * Answers the request with "no response", unless it has already been answered.
         */
        void release() {
            responseFuture.complete(null);
        }
    }
}
