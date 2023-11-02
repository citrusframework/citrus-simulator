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

import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Isolated
@DirtiesContext
@IntegrationTest
@TestPropertySource(properties = {"citrus.simulator.ws.enabled=true"})
class SimulatorWebServiceAutoConfigurationIT {

    @Autowired
    private WsConfigurationSupport wsConfigurationSupport;

    @Autowired
    @Qualifier("simulatorServletRegistrationBean")
    private ServletRegistrationBean<MessageDispatcherServlet> simulatorServletRegistrationBean;

    @Test
    void defaultSpringWsConfigurationIsDisabledByDefault() {
        assertNotNull(wsConfigurationSupport);
        assertNotNull(simulatorServletRegistrationBean);
        assertEquals(new LinkedHashSet<>(List.of("/services/ws/*")), simulatorServletRegistrationBean.getUrlMappings());
    }
}
