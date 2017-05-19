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
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import com.consol.citrus.simulator.sample.model.xml.greeting.PayloadHelper;
import com.consol.citrus.simulator.sample.model.xml.greeting.GreetingEnumType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

/**
 * @author Martin Maher
 */
@Test
public class SimulatorJmsIT extends TestNGCitrusTestDesigner {

    private String defaultResponse = "<DefaultResponse>This is a default response!</DefaultResponse>";

    private PayloadHelper payloadHelper = new PayloadHelper();

    @Autowired
    @Qualifier("simulatorInboundEndpoint")
    private JmsEndpoint simulatorInboundEndpoint;

    @Autowired
    @Qualifier("simulatorOutboundEndpoint")
    private JmsEndpoint simulatorOutboundEndpoint;

    /**
     * Sends a good-morning message to the simulator and expects to receive a good-morning message back.
     */
    @CitrusTest
    public void testGoodMorning() {
        send(simulatorInboundEndpoint)
                .payload(payloadHelper.generateGreetingMessage("Application", GreetingEnumType.MORNING),
                        payloadHelper.getMarshaller());

        receive(simulatorOutboundEndpoint)
                .payload(payloadHelper.generateGreetingMessage("Simulator", GreetingEnumType.MORNING),
                        payloadHelper.getMarshaller());

    }

    /**
     * Sends good afternoon and evening messages to the simulator and expects to receive the same messages back
     */
    @CitrusTest
    public void testGoodAternoonAndEvening() {
        send(simulatorInboundEndpoint)
                .payload(payloadHelper.generateGreetingMessage("Application", GreetingEnumType.AFTERNOON),
                        payloadHelper.getMarshaller());

        receive(simulatorOutboundEndpoint)
                .payload(payloadHelper.generateGreetingMessage("Simulator", GreetingEnumType.AFTERNOON),
                        payloadHelper.getMarshaller());

        send(simulatorInboundEndpoint)
                .payload(payloadHelper.generateGreetingMessage("Application", GreetingEnumType.EVENING),
                        payloadHelper.getMarshaller());

        receive(simulatorOutboundEndpoint)
                .payload(payloadHelper.generateGreetingMessage("Simulator", GreetingEnumType.EVENING),
                        payloadHelper.getMarshaller());
    }

    /**
     * Sends some other request to server expecting positive default response message.
     */
    @CitrusTest
    public void testDefaultRequest() {
        send(simulatorInboundEndpoint)
                .payload("<Default>" +
                        "Should trigger default scenario" +
                        "</Default>");

        receive(simulatorOutboundEndpoint)
                .payload(defaultResponse);
    }

    /**
     * Sends some intervening request to server expecting positive response message.
     */
    @CitrusTest
    public void testInterveningRequest() {
//
//        variable("correlationId", "citrus:randomNumber(10)");
//
//        send(jmsSyncEndpoint)
//                .payload("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
//                        "Go to sleep!" +
//                        "</GoodNight>")
//                .header("correlationId", "${correlationId}");
//
//        receive(jmsSyncEndpoint)
//                .payload("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
//                        "Good Night!" +
//                        "</GoodNightResponse>");
//
//        send(jmsSyncEndpoint)
//                .payload("<InterveningRequest>In between!</InterveningRequest>");
//
//        receive(jmsSyncEndpoint)
//                .payload(defaultResponse);
//
//        send(jmsSyncEndpoint)
//                .payload("<InterveningRequest>In between!</InterveningRequest>")
//                .header("correlationId", "${correlationId}");
//
//        receive(jmsSyncEndpoint)
//                .payload("<InterveningResponse>In between!</InterveningResponse>");
//
//        send(jmsSyncEndpoint)
//                .payload("<InterveningRequest>In between!</InterveningRequest>")
//                .header("correlationId", "${correlationId}");
//
//        receive(jmsSyncEndpoint)
//                .payload(defaultResponse);
    }
}
