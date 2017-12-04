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
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.simulator.sample.model.BankAccount;
import com.consol.citrus.simulator.sample.model.CalculateIbanResponse;
import com.consol.citrus.simulator.sample.model.ValidateIbanResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

@Test
public class BankServiceSimulatorIT extends TestNGCitrusTestDesigner {

    /**
     * Test Http REST client
     */
    @Autowired
    @Qualifier("simulatorHttpClientEndpoint")
    private HttpClient simulatorClient;

    /**
     * Sends a hello request to server expecting positive response message.
     */
    @CitrusTest
    public void testCalculate() {
        http().client(simulatorClient)
                .send()
                .get("/services/rest/bank")
                .queryParam("sortCode", "12345670")
                .queryParam("accountNumber", "0006219653")
        ;

        http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .contentType("application/json;charset=UTF-8")
                .payload(CalculateIbanResponse.builder()
                        .bankAccount(BankAccount.builder()
                                .iban("DE92123456700006219653")
                                .bic("ABCDEFG5670")
                                .bank("The Wealthy ABC bank")
                                .sortCode("12345670")
                                .accountNumber("0006219653")
                                .build()
                        )
                        .build().asJson()
                )
        ;
    }

    /**
     * Sends goodbye request to server expecting positive response message.
     */
    @CitrusTest
    public void testValidate() {
        http().client(simulatorClient)
                .send()
                .get("/services/rest/bank")
                .queryParam("iban", "DE92123456700006219653")
        ;

        http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .contentType("application/json;charset=UTF-8")
                .payload(ValidateIbanResponse.builder()
                        .bankAccount(BankAccount.builder()
                                .iban("DE92123456700006219653")
                                .bic("ABCDEFG5670")
                                .bank("The Wealthy ABC bank")
                                .sortCode("12345670")
                                .accountNumber("0006219653")
                                .build()
                        )
                        .valid(true)
                        .build().asJson()
                )
        ;
    }

}
