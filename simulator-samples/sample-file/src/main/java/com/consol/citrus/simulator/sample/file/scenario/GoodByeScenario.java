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

package com.consol.citrus.simulator.sample.file.scenario;

import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.simulator.scenario.AbstractSimulatorScenario;
import com.consol.citrus.simulator.scenario.Scenario;
import com.consol.citrus.simulator.scenario.ScenarioRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Scenario("GoodBye")
public class GoodByeScenario extends AbstractSimulatorScenario {

    private final Endpoint fileOutboundEndpoint;

    @Autowired
    public GoodByeScenario(@Qualifier("simulatorFileOutboundEndpoint") Endpoint fileOutboundEndpoint) {
        this.fileOutboundEndpoint = fileOutboundEndpoint;
    }

    @Override
    public void run(ScenarioRunner scenario) {
        String filenameBase = "GoodBye";
        String filename = filenameBase + ".xml";
        String filenameDone = filenameBase + ".done";

        scenario
            .receive(builder -> builder.endpoint(getScenarioEndpoint())
                .payload("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">" +
                         "Say GoodBye!" +
                         "</GoodBye>"));

        scenario
            .send(builder -> builder.endpoint(fileOutboundEndpoint)
                .payload("<GoodByeResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                         "Bye bye!" +
                         "</GoodByeResponse>")
                .header("file_name", filename));
        scenario.send(builder -> builder.endpoint(fileOutboundEndpoint)
            .header("file_name", filenameDone));

    }
}
