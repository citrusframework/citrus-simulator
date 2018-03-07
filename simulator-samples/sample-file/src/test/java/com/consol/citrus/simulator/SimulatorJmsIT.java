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
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import com.consol.citrus.endpoint.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

@Test
public class SimulatorJmsIT extends TestNGCitrusTestRunner {

    private String defaultResponse = "<DefaultResponse>This is a default response!</DefaultResponse>";

    @Autowired
    @Qualifier("simulatorFileCitrusInboundEndpoint")
    Endpoint citrusFileInboundEndpoint;

    @Autowired
    @Qualifier("simulatorFileCitrusOutboundEndpoint")
    Endpoint citrusFileOutboundEndpoint;

    @CitrusTest
    public void testHelloRequest() {
        String filenameBase = "Hello";
        String filename = filenameBase + ".xml";
        String filenameDone = filenameBase + ".done";

        send(builder -> builder.endpoint(citrusFileOutboundEndpoint)
            .payload("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                     "Say Hello!" +
                     "</Hello>")
            .header("file_name", filename));
        send(builder -> builder.endpoint(citrusFileOutboundEndpoint)
            .header("file_name", filenameDone));

        receive(builder -> builder.endpoint(citrusFileInboundEndpoint)
            .payload("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                     "Hi there!" +
                     "</HelloResponse>"));
    }

    @CitrusTest
    public void testGoodByeRequest() {
        String filenameBase = "GoodBye";
        String filename = filenameBase + ".xml";
        String filenameDone = filenameBase + ".done";
        send(builder -> builder.endpoint(citrusFileOutboundEndpoint)
            .payload("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">" +
                     "Say GoodBye!" +
                     "</GoodBye>")
            .header("file_name", filename));
        send(builder -> builder.endpoint(citrusFileOutboundEndpoint)
            .header("file_name", filenameDone));

        receive(builder -> builder.endpoint(citrusFileInboundEndpoint)
            .payload("<GoodByeResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                     "Bye bye!" +
                     "</GoodByeResponse>"));
    }

    @CitrusTest
    public void testDefaultRequest() {
        String filenameBase = "Default";
        String filename = filenameBase + ".xml";
        String filenameDone = filenameBase + ".done";

        send(builder -> builder.endpoint(citrusFileOutboundEndpoint)
            .payload("<Default>" +
                     "Should trigger default scenario" +
                     "</Default>")
            .header("file_name", filename));
        send(builder -> builder.endpoint(citrusFileOutboundEndpoint)
            .header("file_name", filenameDone));

        receive(builder -> builder.endpoint(citrusFileInboundEndpoint)
            .payload(defaultResponse));
    }
}
