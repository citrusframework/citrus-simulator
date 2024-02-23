/*
 * Copyright 2024 the original author or authors.
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

package org.citrusframework.simulator.sample.junit.scenario;

import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Scenario("Name")
@RequestMapping(value = "/services/rest/third/party/name/{prename}/{lastname}", method = GET)
public class ThirdPartyNameScenario extends AbstractSimulatorScenario {

    @Override
    public void run(ScenarioRunner scenario) {
        var prename = new AtomicReference<>("");
        var lastname = new AtomicReference<>("");

        scenario.$(
            scenario.http()
                .receive()
                .get()
                .message()
                .extract((message, testContext) -> {
                    if (message instanceof HttpMessage httpMessage) {
                        var pathSegements = httpMessage.getPath().split("/");
                        prename.set(pathSegements[pathSegements.length - 2]);
                        lastname.set(pathSegements[pathSegements.length - 1]);
                    }
                })
        );

        scenario.$(
            scenario.http()
                .sendOkJson(
                    Map.of(
                        "prename", prename.get(),
                        "lastname", lastname.get()
                    )
                )
        );
    }
}
