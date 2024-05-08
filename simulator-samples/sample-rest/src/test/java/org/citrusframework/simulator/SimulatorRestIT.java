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
import org.citrusframework.container.BeforeSuite;
import org.citrusframework.container.SequenceBeforeSuite;
import org.citrusframework.dsl.endpoint.CitrusEndpoints;
import org.citrusframework.http.client.HttpClient;
import org.citrusframework.simulator.sample.RestSimulator;
import org.citrusframework.testng.spring.TestNGCitrusSpringSupport;
import org.citrusframework.xml.XsdSchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import java.util.List;

import static org.citrusframework.actions.SleepAction.Builder.sleep;
import static org.citrusframework.http.actions.HttpActionBuilder.http;

/**
 * @author Christoph Deppisch
 */
@Test
@ContextConfiguration(classes = SimulatorRestIT.EndpointConfig.class)
public class SimulatorRestIT extends TestNGCitrusSpringSupport {

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
                .contentType(MediaType.APPLICATION_XML_VALUE)
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
     * Sends a howdy request to server expecting positive response message.
     */
    @CitrusTest
    public void testHowdyRequest() {
        $(http().client(simulatorClient)
                .send()
                .post("howdy")
                .message()
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .body("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Say Hello!" +
                        "</Hello>"));

        $(http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Howdy partner!" +
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
                .contentType(MediaType.APPLICATION_XML_VALUE)
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
                .contentType(MediaType.APPLICATION_XML_VALUE)
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
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .body("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Go to sleep!" +
                        "</GoodNight>")
                .header("x-correlationid", "${correlationId}"));

        $(http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Good Night!" +
                        "</GoodNightResponse>"));

        //
        // Should be handled by default scenario
        //

        $(http().client(simulatorClient)
                .send()
                .post()
                .message()
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .body("<InterveningRequest>No match correlation!</InterveningRequest>"));

        $(http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body(defaultResponse));

        //
        // Should be handled by good-night scenario
        //

        $(http().client(simulatorClient)
                .send()
                .post()
                .message()
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .body("<InterveningRequest>In between!</InterveningRequest>")
                .header("x-correlationid", "${correlationId}"));

        $(http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body("<InterveningResponse>In between!</InterveningResponse>"));

        $(sleep().milliseconds(2000L));

        //
        // Should be handled by default scenario -> the goodnight scenario should have completed by now
        //

        $(http().client(simulatorClient)
                .send()
                .post()
                .message()
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .body("<InterveningRequest>After correlation!</InterveningRequest>")
                .header("x-correlationid", "${correlationId}"));

        $(http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body(defaultResponse));
    }

    /**
     * Sends a request to the server expecting it to purposefully fail a simulation.
     */
    @CitrusTest
    public void testFailingSimulation() {
        $(http().client(simulatorClient)
            .send()
            .post("fail")
            .message()
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .body("<Failure xmlns=\"http://citrusframework.org/schemas/failure\">" +
                "Fail!" +
                "</Failure>"));

        $(http().client(simulatorClient)
            .receive()
            .response(HttpStatus.OK)); // TODO: Pretty sure this should be HttpStatus.INTERNAL_SERVER_ERROR
    }

    @Configuration
    @PropertySource("classpath:application.properties")
    public static class EndpointConfig {

        @Bean
        public XsdSchemaRepository schemaRepository() {
            XsdSchemaRepository schemaRepository = new XsdSchemaRepository();
            schemaRepository.getLocations()
                .addAll(
                    List.of(
                        "classpath:xsd/HelloService.xsd",
                        "classpath:xsd/FailureService.xsd"
                    ));
            return schemaRepository;
        }

        @Bean
        public HttpClient simulatorClient() {
            return CitrusEndpoints.http().client()
                    .requestUrl(String.format("http://localhost:%s/services/rest/simulator", 8080))
                    .build();
        }

        @Bean
        @ConditionalOnProperty(name = "simulator.mode", havingValue = "embedded")
        public BeforeSuite startEmbeddedSimulator() {
            return new SequenceBeforeSuite.Builder().actions(context -> SpringApplication.run(RestSimulator.class)).build();
        }
    }
}
