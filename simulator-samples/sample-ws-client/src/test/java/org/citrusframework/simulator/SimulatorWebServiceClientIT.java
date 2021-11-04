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

import java.util.Arrays;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.dsl.runner.TestRunnerBeforeSuiteSupport;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.sample.Simulator;
import org.citrusframework.simulator.sample.variables.Name;
import com.consol.citrus.ws.server.WebServiceServer;
import com.consol.citrus.xml.XsdSchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.testng.annotations.Test;

/**
 * Verifies that the webservice client is working properly.
 *
 * @author Martin Maher
 */
@Test
@ContextConfiguration(classes = SimulatorWebServiceClientIT.EndpointConfig.class)
public class SimulatorWebServiceClientIT extends TestNGCitrusTestDesigner {

    @Autowired
    @Qualifier("testSoapServer")
    private WebServiceServer soapServer;

    @Autowired
    @Qualifier("simulatorRestEndpoint")
    protected HttpClient restEndpoint;

    /**
     * Sends a hello request to server expecting positive response message.
     */
    @CitrusTest
    public void testHelloRequest() {
        Name name = new Name();

        http()
            .client(restEndpoint)
            .send()
            .post("/api/scenario/launch/HelloStarter")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .payload(asJson(name.asScenarioParameter()));

        http()
            .client(restEndpoint)
            .receive()
            .response(HttpStatus.OK);

        receive(soapServer)
            .payload("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                    name.getValue() +
                    "</Hello>")
            .header("citrus_soap_action", "Hello");

        send(soapServer)
            .payload("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                    "Hi there " + name.getValue() +
                    "</HelloResponse>");

    }

    private String asJson(ScenarioParameter... scenarioParameters) {
        final Jackson2JsonObjectMapper mapper = new Jackson2JsonObjectMapper();
        try {
            return mapper.toJson(Arrays.asList(scenarioParameters));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Configuration
    public static class EndpointConfig {

        @Bean
        public XsdSchemaRepository schemaRepository() {
            XsdSchemaRepository schemaRepository = new XsdSchemaRepository();
            schemaRepository.getLocations().add("classpath:xsd/HelloService.xsd");
            return schemaRepository;
        }

        @Bean
        public WebServiceServer testSoapServer() {
            return CitrusEndpoints.soap().server()
                    .autoStart(true)
                    .port(8090)
                    .timeout(5000L)
                    .build();
        }

        @Bean
        public HttpClient simulatorRestEndpoint() {
            return CitrusEndpoints.http().client()
                    .requestUrl(String.format("http://localhost:%s", 8080))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .build();
        }

        @Bean
        public SaajSoapMessageFactory messageFactory() {
            return new SaajSoapMessageFactory();
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
