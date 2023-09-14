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

import org.citrusframework.annotations.CitrusTest;
import org.citrusframework.http.client.HttpClient;
import org.citrusframework.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import static org.citrusframework.actions.SleepAction.Builder.sleep;
import static org.citrusframework.http.actions.HttpActionBuilder.http;

/**
 * @author Christoph Deppisch
 */
@Test
@ContextConfiguration(classes = EndpointConfig.class)
public class SimulatorRestIT extends TestNGCitrusSpringSupport {

    public static final String HTTP_CORRELATION_ID = "x-correlationid";
    private final String defaultResponse = "<DefaultResponse>This is a default response!</DefaultResponse>";

    /** Test Http REST client */
    @Autowired
    private HttpClient simulatorClient;

    /**
     * Sends a hello request to server expecting positive response message.
     */
    @CitrusTest
    public void testHelloRequest() {
        $(http().client(simulatorClient)
                .send()
                .post("hello")
                .message()
                .body("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say Hello!" +
                         "</Hello>"));

        $(http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
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
        $(http().client(simulatorClient)
                .send()
                .post("goodbye")
                .message()
                .body("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say GoodBye!" +
                         "</GoodBye>"));

        $(http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
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
        $(http().client(simulatorClient)
                .send()
                .post()
                .message()
                .body("<Default>" +
                            "Should trigger default scenario" +
                        "</Default>"));

        $(http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body(defaultResponse));
    }

    /**
     * Sends some intervening request to server expecting positive response message.
     */
    @CitrusTest
    public void testInterveningRequest() {
        variable("correlationId", "citrus:randomNumber(10)");

        $(http().client(simulatorClient)
                .send()
                .post("goodnight")
                .message()
                .body("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Go to sleep!" +
                        "</GoodNight>")
                .header(HTTP_CORRELATION_ID, "${correlationId}"));

        $(http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Good Night!" +
                        "</GoodNightResponse>"));

        $(http().client(simulatorClient)
                .send()
                .post()
                .message()
                .body("<InterveningRequest>In between!</InterveningRequest>"));

        $(http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body(defaultResponse));

        $(http().client(simulatorClient)
                .send()
                .post()
                .message()
                .body("<InterveningRequest>In between!</InterveningRequest>")
                .header(HTTP_CORRELATION_ID, "${correlationId}"));

        $(http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body("<InterveningResponse>In between!</InterveningResponse>"));

        $(sleep().milliseconds(2000L));

        $(http().client(simulatorClient)
                .send()
                .put("goodnight")
                .message()
                .body("<InterveningRequest>In between!</InterveningRequest>")
                .header(HTTP_CORRELATION_ID, "${correlationId}"));

        $(http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body(defaultResponse));
    }
}
