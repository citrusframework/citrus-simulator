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

package com.consol.citrus.simulator;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
@Test
public class SimulatorRestIT extends TestNGCitrusTestDesigner {

    private String defaultResponse = "<DefaultResponse>This is a default response!</DefaultResponse>";

    /** Test Http REST client */
    @Autowired
    private HttpClient simulatorClient;

    /**
     * Sends a hello request to server expecting positive response message.
     */
    @CitrusTest
    public void testHelloRequest() {
        send(simulatorClient)
                .payload("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say Hello!" +
                         "</Hello>");

        receive(simulatorClient)
                .payload("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Hi there!" +
                         "</HelloResponse>");

    }

    /**
     * Sends goodbye request to server expecting positive response message.
     */
    @CitrusTest
    public void testGoodByeRequest() {
        send(simulatorClient)
                .payload("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say GoodBye!" +
                         "</GoodBye>");

        receive(simulatorClient)
                .payload("<GoodByeResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Bye bye!" +
                         "</GoodByeResponse>");
    }

    /**
     * Sends some other request to server expecting positive default response message.
     */
    @CitrusTest
    public void testDefaultRequest() {
        send(simulatorClient)
                .payload("<Default>" +
                            "Should trigger default scenario" +
                        "</Default>");

        receive(simulatorClient)
                .payload(defaultResponse);
    }

    /**
     * Sends some intervening request to server expecting positive response message.
     */
    @CitrusTest
    public void testInterveningRequest() {
        variable("correlationId", "citrus:randomNumber(10)");

        send(simulatorClient)
                .payload("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Go to sleep!" +
                        "</GoodNight>")
                .header("x-correlationid", "${correlationId}");

        receive(simulatorClient)
                .payload("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Good Night!" +
                        "</GoodNightResponse>");

        send(simulatorClient)
                .payload("<InterveningRequest>In between!</InterveningRequest>");

        receive(simulatorClient)
                .payload(defaultResponse);

        send(simulatorClient)
                .payload("<InterveningRequest>In between!</InterveningRequest>")
                .header("x-correlationid", "${correlationId}");

        receive(simulatorClient)
                .payload("<InterveningResponse>In between!</InterveningResponse>");

        send(simulatorClient)
                .payload("<InterveningRequest>In between!</InterveningRequest>")
                .header("x-correlationid", "${correlationId}");

        receive(simulatorClient)
                .payload(defaultResponse);
    }
}
