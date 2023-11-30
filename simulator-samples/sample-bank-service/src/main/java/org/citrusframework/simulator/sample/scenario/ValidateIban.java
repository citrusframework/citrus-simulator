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

package org.citrusframework.simulator.sample.scenario;

import org.citrusframework.TestAction;
import org.citrusframework.endpoint.resolver.DynamicEndpointUriResolver;
import org.citrusframework.simulator.sample.service.BankService;
import org.citrusframework.simulator.sample.service.QueryParameterService;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.citrusframework.dsl.MessageSupport.MessageHeaderSupport.fromHeaders;
import static org.citrusframework.simulator.sample.model.QueryParameter.ACCOUNT_NUMBER;
import static org.citrusframework.simulator.sample.model.QueryParameter.IBAN;
import static org.citrusframework.simulator.sample.model.QueryParameter.SORT_CODE;
import static org.citrusframework.simulator.sample.model.Variable.JSON_RESPONSE;
import static org.citrusframework.simulator.sample.model.Variable.QUERY_PARAMS;

@Scenario("ValidateIBAN")
@RequestMapping(method = RequestMethod.GET, value = "/services/rest/bank", params = {
        IBAN,
        "!" + SORT_CODE,
        "!" + ACCOUNT_NUMBER
})
public class ValidateIban extends AbstractSimulatorScenario {

    private final BankService bankService;
    private final QueryParameterService queryParameterService;

    public ValidateIban(BankService bankService, QueryParameterService queryParameterService) {
        this.bankService = bankService;
        this.queryParameterService = queryParameterService;
    }

    @Override
    public void run(ScenarioRunner scenario) {
        scenario.$(scenario.http()
                .receive()
                    .get()
                    .message()
                    .extract(fromHeaders().header(DynamicEndpointUriResolver.QUERY_PARAM_HEADER_NAME, QUERY_PARAMS.name())));

        scenario.run(validateIban());

        scenario.$(
            scenario.http()
                .sendOkJson(JSON_RESPONSE.placeholder())
        );
    }

    private TestAction validateIban() {
        return context -> {
            final String queryParams = context.getVariable(QUERY_PARAMS.name(), String.class);
            final String iban = queryParameterService.getIban(queryParams);
            final String jsonResponse = bankService.validate(iban).asJson();
            context.setVariable(JSON_RESPONSE.name(), jsonResponse);
        };
    }
}
