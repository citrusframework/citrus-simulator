/*
 * Copyright the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
