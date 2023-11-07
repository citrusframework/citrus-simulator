package org.citrusframework.simulator.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Thorsten Schlathoelter
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = SimulatorRestConfigurationProperties.class)
@TestPropertySource(properties = {"citrus.simulator.rest.url-mappings=/a/**","citrus.simulator.rest.enabled=false"})
class SimulatorRestConfigurationPropertiesSingleUrlIT {

    @Autowired
    SimulatorRestConfigurationProperties props;

    @Test
    void testMultiUlrConfiguration() {
        assertThat(props.getUrlMappings()).contains("/a/**");
        assertThat(props.isEnabled()).isFalse();
    }
}
