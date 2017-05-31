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

package com.consol.citrus.simulator.ws;

import com.consol.citrus.dsl.actions.DelegatingTestAction;
import com.consol.citrus.dsl.builder.SoapServerFaultResponseActionBuilder;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.simulator.scenario.AbstractSimulatorScenario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Christoph Deppisch
 */
public class SimulatorWebServiceScenario extends AbstractSimulatorScenario {

    /**
     * The endpoint for receiving and sending webservice requests and responses.
     * Since this is a synchronous endpoint the same endpoint can be used
     * for receiving requests and sending responses.
     */
    @Autowired
    @Qualifier("simulatorWsInboundEndpoint")
    private Endpoint simInboundEndpoint;

    @Override
    protected Endpoint getDefaultReceiveEndpoint() {
        return simInboundEndpoint;
    }

    @Override
    protected Endpoint getDefaultSendEndpoint() {
        return simInboundEndpoint;
    }

    @Override
    public DefaultWsScenarioEndpoint scenario() {
        return new DefaultWsScenarioEndpoint();
    }

    /**
     * Default scenario implementation.
     */
    protected class DefaultWsScenarioEndpoint extends DefaultScenarioEndpoint {
        /**
         * Sends SOAP fault as scenario response.
         *
         * @return
         */
        public SoapServerFaultResponseActionBuilder sendFault() {
            SoapServerFaultResponseActionBuilder actionBuilder = (SoapServerFaultResponseActionBuilder)
                    new SoapServerFaultResponseActionBuilder(new DelegatingTestAction<>(), simInboundEndpoint)
                            .withApplicationContext(getApplicationContext())
                            .description("Sending SOAP fault");

            SimulatorWebServiceScenario.this.action(actionBuilder);
            return actionBuilder;
        }
    }
}
