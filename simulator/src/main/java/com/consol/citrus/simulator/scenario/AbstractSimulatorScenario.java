/*
 * Copyright 2006-2016 the original author or authors.
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

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractSimulatorScenario extends ExecutableTestDesignerComponent implements SimulatorScenario, CorrelationHandler {

    /**
     * Starts new correlation for messages with given handler.
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
     * @return
     */
    public ScenarioEndpoint scenario() {
        return new DefaultScenarioEndpoint();
    }

    /**
     * Subclasses must provide the target endpoint.
     * @return
     */
    protected abstract Endpoint getEndpoint();

    /**
     * Default scenario implementation.
     */
    protected class DefaultScenarioEndpoint implements ScenarioEndpoint {
        @Override
        public ReceiveMessageBuilder receive() {
            return (ReceiveMessageBuilder)
                    AbstractSimulatorScenario.this.receive(getEndpoint())
                            .description("Received scenario request");
        }

        @Override
        public SendMessageBuilder send() {
            return (SendMessageBuilder)
                    AbstractSimulatorScenario.this.send(getEndpoint())
                            .description("Sending scenario response");
        }
    }
}
