/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.simulator;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.ws.client.WebServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
@Test
public class SimulatorIT extends TestNGCitrusTestDesigner {

    /** Test SOAP client */
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private WebServiceClient soapClient;

    /**
     * Sends a hello request to server expecting positive response message.
     */
    @CitrusTest
    public void testHelloRequest() {
        send(soapClient)
                .payload("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say Hello!" +
                         "</Hello>")
                .header("citrus_soap_action", "Hello");

        receive(soapClient)
                .payload("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Hi there!" +
                         "</HelloResponse>");

    }

    /**
     * Sends goodbye request to server expecting positive response message.
     */
    @CitrusTest
    public void testGoodByeRequest() {
        send(soapClient)
                .payload("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say GoodBye!" +
                         "</GoodBye>")
                .header("citrus_soap_action", "GoodBye");

        receive(soapClient)
                .payload("<GoodByeResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Bye bye!" +
                         "</GoodByeResponse>");
    }

    /**
     * Sends SOAP fault forcing request type to server expecting SOAP fault response message.
     */
    @CitrusTest
    public void testGoodNightRequest() {
        send(soapClient)
                .payload("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Go to sleep!" +
                         "</GoodNight>")
                .header("citrus_soap_action", "GoodNight");

        receive(soapClient)
                .schemaValidation(false)
                .payload("<SOAP-ENV:Fault xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                            "<faultcode>CITRUS:SIM-1001</faultcode>\n" +
                            "<faultstring xmlns:xml=\"http://www.w3.org/XML/1998/namespace\" xml:lang=\"en\">" +
                                    "No sleep for me!" +
                            "</faultstring>\n" +
                        "</SOAP-ENV:Fault>");
    }
}
