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

package org.citrusframework.simulator.web.rest;

import org.citrusframework.simulator.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link ScenarioResource} REST controller.
 * <p>
 * The scanned scenarios and starters come
 * from {@link org.citrusframework.simulator.service.impl.ScenarioLookupServiceImplTest}.
 */
@IntegrationTest
@AutoConfigureMockMvc
class ScenarioResourceIT {

    private static final String ENTITY_API_URL = "/api/scenarios";
    private static final String ENTITY_API_URL_SCENARIO_NAME = ENTITY_API_URL + "/{scenarioName}";

    @Autowired
    private MockMvc restScenarioParameterMockMvc;

    @Test
    void getAllScenarioNames() throws Exception {
        // Get all the scenarioParameterList
        restScenarioParameterMockMvc
            .perform(get(ENTITY_API_URL))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.length()").value(equalTo(2)))
            .andExpect(jsonPath("$.[0].name").value(equalTo("testScenarioStarter")))
            .andExpect(jsonPath("$.[0].type").value(equalTo("STARTER")))
            .andExpect(jsonPath("$.[1].name").value(equalTo("testSimulatorScenario")))
            .andExpect(jsonPath("$.[1].type").value(equalTo("MESSAGE_TRIGGERED")));
    }

    @Test
    void getAllScenarioNamesDesc() throws Exception {
        // Get all the scenarioParameterList
        restScenarioParameterMockMvc
            .perform(get(ENTITY_API_URL+"?sort=name,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.length()").value(equalTo(2)))
            .andExpect(jsonPath("$.[0].name").value(equalTo("testSimulatorScenario")))
            .andExpect(jsonPath("$.[0].type").value(equalTo("MESSAGE_TRIGGERED")))
            .andExpect(jsonPath("$.[1].name").value(equalTo("testScenarioStarter")))
            .andExpect(jsonPath("$.[1].type").value(equalTo("STARTER")));
    }

    @Test
    void getAllScenarioNamesByTypeDesc() throws Exception {
        // Get all the scenarioParameterList
        restScenarioParameterMockMvc
            .perform(get(ENTITY_API_URL+"?sort=type,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.length()").value(equalTo(2)))
            .andExpect(jsonPath("$.[0].name").value(equalTo("testSimulatorScenario")))
            .andExpect(jsonPath("$.[0].type").value(equalTo("MESSAGE_TRIGGERED")))
            .andExpect(jsonPath("$.[1].name").value(equalTo("testScenarioStarter")))
            .andExpect(jsonPath("$.[1].type").value(equalTo("STARTER")));
    }

    @Test
    void getAllScenarioStarterParameters() throws Exception {
        // Get all the scenarioParameterList
        restScenarioParameterMockMvc
            .perform(get(ENTITY_API_URL_SCENARIO_NAME + "/parameters", "testScenarioStarter"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.length()").value(equalTo(1)))
            .andExpect(jsonPath("$.[0].name").value(equalTo("parameter-name")))
            .andExpect(jsonPath("$.[0].value").value(equalTo("parameter-value")));
    }
}
