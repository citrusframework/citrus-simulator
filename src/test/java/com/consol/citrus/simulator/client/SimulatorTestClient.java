/*
 * Copyright 2006-2014 the original author or authors.
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

package com.consol.citrus.simulator.client;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.CitrusTestBuilder;
import com.consol.citrus.message.ErrorHandlingStrategy;
import com.consol.citrus.validation.xml.DomXmlMessageValidator;
import com.consol.citrus.ws.client.WebServiceClient;
import com.consol.citrus.ws.client.WebServiceEndpointConfiguration;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

/**
 * @author Christoph Deppisch
 */
public class SimulatorTestClient {

    private WebServiceClient soapEndpoint;

    /**
     * Default constructor
     */
    public SimulatorTestClient() throws Exception {
        WebServiceEndpointConfiguration endpointConfiguration = new WebServiceEndpointConfiguration();
        endpointConfiguration.setDefaultUri("http://localhost:8080/simulator");

        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
        messageFactory.afterPropertiesSet();
        endpointConfiguration.setMessageFactory(messageFactory);
        endpointConfiguration.setErrorHandlingStrategy(ErrorHandlingStrategy.PROPAGATE);

        soapEndpoint = new WebServiceClient(endpointConfiguration);
    }

    /**
     * Sends a hello request to server expecting positive response message.
     */
    public void sendHelloRequest() {
        CitrusTestBuilder testBuilder = new CitrusTestBuilder() {
            @Override
            protected void configure() {
                send(soapEndpoint)
                        .payload("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                                    "Say Hello!" +
                                 "</Hello>");

                receive(soapEndpoint)
                        .validator(new DomXmlMessageValidator())
                        .payload("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                                    "Hi there!" +
                                 "</HelloResponse>");
            }
        };

        testBuilder.execute(new TestContext());
    }

    /**
     * Sends goodbye request to server expecting positive response message.
     */
    public void sendGoodByeRequest() {
        CitrusTestBuilder testBuilder = new CitrusTestBuilder() {
            @Override
            protected void configure() {
                send(soapEndpoint)
                        .payload("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">" +
                                    "Say GoodBye!" +
                                 "</GoodBye>");

                receive(soapEndpoint)
                        .validator(new DomXmlMessageValidator())
                        .payload("<GoodByeResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                                    "Bye bye!" +
                                 "</GoodByeResponse>");
            }
        };

        testBuilder.execute(new TestContext());
    }

    /**
     * Sends SOAP fault forcing request type to server expecting SOAP fault response message.
     */
    public void sendGoodNightRequest() {
        CitrusTestBuilder testBuilder = new CitrusTestBuilder() {
            @Override
            protected void configure() {
                send(soapEndpoint)
                        .payload("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                                    "Go to sleep!" +
                                 "</GoodNight>");

                receive(soapEndpoint)
                        .validator(new DomXmlMessageValidator())
                        .payload("<SOAP-ENV:Fault xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                                    "<faultcode>CITRUS:SIM-1001</faultcode>\n" +
                                    "<faultstring xmlns:xml=\"http://www.w3.org/XML/1998/namespace\" xml:lang=\"en\">" +
                                            "No sleep for me!" +
                                    "</faultstring>\n" +
                                "</SOAP-ENV:Fault>");
            }
        };

        testBuilder.execute(new TestContext());
    }

    /**
     * Send some test requests to server and expect response messages.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        SimulatorTestClient client = new SimulatorTestClient();
        client.sendHelloRequest();
        client.sendGoodByeRequest();
        client.sendGoodNightRequest();
    }
}
