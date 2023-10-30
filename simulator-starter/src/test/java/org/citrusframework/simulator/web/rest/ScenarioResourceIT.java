package org.citrusframework.simulator.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.citrusframework.simulator.web.rest.TestUtil.sameInstant;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.events.ScenariosReloadedEvent;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.citrusframework.simulator.service.ScenarioLookupService;
import org.citrusframework.simulator.web.rest.ScenarioResource.Scenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
