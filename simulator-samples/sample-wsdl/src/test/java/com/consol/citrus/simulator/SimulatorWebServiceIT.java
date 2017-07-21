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
public class SimulatorWebServiceIT extends TestNGCitrusTestDesigner {

    @Autowired
    private WebServiceClient soapClient;

    @CitrusTest
    public void testHelloRequest() {
        send(soapClient)
                .payload("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">string</Hello>")
                .header("citrus_soap_action", "Hello");

        receive(soapClient)
                .payload("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">string</HelloResponse>");
    }

    @CitrusTest
    public void testGoodByeRequest() {
        send(soapClient)
                .payload("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">string</GoodBye>")
                .header("citrus_soap_action", "GoodBye");

        receive(soapClient)
                .payload("<GoodByeResponse xmlns=\"http://citrusframework.org/schemas/hello\">string</GoodByeResponse>");
    }

    @CitrusTest
    public void testGoodNightRequest() {
        send(soapClient)
                .payload("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">string</GoodNight>")
                .header("citrus_soap_action", "GoodNight");

        receive(soapClient)
                .payload("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">string</GoodNightResponse>");
    }
}
