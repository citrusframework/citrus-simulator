package org.citrusframework.simulator.ui.config;

import org.citrusframework.simulator.ui.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@IntegrationTest
public class SimulatorUiAutoconfigurationIT {

    @Autowired
    private SimulatorUiAutoconfiguration simulatorUiAutoconfiguration;

    @Test
    void isEnabledByDefault() {
        assertNotNull(simulatorUiAutoconfiguration, "Simulator UI autoconfiguration is enabled by default, whenever simulator-ui is on the classpath.");
    }
}
