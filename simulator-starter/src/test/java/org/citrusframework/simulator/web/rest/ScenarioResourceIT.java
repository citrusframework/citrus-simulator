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
            .perform(get(ENTITY_API_URL ))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.length()").value(equalTo(2)))
            .andExpect(jsonPath("$.[0].name").value(equalTo("testScenarioStarter")))
            .andExpect(jsonPath("$.[0].type").value(equalTo("STARTER")))
            .andExpect(jsonPath("$.[1].name").value(equalTo("testSimulatorScenario")))
            .andExpect(jsonPath("$.[1].type").value(equalTo("MESSAGE_TRIGGERED")));
    }

    @Test
    void getAllScenarioStarterParameters() throws Exception {
        // Get all the scenarioParameterList
        restScenarioParameterMockMvc
            .perform(get(ENTITY_API_URL_SCENARIO_NAME+"/parameters", "testScenarioStarter" ))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.length()").value(equalTo(1)))
            .andExpect(jsonPath("$.[0].name").value(equalTo("parameter-name")))
            .andExpect(jsonPath("$.[0].value").value(equalTo("parameter-value")));
    }
}
