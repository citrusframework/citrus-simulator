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

package com.consol.citrus.simulator.sample.scenario;

import com.consol.citrus.TestAction;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.endpoint.resolver.DynamicEndpointUriResolver;
import com.consol.citrus.simulator.sample.service.BankService;
import com.consol.citrus.simulator.sample.service.QueryParameterService;
import com.consol.citrus.simulator.scenario.AbstractSimulatorScenario;
import com.consol.citrus.simulator.scenario.Scenario;
import com.consol.citrus.simulator.scenario.ScenarioRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.consol.citrus.simulator.sample.model.QueryParameter.*;
import static com.consol.citrus.simulator.sample.model.Variable.JSON_RESPONSE;
import static com.consol.citrus.simulator.sample.model.Variable.QUERY_PARAMS;

@Scenario("CalculateIBAN")
@RequestMapping(method = RequestMethod.GET, value = "/services/rest/bank", params = {
        SORT_CODE,
        ACCOUNT_NUMBER,
        "!" + IBAN
})
public class CalculateIban extends AbstractSimulatorScenario {
    private final BankService bankService;
    private final QueryParameterService queryParameterService;

    public CalculateIban(BankService bankService, QueryParameterService queryParameterService) {
        this.bankService = bankService;
        this.queryParameterService = queryParameterService;
    }

    @Override
    public void run(ScenarioRunner scenario) {
        scenario
            .http()
            .receive(builder -> builder
                        .get()
                        .extractFromHeader(DynamicEndpointUriResolver.QUERY_PARAM_HEADER_NAME, QUERY_PARAMS.name()));

        scenario.run(calculateIban());

        scenario
            .http()
            .send(builder -> builder
                    .response(HttpStatus.OK)
                    .payload(JSON_RESPONSE.placeholder())
                    .contentType(MediaType.APPLICATION_JSON_UTF8.toString()));
    }

    private TestAction calculateIban() {
        return new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                final String queryParams = context.getVariable(QUERY_PARAMS.name(), String.class);
                final String sortCode = queryParameterService.getSortCode(queryParams);
                final String bankAccountNumber = queryParameterService.getBankAccountNumber(queryParams);
                final String jsonResponse = bankService.calculate(sortCode, bankAccountNumber).asJson();
                context.setVariable(JSON_RESPONSE.name(), jsonResponse);
            }
        };
    }
}
