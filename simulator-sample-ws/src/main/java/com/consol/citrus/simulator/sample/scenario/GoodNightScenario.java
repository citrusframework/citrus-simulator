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

package com.consol.citrus.simulator.sample.scenario;

import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.message.Message;
import com.consol.citrus.simulator.scenario.Scenario;
import com.consol.citrus.simulator.ws.SimulatorWebServiceScenario;

/**
 * @author Christoph Deppisch
 */
@Scenario("GoodNight")
public class GoodNightScenario extends SimulatorWebServiceScenario {

    @Override
    protected void configure() {
        startCorrelation()
            .withHandler(this);

        receiveScenarioRequest()
            .payload("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Go to sleep!" +
                     "</GoodNight>")
            .header("citrus_soap_action", "GoodNight");

        sendScenarioFault()
            .faultCode("{http://citrusframework.org}CITRUS:SIM-1001")
            .faultString("No sleep for me!");

        receiveScenarioRequest()
                .payload("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Go to sleep!" +
                        "</GoodNight>")
                .header("citrus_soap_action", "GoodNight");

        sendScenarioResponse()
                .payload("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Good Night!" +
                        "</GoodNightResponse>");
    }

    @Override
    public boolean isHandlerFor(Message message) {
        return new XPathPayloadMappingKeyExtractor().getMappingKey(message).equals("GoodNight");
    }
}
