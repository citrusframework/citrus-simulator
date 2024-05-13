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
import org.citrusframework.simulator.sample.Simulator;
import org.citrusframework.testng.spring.TestNGCitrusSpringSupport;
import org.citrusframework.ws.client.WebServiceClient;
import org.citrusframework.ws.interceptor.LoggingClientInterceptor;
import org.citrusframework.xml.XsdSchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.testng.annotations.Test;

import static org.citrusframework.ws.actions.SoapActionBuilder.soap;

/**
 * @author Christoph Deppisch
 */
@Test
@ContextConfiguration(classes = SimulatorWebServiceWithWsdlIT.EndpointConfig.class)
public class SimulatorWebServiceWithWsdlIT extends TestNGCitrusSpringSupport {

    @Autowired
    private WebServiceClient soapClient;

    @CitrusTest
    public void testHelloRequest() {
        $(soap().client(soapClient)
                .send()
                .message()
                .soapAction("Hello")
                .body("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say Hello!" +
                         "</Hello>"));

        $(soap().client(soapClient)
                .receive()
                .message()
                .body("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Hello!" +
                         "</HelloResponse>"));
    }

    @CitrusTest
    public void testGoodByeRequest() {
        $(soap().client(soapClient)
                .send()
                .message()
                .soapAction("GoodBye")
                .body("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say GoodBye!" +
                         "</GoodBye>"));

        $(soap().client(soapClient)
                .receive()
                .message()
                .body("<GoodByeResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "GoodBye!" +
                         "</GoodByeResponse>"));
    }

    @CitrusTest
    public void testGoodNightRequest() {
        $(soap().client(soapClient)
                .send()
                .message()
                .soapAction("GoodNight")
                .body("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Go to sleep!" +
                         "</GoodNight>"));

        $(soap().client(soapClient)
                .receive()
                .message()
                .body("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "@ignore@" +
                        "</GoodNightResponse>"));
    }

    @CitrusTest
    public void testUnknownRequest() {
        $(soap().client(soapClient)
                .assertFault()
                .faultActor("SERVER")
                .faultCode("{http://localhost:8080/HelloService/v1}HELLO:ERROR-1100")
                .faultString("No matching scenario found")
                .when(
                    soap().client(soapClient)
                        .send()
                        .message()
                        .soapAction("SomethingElse")
                        .body("<SomethingElse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                                "Say something else!" +
                                "</SomethingElse>")));
    }

    @CitrusTest
    public void testInvalidSoapAction() {
        $(soap().client(soapClient)
                .assertFault()
                .faultActor("SERVER")
                .faultCode("{http://localhost:8080/HelloService/v1}HELLO:ERROR-1001")
                .faultString("Internal server error")
                .when(
                    soap().client(soapClient)
                        .send()
                        .message()
                        .soapAction("SomethingElse")
                        .body("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                                "Say Hello!" +
                                "</Hello>")));
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
        @ConditionalOnProperty(name = "simulator.mode", havingValue = "embedded")
        public BeforeSuite startEmbeddedSimulator() {
            return new SequenceBeforeSuite.Builder().actions(context -> SpringApplication.run(Simulator.class)).build();
        }
    }
}
