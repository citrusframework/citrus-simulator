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
import org.citrusframework.container.BeforeSuite;
import org.citrusframework.container.SequenceBeforeSuite;
import org.citrusframework.dsl.endpoint.CitrusEndpoints;
import org.citrusframework.http.client.HttpClient;
import org.citrusframework.simulator.sample.RestSimulator;
import org.citrusframework.testng.spring.TestNGCitrusSpringSupport;
import org.citrusframework.xml.XsdSchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    /**
     * Test Http REST client configured to access the API
     */
    @Autowired
    @Qualifier("apiClient")
    private HttpClient apiClient;

    /**
     * Test Http REST client
     */
    @Autowired
    @Qualifier("simulatorClient")
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

    @CitrusTest
    public void testSimulationWithExplodingQueryParams() {
        $(http().client(simulatorClient)
            .send()
            .get("parameter")
            .queryParam("exploded", "1")
            .queryParam("exploded", "2"));

        $(http().client(simulatorClient)
            .receive()
            .response(HttpStatus.OK)
            .message()
            .body(
                """
                    {
                      "pulver": [1, 2]
                    }
                    """
            ));
    }

    /**
     * Sends a request to the server, expecting it to purposefully fail a simulation. The response code must therefore
     * be {@link HttpStatus#OK}.
     *
     * @see org.citrusframework.simulator.sample.scenario.FailScenario
     */
    @CitrusTest
    public void testSimulationFailingExpectantly() {
        $(http().client(simulatorClient)
            .send()
            .get("fail"));

        $(http().client(simulatorClient)
            .receive()
            .response(HttpStatus.OK));

        // Make sure we receive exactly one record using "count" resources
        $(http().client(apiClient)
            .send()
            .get("scenario-executions/count?headers=citrus_endpoint_uri~/services/rest/simulator/fail&scenarioName.contains=Fail&status.equals=2&distinct=true"));

        $(http().client(apiClient)
            .receive()
            .response(HttpStatus.OK)
            .getMessageBuilderSupport()
            .body("1"));
    }

    /**
     * Sends a request to the server, expecting it to execute a simulation. The response should indicate the unexpected
     * error, returning a {@link HttpStatus#INTERNAL_SERVER_ERROR}.
     *
     * @see org.citrusframework.simulator.sample.scenario.ThrowScenario
     */
    @CitrusTest
    public void testSimulationWithUnexpectedError() {
        $(http().client(simulatorClient)
            .send()
            .get("throw")
            .message()
            .accept(MediaType.APPLICATION_JSON_VALUE));

        $(http().client(simulatorClient)
            .receive()
            .response(HttpStatus.INTERNAL_SERVER_ERROR)
            .message()
            .body(
                // language=json
                """
                    {
                      "timestamp":"@ignore@",
                      "status":555,
                      "error":"Http Status 555",
                      "path":"/services/rest/simulator/throw"
                    }
                    """
            ));
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
        public HttpClient apiClient() {
            return CitrusEndpoints.http().client()
                .requestUrl(String.format("http://localhost:%s/api", 8080))
                .build();
        }

        @Bean
        @ConditionalOnProperty(name = "simulator.mode", havingValue = "embedded")
        public BeforeSuite startEmbeddedSimulator() {
            return new SequenceBeforeSuite.Builder().actions(context -> SpringApplication.run(RestSimulator.class)).build();
        }
    }
}
