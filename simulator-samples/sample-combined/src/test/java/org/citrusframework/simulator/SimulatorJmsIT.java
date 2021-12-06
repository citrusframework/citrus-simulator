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

package org.citrusframework.simulator;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.jms.endpoint.JmsSyncEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
@Test
@ContextConfiguration(classes = EndpointConfig.class)
public class SimulatorJmsIT extends TestNGCitrusTestDesigner {

    private String defaultResponse = "<DefaultResponse>This is a default response!</DefaultResponse>";

    /** Test JMS endpoint */
    @Autowired
    private JmsSyncEndpoint jmsSyncEndpoint;

    /**
     * Sends a hello request to server expecting positive response message.
     */
    @CitrusTest
    public void testHelloRequest() {
        send(jmsSyncEndpoint)
                .payload("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say Hello!" +
                         "</Hello>");

        receive(jmsSyncEndpoint)
                .payload("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Hi there!" +
                         "</HelloResponse>");

    }

    /**
     * Sends goodbye request to server expecting positive response message.
     */
    @CitrusTest
    public void testGoodByeRequest() {
        send(jmsSyncEndpoint)
                .payload("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say GoodBye!" +
                         "</GoodBye>");

        receive(jmsSyncEndpoint)
                .payload("<GoodByeResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Bye bye!" +
                         "</GoodByeResponse>");
    }

    /**
     * Sends some other request to server expecting positive default response message.
     */
    @CitrusTest
    public void testDefaultRequest() {
        send(jmsSyncEndpoint)
                .payload("<Default>" +
                            "Should trigger default scenario" +
                        "</Default>");

        receive(jmsSyncEndpoint)
                .payload(defaultResponse);
    }

    /**
     * Sends some intervening request to server expecting positive response message.
     */
    @CitrusTest
    public void testInterveningRequest() {
        variable("correlationId", "citrus:randomNumber(10)");

        send(jmsSyncEndpoint)
                .payload("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Go to sleep!" +
                        "</GoodNight>")
                .header("x-correlationid", "${correlationId}");

        receive(jmsSyncEndpoint)
                .payload("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Good Night!" +
                        "</GoodNightResponse>");

        send(jmsSyncEndpoint)
                .payload("<InterveningRequest>In between!</InterveningRequest>");

        receive(jmsSyncEndpoint)
                .payload(defaultResponse);

        send(jmsSyncEndpoint)
                .payload("<InterveningRequest>In between!</InterveningRequest>")
                .header("x-correlationid", "${correlationId}");

        receive(jmsSyncEndpoint)
                .payload("<InterveningResponse>In between!</InterveningResponse>");

        sleep(2000L);

        send(jmsSyncEndpoint)
                .payload("<InterveningRequest>In between!</InterveningRequest>")
                .header("x-correlationid", "${correlationId}");

        receive(jmsSyncEndpoint)
                .payload(defaultResponse);
    }
}
