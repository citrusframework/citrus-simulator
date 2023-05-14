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
import org.citrusframework.simulator.sample.config.EndpointConfig;
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
@ContextConfiguration(classes = {EndpointConfig.class})
public class SimulatorRestIT extends TestNGCitrusSpringSupport {

    private String defaultResponse = "<DefaultResponse>This is a default response!</DefaultResponse>";

    /** Test Http REST client */
    @Autowired
    private HttpClient simulatorClient;

    /**
     * Sends a hello request to server expecting positive response message.
     */
    @CitrusTest
    public void testHelloRequest() {
        when(
            http().client(simulatorClient)
                .send()
                .post("hello")
                .getMessageBuilderSupport()
                .body("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Say Hello!" +
                        "</Hello>")
        );

        then(
            http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .getMessageBuilderSupport()
                .body("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Hi there!" +
                        "</HelloResponse>")
        );
    }

    /**
     * Sends goodbye request to server expecting positive response message.
     */
    @CitrusTest
    public void testGoodByeRequest() {
        when(
            http().client(simulatorClient)
                .send()
                .post("goodbye")
                .getMessageBuilderSupport()
                .body("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Say GoodBye!" +
                        "</GoodBye>")
        );

        then(
            http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .getMessageBuilderSupport()
                .body("<GoodByeResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Bye bye!" +
                        "</GoodByeResponse>")
        );
    }

    /**
     * Sends some other request to server expecting positive default response message.
     */
    @CitrusTest
    public void testDefaultRequest() {
        when(
            http().client(simulatorClient)
                .send()
                .post()
                .getMessageBuilderSupport()
                .body("<Default>" +
                            "Should trigger default scenario" +
                        "</Default>")
        );

        then(
            http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .getMessageBuilderSupport()
                .body(defaultResponse)
        );
    }

    /**
     * Sends some intervening request to server expecting positive response message.
     */
    @CitrusTest
    public void testInterveningRequest() {
        variable("correlationId", "citrus:randomNumber(10)");

        then(
            http().client(simulatorClient)
                .send()
                .post("goodnight")
                .getMessageBuilderSupport()
                .body("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Go to sleep!" +
                        "</GoodNight>")
                .header("x-correlationid", "${correlationId}")
        );

        then(
            http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .getMessageBuilderSupport()
                .body("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Good Night!" +
                        "</GoodNightResponse>")
        );

        then(
            http().client(simulatorClient)
                .send()
                .post()
                .getMessageBuilderSupport()
                .body("<InterveningRequest>In between!</InterveningRequest>")
        );

        then(
            http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .getMessageBuilderSupport()
                .body(defaultResponse)
        );

        then(
            http().client(simulatorClient)
                .send()
                .post()
                .getMessageBuilderSupport()
                .body("<InterveningRequest>In between!</InterveningRequest>")
                .header("x-correlationid", "${correlationId}")
        );

        then(
            http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .getMessageBuilderSupport()
                .body("<InterveningResponse>In between!</InterveningResponse>")
        );

        and(sleep().milliseconds(2000L));

        then(
            http().client(simulatorClient)
                .send()
                .put("goodnight")
                .getMessageBuilderSupport()
                .body("<InterveningRequest>In between!</InterveningRequest>")
                .header("x-correlationid", "${correlationId}")
        );

        then(
            http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .getMessageBuilderSupport()
                .body(defaultResponse)
        );
    }
}
