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
import com.consol.citrus.dsl.builder.ReceiveMessageBuilder;
import com.consol.citrus.dsl.builder.SendMessageBuilder;
import com.consol.citrus.dsl.design.TestDesigner;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.message.Message;
import com.consol.citrus.simulator.correlation.CorrelationHandler;
import com.consol.citrus.simulator.correlation.CorrelationHandlerBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractSimulatorScenario implements SimulatorScenario, CorrelationHandler, ApplicationContextAware {

    /** Spring bean application context */
    private ApplicationContext applicationContext;

    /**
     * Starts new correlation for messages with given handler.
     *
     * @return
     */
    public CorrelationHandlerBuilder startCorrelation(TestDesigner designer) {
        CorrelationHandlerBuilder builder = new CorrelationHandlerBuilder(this);
        designer.action(builder);
        designer.doFinally().actions(builder.stop());
        return builder;
    }

    @Override
    public boolean isHandlerFor(Message message, TestContext context) {
        return false;
    }

    /**
     * Gets the scenario endpoint.
     *
     * @return
     */
    public ScenarioEndpoint scenario() {
        return new DefaultScenarioEndpoint();
    }

    /**
     * Subclasses must provide the endpoint for receiving messages.
     *
     * @return
     */
    protected abstract Endpoint getInboundEndpoint();

    /**
     * Subclasses must provide the endpoint for sending messages. When simulating a synchronous endpoint (e.g. HTTP)
     * this is typically the same endpoint that is returned by {@link #getInboundEndpoint()}
     *
     * @return
     */
    protected abstract Endpoint getOutboundEndpoint();

    /**
     * Sets the applicationContext.
     *
     * @param applicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Gets the applicationContext.
     *
     * @return
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Default scenario implementation.
     */
    protected class DefaultScenarioEndpoint implements ScenarioEndpoint {
        @Override
        public ReceiveMessageBuilder receive(TestDesigner designer) {
            return (ReceiveMessageBuilder)
                    designer.receive(getInboundEndpoint())
                            .description("Received scenario request");
        }

        @Override
        public ReceiveMessageBuilder receive(TestDesigner designer, String endpointName) {
            return (ReceiveMessageBuilder)
                    designer.receive(endpointName)
                            .description("Received scenario request");
        }

        @Override
        public SendMessageBuilder send(TestDesigner designer) {
            return (SendMessageBuilder)
                    designer.send(getOutboundEndpoint())
                            .description("Sending scenario response");
        }

        @Override
        public SendMessageBuilder send(TestDesigner designer, String endpointName) {
            return (SendMessageBuilder)
                    designer.send(endpointName)
                            .description("Sending scenario response");
        }
    }
}
