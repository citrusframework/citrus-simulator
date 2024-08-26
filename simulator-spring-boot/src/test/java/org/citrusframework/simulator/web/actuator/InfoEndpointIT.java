package org.citrusframework.simulator.web.actuator;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(
    properties = {"management.server.port="},
    locations = {"classpath:META-INF/citrus-simulator.properties"}
)
class InfoEndpointIT extends AbstractInfoEndpointIT {

    @Test
    void testResultsResetIsEnabledByDefault() throws Exception {
        getSimulatorInfoAndAssertResetResultsEnabledHasValue("true");
    }

    @Nested
    @TestPropertySource(properties = {
        "citrus.simulator.simulation-results.reset-enabled=true"
    })
    class WithResetBeingExplicitlyEnabled {

        @Test
        void infoEndpointReturnsConfiguration() throws Exception {
            getSimulatorInfoAndAssertResetResultsEnabledHasValue("true");
        }
    }
}
