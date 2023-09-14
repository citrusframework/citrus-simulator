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
import org.citrusframework.message.ErrorHandlingStrategy;
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

import static org.citrusframework.actions.ReceiveMessageAction.Builder.receive;
import static org.citrusframework.actions.SendMessageAction.Builder.send;
import static org.citrusframework.validation.xml.XmlMessageValidationContext.Builder.xml;
import static org.citrusframework.ws.actions.SoapActionBuilder.soap;

/**
 * @author Christoph Deppisch
 */
@Test
@ContextConfiguration(classes = SimulatorWebServiceIT.EndpointConfig.class)
public class SimulatorWebServiceIT extends TestNGCitrusSpringSupport {

    @Autowired
    private WebServiceClient soapClient;

    @CitrusTest
    public void testHelloRequest() {
        $(send(soapClient)
                .message()
                .body("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say Hello!" +
                         "</Hello>")
                .header("citrus_soap_action", "Hello"));

        $(receive(soapClient)
                .message()
                .body("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Hi there!" +
                         "</HelloResponse>"));
    }

    @CitrusTest
    public void testGoodByeRequest() {
        $(send(soapClient)
                .message()
                .body("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say GoodBye!" +
                         "</GoodBye>")
                .header("citrus_soap_action", "GoodBye"));

        $(receive(soapClient)
                .message()
                .body("<GoodByeResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Bye bye!" +
                         "</GoodByeResponse>"));
    }

    @CitrusTest
    public void testGoodNightRequest() {
        $(send(soapClient)
                .message()
                .body("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Go to sleep!" +
                         "</GoodNight>")
                .header("citrus_soap_action", "GoodNight"));

        $(receive(soapClient)
                .message()
                .validate(xml().schemaValidation(false))
                .body("<SOAP-ENV:Fault xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                            "<faultcode>CITRUS:SIM-1001</faultcode>\n" +
                            "<faultstring xmlns:xml=\"http://www.w3.org/XML/1998/namespace\" xml:lang=\"en\">" +
                                "No sleep for me!" +
                            "</faultstring>\n" +
                        "</SOAP-ENV:Fault>"));

        $(send(soapClient)
                .message()
                .body("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Go to sleep!" +
                        "</GoodNight>")
                .header("citrus_soap_action", "GoodNight"));

        $(receive(soapClient)
                .message()
                .body("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Good Night!" +
                        "</GoodNightResponse>"));
    }

    @CitrusTest
    public void testUnknownRequest() {
        $(soap().client(soapClient)
                .send()
                .message()
                .soapAction("SomethingElse")
                .body("<SomethingElse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say something else!" +
                        "</SomethingElse>"));

        $(soap().client(soapClient)
                .receive()
                .message()
                .validate(xml().schemaValidation(false))
                .body("<SOAP-ENV:Fault xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                            "<faultcode>CITRUS:SIM-1100</faultcode>\n" +
                            "<faultstring xmlns:xml=\"http://www.w3.org/XML/1998/namespace\" xml:lang=\"en\">" +
                                "No matching scenario found" +
                            "</faultstring>\n" +
                            "<faultactor>SERVER</faultactor>\n" +
                        "</SOAP-ENV:Fault>"));
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
        public WebServiceClient simulatorClient() {
            return CitrusEndpoints.soap().client()
                    .defaultUri(String.format("http://localhost:%s/services/ws/simulator", 8080))
                    .interceptor(loggingClientInterceptor())
                    .messageFactory(messageFactory())
                    .faultStrategy(ErrorHandlingStrategy.PROPAGATE)
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
