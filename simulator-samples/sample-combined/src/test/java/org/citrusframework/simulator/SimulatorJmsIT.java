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

package org.citrusframework.simulator;

import org.citrusframework.annotations.CitrusTest;
import org.citrusframework.jms.endpoint.JmsSyncEndpoint;
import org.citrusframework.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import static org.citrusframework.actions.ReceiveMessageAction.Builder.receive;
import static org.citrusframework.actions.SendMessageAction.Builder.send;
import static org.citrusframework.actions.SleepAction.Builder.sleep;

/**
 * @author Christoph Deppisch
 */
@Test
@ContextConfiguration(classes = EndpointConfig.class)
public class SimulatorJmsIT extends TestNGCitrusSpringSupport {

    public static final String JMS_CORRELATION_ID = "x_correlationid";
    private final String defaultResponse = "<DefaultResponse>This is a default response!</DefaultResponse>";

    /** Test JMS endpoint */
    @Autowired
    private JmsSyncEndpoint jmsSyncEndpoint;

    /**
     * Sends a hello request to server expecting positive response message.
     */
    @CitrusTest
    public void testHelloRequest() {
        $(send(jmsSyncEndpoint)
                .message()
                .body("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say Hello!" +
                         "</Hello>"));

        $(receive(jmsSyncEndpoint)
                .message()
                .body("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Hi there!" +
                         "</HelloResponse>"));

    }

    /**
     * Sends goodbye request to server expecting positive response message.
     */
    @CitrusTest
    public void testGoodByeRequest() {
        $(send(jmsSyncEndpoint)
                .message()
                .body("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say GoodBye!" +
                         "</GoodBye>"));

        $(receive(jmsSyncEndpoint)
                .message()
                .body("<GoodByeResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Bye bye!" +
                         "</GoodByeResponse>"));
    }

    /**
     * Sends some other request to server expecting positive default response message.
     */
    @CitrusTest
    public void testDefaultRequest() {
        $(send(jmsSyncEndpoint)
                .message()
                .body("<Default>" +
                            "Should trigger default scenario" +
                        "</Default>"));

        $(receive(jmsSyncEndpoint)
                .message()
                .body(defaultResponse));
    }

    /**
     * Sends some intervening request to server expecting positive response message.
     */
    @CitrusTest
    public void testInterveningRequest() {
        variable("correlationId", "citrus:randomNumber(10)");

        $(send(jmsSyncEndpoint)
                .message()
                .body("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Go to sleep!" +
                        "</GoodNight>")
                .header(JMS_CORRELATION_ID, "${correlationId}"));

        $(receive(jmsSyncEndpoint)
                .message()
                .body("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Good Night!" +
                        "</GoodNightResponse>"));

        $(send(jmsSyncEndpoint)
                .message()
                .body("<InterveningRequest>In between!</InterveningRequest>"));

        $(receive(jmsSyncEndpoint)
                .message()
                .body(defaultResponse));

        $(send(jmsSyncEndpoint)
                .message()
                .body("<InterveningRequest>In between!</InterveningRequest>")
                .header(JMS_CORRELATION_ID, "${correlationId}"));

        $(receive(jmsSyncEndpoint)
                .message()
                .body("<InterveningResponse>In between!</InterveningResponse>"));

        $(sleep().milliseconds(2000L));

        $(send(jmsSyncEndpoint)
                .message()
                .body("<InterveningRequest>In between!</InterveningRequest>")
                .header(JMS_CORRELATION_ID, "${correlationId}"));

        $(receive(jmsSyncEndpoint)
                .message()
                .body(defaultResponse));
    }
}
