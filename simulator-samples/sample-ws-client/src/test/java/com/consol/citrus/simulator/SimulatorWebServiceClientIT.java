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
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.simulator.model.ScenarioParameter;
import com.consol.citrus.simulator.sample.variables.Name;
import com.consol.citrus.ws.server.WebServiceServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * Verifies that the webservice client is working properly.
 *
 * @author Martin Maher
 */
@Test
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

}
