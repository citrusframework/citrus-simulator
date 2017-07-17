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

package com.consol.citrus.simulator.sample.starter;

import com.consol.citrus.dsl.design.TestDesigner;
import com.consol.citrus.simulator.model.ScenarioParameter;
import com.consol.citrus.simulator.sample.variables.Name;
import com.consol.citrus.simulator.sample.variables.Variables;
import com.consol.citrus.simulator.scenario.*;
import com.consol.citrus.ws.client.WebServiceClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;

@Starter("HelloStarter")
public class HelloStarter implements ScenarioStarter {

    @Autowired
    private WebServiceClient webServiceClient;

    @Override
    public void run(TestDesigner designer) {
        designer.echo(String.format("Saying hello to: %s", Variables.NAME_PH));

        designer.send(webServiceClient)
                .payload("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            Variables.NAME_PH +
                        "</Hello>")
                .header("citrus_soap_action", "Hello");

        designer.receive(webServiceClient)
                .payload("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Hi there " + Variables.NAME_PH +
                        "</HelloResponse>");
    }

    @Override
    public Collection<ScenarioParameter> getScenarioParameters() {
        return Arrays.asList(new Name().asScenarioParameter());
    }
}
