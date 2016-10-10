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

import com.consol.citrus.simulator.endpoint.SimulatorEndpointScenario;
import com.consol.citrus.simulator.scenario.Scenario;

/**
 * @author Christoph Deppisch
 */
@Scenario("GoodNight")
public class GoodNightScenario extends SimulatorEndpointScenario {

    private static final String CORRELATION_ID = "correlationId";

    @Override
    protected void configure() {
        receiveScenarioRequest()
            .payload("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                        "<from>user@citrusframework.org</from>" +
                        "<to>citrus@citrusframework.org</to>" +
                        "<cc></cc>" +
                        "<bcc></bcc>" +
                        "<subject>GoodNight</subject>" +
                        "<body>" +
                            "<contentType>text/plain; charset=utf-8</contentType>" +
                            "<content>Say GoodNight!</content>" +
                        "</body>" +
                    "</mail-message>");

        startCorrelation()
            .onMessageType("mail-message");

        sendScenarioResponse()
            .payload("<mail-response xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                        "<code>250</code>" +
                        "<message>OK</message>" +
                    "</mail-response>");

        receiveScenarioRequest()
                .payload("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                            "<from>user@citrusframework.org</from>" +
                            "<to>citrus@citrusframework.org</to>" +
                            "<cc></cc>" +
                            "<bcc></bcc>" +
                            "<subject>Intervening</subject>" +
                            "<body>" +
                                "<contentType>text/plain; charset=utf-8</contentType>" +
                                "<content>Say Intervening!</content>" +
                            "</body>" +
                        "</mail-message>");

        sendScenarioResponse()
                .payload("<mail-response xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                            "<code>250</code>" +
                            "<message>OK</message>" +
                        "</mail-response>");
    }
}
