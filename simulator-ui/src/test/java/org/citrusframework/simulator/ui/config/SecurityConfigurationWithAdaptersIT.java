/*
 * Copyright 2023 the original author or authors.
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

package org.citrusframework.simulator.ui.config;

import org.citrusframework.simulator.http.SimulatorRestAdapter;
import org.citrusframework.simulator.http.SimulatorRestConfigurationProperties;
import org.citrusframework.simulator.ui.IntegrationTest;
import org.citrusframework.simulator.ui.config.SecurityConfigurationWithAdaptersIT.AdapterConfiguration;
import org.citrusframework.simulator.ui.test.TestApplication;
import org.citrusframework.simulator.ws.SimulatorWebServiceAdapter;
import org.citrusframework.simulator.ws.SimulatorWebServiceConfigurationProperties;
import org.junit.jupiter.api.parallel.Isolated;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * @author Thorsten Schlathoelter
 */
@Isolated
@DirtiesContext
@IntegrationTest
@SpringBootTest(classes = {TestApplication.class, AdapterConfiguration.class}, properties = {"with-adapters=true"})
class SecurityConfigurationWithAdaptersIT extends AbstractSecurityConfigurationIT {

    @Override
    protected String getContext() {
        return "modified-simulator";
    }

    @Configuration
    protected static class AdapterConfiguration {

        @Bean
        @ConditionalOnProperty(name = "with-adapters", havingValue = "true")
        public SimulatorRestAdapter simulatorRestAdapter() {
            return new SimulatorRestAdapter() {
                @Override
                public List<String> urlMappings(SimulatorRestConfigurationProperties simulatorRestConfiguration) {
                    return List.of("/modified-simulator/rest/**");
                }
            };
        }

        @Bean
        @ConditionalOnProperty(name = "with-adapters", havingValue = "true")
        public SimulatorWebServiceAdapter simulatorWebServiceAdapter() {
            return new SimulatorWebServiceAdapter() {
                public String servletMapping(SimulatorWebServiceConfigurationProperties simulatorWebServiceConfiguration) {
                    return "/modified-simulator/ws/*";
                }
            };
        }
    }
}
