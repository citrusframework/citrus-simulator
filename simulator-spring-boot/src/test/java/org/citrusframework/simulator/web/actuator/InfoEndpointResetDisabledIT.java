package org.citrusframework.simulator.web.actuator;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(
    properties = {"management.server.port=", "citrus.simulator.simulation-results.reset-enabled=false"},
    locations = {"classpath:META-INF/citrus-simulator.properties"}
)
class InfoEndpointResetDisabledIT extends AbstractInfoEndpointIT {

    @Test
    void infoEndpointReturnsConfiguration() throws Exception {
        getSimulatorInfoAndAssertResetResultsEnabledHasValue("false");
    }
}
