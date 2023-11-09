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

import org.citrusframework.exceptions.TestCaseFailedException;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;

import java.util.Objects;

/**
 * @author Christoph Deppisch
 */
@Scenario("Default")
public class DefaultScenario extends AbstractSimulatorScenario {

    private static final Logger logger = LoggerFactory.getLogger(DefaultScenario.class);

    @Override
    public void run(ScenarioRunner scenario) {
        boolean isSoap = false;
        try {
            scenario.$(scenario.receive());
        } catch (TestCaseFailedException e) {
            if (getRootCause(e) instanceof SAXParseException saxParseException) {
                logger.warn("Default Scenario request parsing failed, assuming unmatched SOAP request!", e);
                scenario.$(
                    scenario.soap()
                        .sendFault()
                        .message()
                        .faultActor("SERVER")
                        .faultCode("{http://localhost:8080/HelloService/v1}CITRUS:SIM-1100")
                        .faultString("No matching scenario found")
                );
                isSoap = true;
            }
        }

        if (!isSoap) {
            scenario.$(
                scenario.send()
                    .message()
                    .body("<DefaultResponse>This is a default response!</DefaultResponse>")
            );
        }
    }

    private Throwable getRootCause(Exception e) {
        Throwable cause = e;

        while (!Objects.isNull(cause.getCause())) {
            cause = cause.getCause();
        }

        return cause;
    }
}
