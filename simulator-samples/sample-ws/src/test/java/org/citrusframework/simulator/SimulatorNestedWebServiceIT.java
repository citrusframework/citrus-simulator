/*
 * Copyright 2024 the original author or authors.
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

package org.citrusframework.simulator;

import static org.citrusframework.actions.ReceiveMessageAction.Builder.receive;
import static org.citrusframework.actions.SendMessageAction.Builder.send;

import org.citrusframework.annotations.CitrusTest;
import org.citrusframework.testng.spring.TestNGCitrusSpringSupport;
import org.citrusframework.ws.client.WebServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

@Test
@ContextConfiguration(classes = EndpointConfig.class)
public class SimulatorNestedWebServiceIT extends TestNGCitrusSpringSupport {

    @Autowired
    private WebServiceClient nestedSimulatorClient;

    @CitrusTest
    public void testHelloRequest() {
        $(send(nestedSimulatorClient)
            .message()
            .body("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                "Say Hello!" +
                "</Hello>")
            .header("citrus_soap_action", "Hello"));

        $(receive(nestedSimulatorClient)
            .message()
            .body("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                "Hi there!" +
                "</HelloResponse>"));
    }
}
