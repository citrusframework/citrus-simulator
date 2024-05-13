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
import org.citrusframework.simulator.service.impl.ScenarioLookupServiceImplTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.citrusframework.simulator.service.impl.ScenarioLookupServiceImplTest.SCENARIO_NAME;
import static org.citrusframework.simulator.service.impl.ScenarioLookupServiceImplTest.STARTER_NAME;
import static org.citrusframework.simulator.web.rest.ScenarioResource.Scenario.ScenarioType.MESSAGE_TRIGGERED;
import static org.citrusframework.simulator.web.rest.ScenarioResource.Scenario.ScenarioType.STARTER;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
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
            .andExpect(jsonPath("$.length()").value(greaterThan(2)))
            .andExpect(jsonPath("$.[*]", hasItems(
                allOf(
                    hasEntry("name", STARTER_NAME),
                    hasEntry("type", STARTER.toString())
                ),
                allOf(
                    hasEntry("name", SCENARIO_NAME),
                    hasEntry("type", MESSAGE_TRIGGERED.toString())
                )
            )));
    }

    @Test
    void getAllScenarioNamesDesc() throws Exception {
        // Get all the scenarioParameterList
        restScenarioParameterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=name,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.length()").value(greaterThan(2)))
            .andExpect(jsonPath("$.[*]", hasItems(
                allOf(
                    hasEntry("name", SCENARIO_NAME),
                    hasEntry("type", MESSAGE_TRIGGERED.toString())
                ),
                allOf(
                    hasEntry("name", STARTER_NAME),
                    hasEntry("type", STARTER.toString())
                )
            )));
    }

    @Test
    void getAllScenarioNamesByTypeDesc() throws Exception {
        // Get all the scenarioParameterList
        restScenarioParameterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=type,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.length()").value(greaterThan(2)))
            .andExpect(jsonPath("$.[*]", hasItems(
                allOf(
                    hasEntry("name", SCENARIO_NAME),
                    hasEntry("type", MESSAGE_TRIGGERED.toString())
                ),
                allOf(
                    hasEntry("name", STARTER_NAME),
                    hasEntry("type", STARTER.toString())
                )
            )));
    }

    @Test
    void getTestSimulatorScenario() throws Exception {
        restScenarioParameterMockMvc
            .perform(get(ENTITY_API_URL + "?nameContains=Simulator"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.length()").value(equalTo(4)))
            .andExpect(jsonPath("$.[*]", hasItem(
                allOf(
                    hasEntry("name", SCENARIO_NAME),
                    hasEntry("type", MESSAGE_TRIGGERED.toString())
                )
            )));
    }

    @Test
    void getSingleScenarioWithNameContains() throws Exception {
        restScenarioParameterMockMvc
            .perform(get(ENTITY_API_URL + "?nameContains=" + encode(SCENARIO_NAME, UTF_8)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.length()").value(equalTo(1)))
            .andExpect(jsonPath("$.[0].name", equalTo(SCENARIO_NAME)))
            .andExpect(jsonPath("$.[0].type", equalTo(MESSAGE_TRIGGERED.toString())));
    }

    @Test
    void getMultipleScenariosWithNameContains() throws Exception {
        restScenarioParameterMockMvc
            .perform(get(ENTITY_API_URL + "?nameContains=" + ScenarioLookupServiceImplTest.class.getSimpleName()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.length()").value(equalTo(2)))
            .andExpect(jsonPath("$.[*]", hasItems(
                allOf(
                    hasEntry("name", STARTER_NAME),
                    hasEntry("type", STARTER.toString())
                ),
                allOf(
                    hasEntry("name", SCENARIO_NAME),
                    hasEntry("type", MESSAGE_TRIGGERED.toString())
                )
            )));
    }

    @Test
    void getAllScenarioStarterParameters() throws Exception {
        restScenarioParameterMockMvc
            .perform(get(ENTITY_API_URL_SCENARIO_NAME + "/parameters", STARTER_NAME))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.length()").value(equalTo(1)))
            .andExpect(jsonPath("$.[0].name").value(equalTo("parameter-name")))
            .andExpect(jsonPath("$.[0].value").value(equalTo("parameter-value")));
    }
}
