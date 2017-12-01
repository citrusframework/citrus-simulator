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

package com.consol.citrus.simulator.sample.model;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ValidateIbanResponseTest {
    @Test
    public void shouldMarshalAndUnmarshal() {
        ValidateIbanResponse expectedResponse = ValidateIbanResponse.builder()
                .bankAccount(BankAccount.builder()
                        .accountNumber("123456")
                        .bank("The big bank")
                        .bic("ABCDEF")
                        .iban("DEXX12345671234567")
                        .sortCode("111111")
                        .build())
                .valid(true)
                .build();

        String json = expectedResponse.asJson();
        ValidateIbanResponse response = ValidateIbanResponse.fromJson(json);
        Assert.assertEquals(response, expectedResponse);
    }

}