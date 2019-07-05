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

package ${package};

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.ws.client.WebServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

@Test
public class SimulatorWebServiceIT extends TestNGCitrusTestDesigner {

    @Autowired
    private WebServiceClient soapClient;

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

        send(soapClient)
                .payload("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Go to sleep!" +
                        "</GoodNight>")
                .header("citrus_soap_action", "GoodNight");

        receive(soapClient)
                .payload("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Good Night!" +
                        "</GoodNightResponse>");
    }

    @CitrusTest
    public void testUnknownRequest() {
        soap().client(soapClient)
                .send()
                .soapAction("SomethingElse")
                .payload("<SomethingElse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say something else!" +
                        "</SomethingElse>");

        soap().client(soapClient)
                .receive()
                .schemaValidation(false)
                .payload("<SOAP-ENV:Fault xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                            "<faultcode>CITRUS:SIM-1100</faultcode>\n" +
                            "<faultstring xmlns:xml=\"http://www.w3.org/XML/1998/namespace\" xml:lang=\"en\">" +
                                "No matching scenario found" +
                            "</faultstring>\n" +
                            "<faultactor>SERVER</faultactor>\n" +
                        "</SOAP-ENV:Fault>");
    }
}
