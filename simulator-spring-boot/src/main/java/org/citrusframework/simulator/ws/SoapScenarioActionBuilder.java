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

package org.citrusframework.simulator.ws;

import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.ws.actions.ReceiveSoapMessageAction;
import org.citrusframework.ws.actions.SendSoapFaultAction;
import org.citrusframework.ws.actions.SendSoapMessageAction;
import org.citrusframework.ws.actions.SoapActionBuilder;

/**
 * @author Christoph Deppisch
 */
public class SoapScenarioActionBuilder extends SoapActionBuilder {

    /** Scenario endpoint */
    private final ScenarioEndpoint scenarioEndpoint;

    public SoapScenarioActionBuilder(ScenarioEndpoint scenarioEndpoint) {
        this.scenarioEndpoint = scenarioEndpoint;
    }

    /**
     * Default scenario receive operation.
     * @return
     */
    public ReceiveSoapMessageAction.Builder receive() {
        return server(scenarioEndpoint.getName()).receive().endpoint(scenarioEndpoint);
    }

    /**
     * Default scenario send response operation.
     *
     * @return
     */
    public SendSoapMessageAction.Builder send() {
        return server(scenarioEndpoint.getName()).send().endpoint(scenarioEndpoint);
    }

    /**
     * Sends SOAP fault as scenario response.
     * @return
     */
    public SendSoapFaultAction.Builder sendFault() {
        return server(scenarioEndpoint.getName()).sendFault().endpoint(scenarioEndpoint);
    }

}
