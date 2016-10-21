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

package com.consol.citrus.simulator.ws;

import com.consol.citrus.dsl.builder.SendSoapFaultBuilder;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.simulator.scenario.AbstractSimulatorScenario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Christoph Deppisch
 */
public class SimulatorWebServiceScenario extends AbstractSimulatorScenario {

    @Autowired
    @Qualifier("simulatorWsInboundEndpoint")
    private Endpoint simInboundEndpoint;

    @Override
    protected Endpoint getEndpoint() {
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
         * @return
         */
        public SendSoapFaultBuilder sendFault() {
            return (SendSoapFaultBuilder)
                    sendSoapFault(simInboundEndpoint)
                            .description("Sending SOAP fault");
        }
    }
}
