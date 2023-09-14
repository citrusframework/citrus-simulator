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

package org.citrusframework.simulator.sample.scenario;

import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Christoph Deppisch
 */
@Scenario("GoodNight")
@RequestMapping(value = "/services/rest/simulator/goodnight", method = RequestMethod.POST)
public class GoodNightScenario extends AbstractSimulatorScenario {

    private static final String HTTP_CORRELATION_ID = "x-correlationid";
    private static final String JMS_CORRELATION_ID = "x_correlationid";

    @Override
    public void run(ScenarioRunner scenario) {
        scenario.$(scenario.receive()
            .message()
            .validate((message, context) -> {
                Assert.isTrue(message.getPayload(String.class).equals("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Go to sleep!" +
                        "</GoodNight>"), "Message body validation failed!");

                if (message.getHeader(HTTP_CORRELATION_ID) != null) {
                    context.setVariable("correlationIdHeader", HTTP_CORRELATION_ID);
                    context.setVariable("correlationId", message.getHeader(HTTP_CORRELATION_ID));
                } else if (message.getHeader(JMS_CORRELATION_ID) != null) {
                    context.setVariable("correlationIdHeader", JMS_CORRELATION_ID);
                    context.setVariable("correlationId", message.getHeader(JMS_CORRELATION_ID));
                }
            }));

        scenario.$(correlation().start()
            .onHeader("${correlationIdHeader}", "${correlationId}"));

        scenario.$(scenario.send()
                .message()
                .body("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                                "Good Night!" +
                            "</GoodNightResponse>"));

        scenario.$(scenario.receive()
                .selector("${correlationIdHeader} = '${correlationId}'")
                .message()
                .body("<InterveningRequest>In between!</InterveningRequest>"));

        scenario.$(scenario.send()
                .message()
                .body("<InterveningResponse>In between!</InterveningResponse>"));
    }
}
