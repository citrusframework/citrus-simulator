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

package org.citrusframework.simulator.sample.scenario;

import org.citrusframework.context.TestContext;
import org.citrusframework.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import org.citrusframework.message.Message;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioRunner;

/**
 * @author Christoph Deppisch
 */
@Scenario("GoodNight")
public class GoodNightScenario extends AbstractSimulatorScenario {

    @Override
    public void run(ScenarioRunner scenario) {
        scenario.$(correlation().start()
                .withHandler(this));

        scenario.$(scenario.soap()
                .receive()
                .message()
                .body("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Go to sleep!" +
                    "</GoodNight>")
                .soapAction("GoodNight"));

        scenario.$(scenario.soap()
                .sendFault()
                .message()
                .faultCode("{http://citrusframework.org}CITRUS:SIM-1001")
                .faultString("No sleep for me!"));

        scenario.$(scenario.soap()
                .receive()
                .message()
                .body("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Go to sleep!" +
                    "</GoodNight>")
                .soapAction("GoodNight"));

        scenario.$(scenario.soap()
                .send()
                .message()
                .body("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Good Night!" +
                    "</GoodNightResponse>"));
    }

    @Override
    public boolean isHandlerFor(Message message, TestContext context) {
        return new XPathPayloadMappingKeyExtractor().getMappingKey(message).equals("GoodNight");
    }
}
