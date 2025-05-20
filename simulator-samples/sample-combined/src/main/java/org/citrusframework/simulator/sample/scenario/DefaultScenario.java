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

import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.ws.message.SoapMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Christoph Deppisch
 */
@Scenario("Default")
public class DefaultScenario extends AbstractSimulatorScenario {

    private static final Logger logger = LoggerFactory.getLogger(DefaultScenario.class);

    @Override
    public void run(ScenarioRunner scenario) {
        final AtomicBoolean isSoap = new AtomicBoolean(false);

        scenario.$(scenario.receive().message().validate((message, context) -> {
            isSoap.set(message instanceof SoapMessage);
        }));

        if (isSoap.get()) {
            scenario.$(
                scenario.soap()
                    .sendFault()
                    .message()
                    .faultActor("SERVER")
                    .faultCode("{http://localhost:8080/HelloService/v1}CITRUS:SIM-1100")
                    .faultString("No matching scenario found")
            );

            return;
        }

        scenario.$(
            scenario.send()
                .message()
                .body("<DefaultResponse>This is a default response!</DefaultResponse>")
        );
    }
}
