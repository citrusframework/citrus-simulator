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

package org.citrusframework.simulator.sample.starter;

import org.citrusframework.http.client.HttpClient;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.sample.model.QueryParameter;
import org.citrusframework.simulator.sample.model.Variable;
import org.citrusframework.simulator.sample.scenario.ValidateIban;
import org.citrusframework.simulator.scenario.AbstractScenarioStarter;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.simulator.scenario.Starter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static org.citrusframework.actions.EchoAction.Builder.echo;
import static org.citrusframework.http.actions.HttpActionBuilder.http;

/**
 * This starter can be used to test the simulator scenario
 * {@link ValidateIban}.
 * <br />It does this by sending a HTTP request to the
 * Rest Service Validate-IBAN. The simulator is configured to forward all Validate-IBAN requests to the
 * ValidateIban scenario which sends back a HTTP response containing the validation result.
 */
@Starter("TestValidate")
public class ValidateStarter extends AbstractScenarioStarter {

    private final HttpClient client;

    public ValidateStarter(@Qualifier("simulatorHttpClientEndpoint") HttpClient client) {
        this.client = client;
    }

    @Override
    public void run(ScenarioRunner scenario) {
        scenario.$(echo("Sending request to Validate-IBAN REST Service..."));

        scenario.$(http().client(client)
                    .send()
                    .get("/services/rest/bank")
                    .queryParam(QueryParameter.IBAN, Variable.IBAN.placeholder()));

        scenario.$(echo("Receiving response from Validate-IBAN REST Service..."));

        scenario.$(http().client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        scenario.$(echo("Response received from Validate-IBAN REST Service"));
    }

    @Override
    public List<ScenarioParameter> getScenarioParameters() {
        List<ScenarioParameter> scenarioParameters = new ArrayList<>();

        // iban (text box)
        scenarioParameters.add(ScenarioParameter.builder()
                .name(Variable.IBAN.name())
                .label("IBAN")
                .required()
                .dropdown()
                .addOption("DE92123456700006219653", "DE92123456700006219653") // Valid IBAN
                .addOption("DE00123456700006219653", "DE00123456700006219653") // Invalid IBAN
                .build());

        return scenarioParameters;
    }
}
