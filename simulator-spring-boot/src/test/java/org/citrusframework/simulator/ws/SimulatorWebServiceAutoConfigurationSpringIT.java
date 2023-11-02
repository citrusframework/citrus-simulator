package org.citrusframework.simulator.ws;

import org.citrusframework.simulator.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.ws.config.annotation.WsConfigurationSupport;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Isolated
@DirtiesContext
@IntegrationTest
@TestPropertySource(properties = {"citrus.simulator.ws.enabled=true", "spring.webservices.autoconfiguration.enabled=true"})
class SimulatorWebServiceAutoConfigurationSpringIT {

    @Autowired
    private WsConfigurationSupport wsConfigurationSupport;

    @Autowired
    @Qualifier("messageDispatcherServlet")
    private ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet;

    @Test
    void defaultSpringWsConfigurationIsEnabled() {
        assertNotNull(wsConfigurationSupport);
        assertNotNull(messageDispatcherServlet);
    }
}
