package org.citrusframework.simulator.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimulatorConfigurationPropertiesTest {

    private SimulatorConfigurationProperties fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new SimulatorConfigurationProperties();
    }

    @Nested
    class SimulationResults {

        @Test
        void isNotNull() {
            assertThat(fixture.getSimulationResults())
                .isNotNull();
        }

        @Test
        void resetIsEnabledByDefault() {
            assertThat(fixture.getSimulationResults().isResetEnabled())
                .isTrue();
        }
    }
}
