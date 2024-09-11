package org.citrusframework.simulator.web.actuator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.citrusframework.simulator.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@DirtiesContext
@IntegrationTest
@AutoConfigureMockMvc
public abstract class AbstractInfoEndpointIT {

    @Autowired
    private MockMvc mockMvc;

    protected void getSimulatorInfoAndAssertResetResultsEnabledHasValue(String resetResultsEnabled)
        throws Exception {
        mockMvc.perform(get("/api/manage/info"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/vnd.spring-boot.actuator.v3+json"))
            .andExpect(jsonPath("$.config['reset-results-enabled']").value(resetResultsEnabled));
    }
}
