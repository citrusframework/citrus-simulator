package org.citrusframework.simulator.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Thorsten Schlathoelter
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = SimulatorRestConfigurationProperties.class)
@TestPropertySource(properties = {"citrus.simulator.rest.url-mappings=/a/**,/b/**","citrus.simulator.rest.enabled=true"})
class SimulatorRestConfigurationPropertiesMultiUrlIT {

    @Autowired
    SimulatorRestConfigurationProperties props;

    @Test
    void testMultiUlrConfiguration() {
        assertThat(props.getUrlMappings()).contains("/a/**","/b/**");
        assertThat(props.isEnabled()).isTrue();
    }
}
