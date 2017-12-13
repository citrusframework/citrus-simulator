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

import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.simulator.model.ScenarioParameter;
import com.consol.citrus.simulator.model.ScenarioParameterBuilder;
import com.consol.citrus.simulator.sample.model.QueryParameter;
import com.consol.citrus.simulator.sample.model.Variable;
import com.consol.citrus.simulator.scenario.AbstractScenarioStarter;
import com.consol.citrus.simulator.scenario.ScenarioRunner;
import com.consol.citrus.simulator.scenario.Starter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * This starter can be used to test the simulator scenario
 * {@link com.consol.citrus.simulator.sample.scenario.CalculateIban}.
 * <br />It does this by sending a HTTP request to the Rest Service Calculate-IBAN. The simulator is configured to
 * forward all Calculate-IBAN requests to the CalculateIban scenario which sends back a HTTP response containing the
 * calculated IBAN.
 */
@Starter("TestCalculate")
public class CalculateStarter extends AbstractScenarioStarter {

    private final HttpClient client;

    public CalculateStarter(@Qualifier("simulatorHttpClientEndpoint") HttpClient client) {
        this.client = client;
    }

    @Override
    public void run(ScenarioRunner scenario) {
        scenario.echo("Sending request to Calculate-IBAN REST Service...");

        scenario.http(builder -> builder.client(client)
                    .send()
                    .get("/services/rest/bank")
                    .queryParam(QueryParameter.SORT_CODE, Variable.SORT_CODE.placeholder())
                    .queryParam(QueryParameter.ACCOUNT_NUMBER, Variable.ACCOUNT.placeholder()));

        scenario.echo("Receiving response from Calculate-IBAN REST Service...");

        scenario.http(builder -> builder.client(client)
                    .receive()
                    .response(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON_UTF8.toString()));

        scenario.echo("Response received from Calculate-IBAN REST Service");
    }

    @Override
    public Collection<ScenarioParameter> getScenarioParameters() {
        List<ScenarioParameter> scenarioParameters = new ArrayList<>();

        // bank account (text box)
        scenarioParameters.add(new ScenarioParameterBuilder()
                .name(Variable.ACCOUNT.name())
                .label("Bank Account")
                .required()
                .textbox()
                .value("6219653")
                .build());

        // sort code (text box)
        scenarioParameters.add(new ScenarioParameterBuilder()
                .name(Variable.SORT_CODE.name())
                .label("Sort Code")
                .required()
                .textbox()
                .value("12345670")
                .build());

        return scenarioParameters;
    }
}
