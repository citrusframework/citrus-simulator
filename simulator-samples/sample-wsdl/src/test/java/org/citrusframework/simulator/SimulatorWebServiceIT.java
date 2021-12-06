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
import org.citrusframework.simulator.sample.Simulator;
import com.consol.citrus.ws.client.WebServiceClient;
import com.consol.citrus.ws.interceptor.LoggingClientInterceptor;
import com.consol.citrus.xml.XsdSchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
@Test
@ContextConfiguration(classes = SimulatorWebServiceIT.EndpointConfig.class)
public class SimulatorWebServiceIT extends TestNGCitrusTestDesigner {

    @Autowired
    private WebServiceClient soapClient;

    @CitrusTest
    public void testHelloRequest() {
        soap().client(soapClient)
                .send()
                .soapAction("Hello")
                .payload("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say Hello!" +
                         "</Hello>");

        soap().client(soapClient)
                .receive()
                .payload("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Hello!" +
                         "</HelloResponse>");
    }

    @CitrusTest
    public void testGoodByeRequest() {
        soap().client(soapClient)
                .send()
                .soapAction("GoodBye")
                .payload("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say GoodBye!" +
                         "</GoodBye>");

        soap().client(soapClient)
                .receive()
                .payload("<GoodByeResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "GoodBye!" +
                         "</GoodByeResponse>");
    }

    @CitrusTest
    public void testGoodNightRequest() {
        soap().client(soapClient)
                .send()
                .soapAction("GoodNight")
                .payload("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Go to sleep!" +
                         "</GoodNight>");

        soap().client(soapClient)
                .receive()
                .payload("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "@ignore@" +
                        "</GoodNightResponse>");
    }

    @CitrusTest
    public void testUnknownRequest() {
        assertSoapFault()
                .faultActor("SERVER")
                .faultCode("{http://localhost:8080/HelloService/v1}HELLO:ERROR-1100")
                .faultString("No matching scenario found")
                .when(
                    soap().client(soapClient)
                        .send()
                        .soapAction("SomethingElse")
                        .payload("<SomethingElse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                                "Say something else!" +
                                "</SomethingElse>"));
    }

    @CitrusTest
    public void testInvalidSoapAction() {
        assertSoapFault()
                .faultActor("SERVER")
                .faultCode("{http://localhost:8080/HelloService/v1}HELLO:ERROR-1001")
                .faultString("Internal server error")
                .when(
                    soap().client(soapClient)
                        .send()
                        .soapAction("SomethingElse")
                        .payload("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                                "Say Hello!" +
                                "</Hello>"));
    }

    @Configuration
    public static class EndpointConfig {

        @Bean
        public XsdSchemaRepository schemaRepository() {
            XsdSchemaRepository schemaRepository = new XsdSchemaRepository();
            schemaRepository.getLocations().add("classpath:xsd/Hello.wsdl");
            return schemaRepository;
        }

        @Bean
        public WebServiceClient simulatorClient() {
            return CitrusEndpoints.soap().client()
                    .defaultUri(String.format("http://localhost:%s/services/ws/HelloService/v1", 8080))
                    .interceptor(loggingClientInterceptor())
                    .messageFactory(messageFactory())
                    .build();
        }

        @Bean
        public SaajSoapMessageFactory messageFactory() {
            return new SaajSoapMessageFactory();
        }

        @Bean
        public LoggingClientInterceptor loggingClientInterceptor() {
            return new LoggingClientInterceptor();
        }

        @Bean
        @ConditionalOnProperty(name = "simulator.mode", havingValue = "embedded", matchIfMissing = true)
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
