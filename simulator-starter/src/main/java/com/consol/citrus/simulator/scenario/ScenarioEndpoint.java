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

package com.consol.citrus.simulator.scenario;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.endpoint.AbstractEndpoint;
import com.consol.citrus.message.Message;
import com.consol.citrus.messaging.Consumer;
import com.consol.citrus.messaging.Producer;
import com.consol.citrus.simulator.exception.SimulatorException;

import java.util.Stack;
import java.util.concurrent.*;

/**
 * @author Christoph Deppisch
 */
public class ScenarioEndpoint extends AbstractEndpoint implements Producer, Consumer {

    /** Internal im memory message channel */
    private LinkedBlockingQueue<Message> channel = new LinkedBlockingQueue<>();

    /** Stack of response futures to complete */
    private Stack<CompletableFuture<Message>> responseFutures = new Stack<>();

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
     * @param request
     */
    public void add(Message request, CompletableFuture<Message> future) {
        responseFutures.push(future);
        channel.add(request);
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
            return channel.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new SimulatorException(e);
        }
    }

    @Override
    public void send(Message message, TestContext context) {
        if (responseFutures.isEmpty()) {
            throw new SimulatorException("Failed to process scenario response message - missing response consumer");
        } else {
            responseFutures.pop().complete(message);
        }
    }
}
