/*
 * Copyright the original author or authors.
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

package org.citrusframework.simulator.sample.starter;

import java.util.Collection;
import java.util.Collections;

import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.sample.variables.Name;
import org.citrusframework.simulator.sample.variables.Variables;
import org.citrusframework.simulator.scenario.AbstractScenarioStarter;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.simulator.scenario.Starter;
import org.citrusframework.ws.client.WebServiceClient;
import org.springframework.beans.factory.annotation.Autowired;

import static org.citrusframework.actions.EchoAction.Builder.echo;
import static org.citrusframework.actions.ReceiveMessageAction.Builder.receive;
import static org.citrusframework.actions.SendMessageAction.Builder.send;

@Starter("HelloStarter")
public class HelloStarter extends AbstractScenarioStarter {

    @Autowired
    private WebServiceClient webServiceClient;

    @Override
    public void run(ScenarioRunner scenario) {
        scenario.$(echo(String.format("Saying hello to: %s", Variables.NAME_PH)));

        scenario.$(send()
                .endpoint(webServiceClient)
                .message()
                .body("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            Variables.NAME_PH +
                        "</Hello>")
                .header("citrus_soap_action", "Hello"));

        scenario.$(receive()
                .endpoint(webServiceClient)
                .message()
                .body("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Hi there " + Variables.NAME_PH +
                        "</HelloResponse>"));
    }

    @Override
    public Collection<ScenarioParameter> getScenarioParameters() {
        return Collections.singletonList(new Name().asScenarioParameter());
    }
}
