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
import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.dsl.runner.TestRunnerBeforeSuiteSupport;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import org.citrusframework.simulator.sample.Simulator;
import com.consol.citrus.xml.XsdSchemaRepository;
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

/**
 * @author Christoph Deppisch
 */
@Test
@ContextConfiguration(classes = SimulatorRestIT.EndpointConfig.class)
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
        http().client(simulatorClient)
                .send()
                .post("hello")
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .payload("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say Hello!" +
                         "</Hello>");

        http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .payload("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Hi there!" +
                         "</HelloResponse>");
    }

    /**
     * Sends a howdy request to server expecting positive response message.
     */
    @CitrusTest
    public void testHowdyRequest() {
        http().client(simulatorClient)
                .send()
                .post("howdy")
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .payload("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Say Hello!" +
                        "</Hello>");

        http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .payload("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Howdy partner!" +
                        "</HelloResponse>");
    }



    /**
     * Sends goodbye request to server expecting positive response message.
     */
    @CitrusTest
    public void testGoodByeRequest() {
        http().client(simulatorClient)
                .send()
                .post("goodbye")
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .payload("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say GoodBye!" +
                         "</GoodBye>");

        http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .payload("<GoodByeResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Bye bye!" +
                         "</GoodByeResponse>");
    }

    /**
     * Sends some other request to server expecting positive default response message.
     */
    @CitrusTest
    public void testDefaultRequest() {
        http().client(simulatorClient)
                .send()
                .post()
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .payload("<Default>" +
                            "Should trigger default scenario" +
                        "</Default>");

        http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .payload(defaultResponse);
    }

    /**
     * Sends some intervening request to server expecting positive response message.
     */
    @CitrusTest
    public void testInterveningRequest() {
        variable("correlationId", "citrus:randomNumber(10)");

        http().client(simulatorClient)
                .send()
                .post("goodnight")
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .payload("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Go to sleep!" +
                        "</GoodNight>")
                .header("x-correlationid", "${correlationId}");

        http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .payload("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Good Night!" +
                        "</GoodNightResponse>");

        //
        // Should be handled by default scenario
        //

        http().client(simulatorClient)
                .send()
                .post()
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .payload("<InterveningRequest>No match correlation!</InterveningRequest>");

        http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .payload(defaultResponse);

        //
        // Should be handled by good-night scenario
        //

        http().client(simulatorClient)
                .send()
                .post()
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .payload("<InterveningRequest>In between!</InterveningRequest>")
                .header("x-correlationid", "${correlationId}");

        http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .payload("<InterveningResponse>In between!</InterveningResponse>");

        sleep(2000L);

        //
        // Should be handled by default scenario -> the goodnight scenario should have completed by now
        //

        http().client(simulatorClient)
                .send()
                .post()
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .payload("<InterveningRequest>After correlation!</InterveningRequest>")
                .header("x-correlationid", "${correlationId}");

        http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .payload(defaultResponse);
    }

    @Configuration
    @PropertySource("classpath:application.properties")
    public static class EndpointConfig {

        @Bean
        public XsdSchemaRepository schemaRepository() {
            XsdSchemaRepository schemaRepository = new XsdSchemaRepository();
            schemaRepository.getLocations().add("classpath:xsd/HelloService.xsd");
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
        public TestRunnerBeforeSuiteSupport startEmbeddedSimulator() {
            return new TestRunnerBeforeSuiteSupport() {
                @Override
                public void beforeSuite(TestRunner runner) {
                    SpringApplication.run(Simulator.class);
                }
            };
        }
    }
}
