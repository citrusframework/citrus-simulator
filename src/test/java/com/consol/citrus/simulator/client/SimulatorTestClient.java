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
import com.consol.citrus.message.MessageSender;
import com.consol.citrus.validation.xml.DomXmlMessageValidator;
import com.consol.citrus.ws.message.SoapReplyMessageReceiver;
import com.consol.citrus.ws.message.WebServiceMessageSender;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

/**
 * @author Christoph Deppisch
 */
public class SimulatorTestClient {

    /** Message sender/receiver components */
    private WebServiceMessageSender soapRequestSender;
    private SoapReplyMessageReceiver soapResponseReceiver;

    /**
     * Default constructor
     */
    public SimulatorTestClient() throws Exception {
        soapRequestSender = new WebServiceMessageSender();
        soapRequestSender.setDefaultUri("http://localhost:18080/simulator");

        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
        messageFactory.afterPropertiesSet();
        soapRequestSender.setMessageFactory(messageFactory);
        soapRequestSender.setErrorHandlingStrategy(MessageSender.ErrorHandlingStrategy.PROPAGATE);
        soapRequestSender.afterPropertiesSet();

        soapResponseReceiver = new SoapReplyMessageReceiver();
        soapRequestSender.setReplyMessageHandler(soapResponseReceiver);
    }

    /**
     * Sends a hello request to server expecting positive response message.
     */
    public void sendHelloRequest() {
        CitrusTestBuilder testBuilder = new CitrusTestBuilder() {
            @Override
            protected void configure() {
                send(soapRequestSender)
                        .payload("<Hello>Say Hello!</Hello>");

                receive(soapResponseReceiver)
                        .validator(new DomXmlMessageValidator())
                        .payload("<HelloResponse>Hi there!</HelloResponse>");
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
                send(soapRequestSender)
                        .payload("<GoodBye>Say GoodBye!</GoodBye>");

                receive(soapResponseReceiver)
                        .validator(new DomXmlMessageValidator())
                        .payload("<GoodByeResponse>Bye bye!</GoodByeResponse>");
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
                send(soapRequestSender)
                        .payload("<GoodNight>Go to sleep!</GoodNight>");

                receive(soapResponseReceiver)
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
