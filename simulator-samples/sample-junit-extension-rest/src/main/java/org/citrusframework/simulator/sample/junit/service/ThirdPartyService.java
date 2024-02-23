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

package org.citrusframework.simulator.sample.junit.service;

import org.citrusframework.simulator.sample.junit.client.ThirdPartyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThirdPartyService {

    private static final String PRENAME = "prename";
    private static final String LASTNAME = "lastname";

    private final ThirdPartyClient thirdPartyClient;

    @Autowired
    public ThirdPartyService(ThirdPartyClient thirdPartyClient) {
        this.thirdPartyClient = thirdPartyClient;
    }

    public String calculateFullName(String prename, String lastname) {
        var variables = thirdPartyClient.nameVariables(prename,lastname);
        return variables.getOrDefault(PRENAME, "[" + PRENAME.toUpperCase() + "]") + " " + variables.getOrDefault(LASTNAME, "[" + LASTNAME.toUpperCase() + "]");
    }
}
