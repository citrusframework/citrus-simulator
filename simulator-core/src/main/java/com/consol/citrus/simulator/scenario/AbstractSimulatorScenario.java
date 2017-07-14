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

import com.consol.citrus.dsl.builder.ReceiveMessageBuilder;
import com.consol.citrus.dsl.builder.SendMessageBuilder;
import com.consol.citrus.dsl.design.ExecutableTestDesignerComponent;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.message.Message;
import com.consol.citrus.simulator.correlation.CorrelationHandler;
import com.consol.citrus.simulator.correlation.CorrelationHandlerBuilder;
import com.consol.citrus.simulator.exception.SimulatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractSimulatorScenario extends ExecutableTestDesignerComponent implements SimulatorScenario, CorrelationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSimulatorScenario.class);

    @Autowired
    private List<Endpoint> endpoints;

    @PostConstruct
    private void init() {
        List<String> endpointNames = endpoints.parallelStream()
                .map(endpoint -> endpoint.getName())
                .sorted()
                .collect(Collectors.toList());
        LOG.info(String.format("Endpoints discovered: \n%s", endpointNames));
    }

    /**
     * Starts new correlation for messages with given handler.
     *
     * @return
     */
    public CorrelationHandlerBuilder startCorrelation() {
        CorrelationHandlerBuilder builder = new CorrelationHandlerBuilder(getTestCase(), this);
        action(builder);
        return builder;
    }

    @Override
    public boolean isHandlerFor(Message message) {
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
     * Subclasses must provide the target endpoint.
     *
     * @return
     */
    protected Endpoint getEndpointByName(String name) {
        Optional<Endpoint> optional = endpoints.parallelStream()
                .filter(endpoint -> endpoint.getName().equalsIgnoreCase(name))
                .findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new SimulatorException(String.format("No endpoint found matching name '%s'", name));
    }

    /**
     * Default scenario implementation.
     */
    protected class DefaultScenarioEndpoint implements ScenarioEndpoint {
        @Override
        public ReceiveMessageBuilder receive() {
            return (ReceiveMessageBuilder)
                    AbstractSimulatorScenario.this.receive(getInboundEndpoint())
                            .description("Received scenario request");
        }

        @Override
        public ReceiveMessageBuilder receive(String endpointName) {
            return (ReceiveMessageBuilder)
                    AbstractSimulatorScenario.this.receive(getEndpointByName(endpointName))
                            .description("Received scenario request");
        }

        @Override
        public SendMessageBuilder send() {
            return (SendMessageBuilder)
                    AbstractSimulatorScenario.this.send(getOutboundEndpoint())
                            .description("Sending scenario response");
        }

        @Override
        public SendMessageBuilder send(String endpointName) {
            return (SendMessageBuilder)
                    AbstractSimulatorScenario.this.send(getEndpointByName(endpointName))
                            .description("Sending scenario response");
        }
    }
}
