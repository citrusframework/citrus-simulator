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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import org.citrusframework.endpoint.adapter.EmptyResponseEndpointAdapter;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.scenario.mapper.ContentBasedXPathScenarioMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SimulatorWebServiceAdapterTest {

    @Mock
    private SimulatorWebServiceConfigurationProperties simulatorWebServiceConfigurationProperties;

    @Mock
    private SimulatorConfigurationProperties simulatorConfigurationProperties;

    private SimulatorWebServiceAdapter fixture;

    @BeforeEach
    void setUp() {
        fixture = new SimulatorWebServiceAdapter() {
        };
    }

    @Test
    void testServletMappings() {
        doReturn(asList("/path1", "/path2")).when(simulatorWebServiceConfigurationProperties).getServletMappings();

        assertThat(fixture.servletMappings(simulatorWebServiceConfigurationProperties))
            .hasSize(2)
            .containsExactly("/path1", "/path2");
    }

    @Test
    void testScenarioMapper() {
        assertThat(fixture.scenarioMapper())
            .isNotNull()
            .isInstanceOf(ContentBasedXPathScenarioMapper.class);
    }

    @Test
    void testFallbackEndpointAdapter() {
        assertThat(fixture.fallbackEndpointAdapter())
            .isNotNull()
            .isInstanceOf(EmptyResponseEndpointAdapter.class);
    }

    @Test
    void testExceptionDelay() {
        doReturn(1000L).when(simulatorConfigurationProperties).getExceptionDelay();

        assertThat(fixture.exceptionDelay(simulatorConfigurationProperties))
            .isNotNull()
            .isEqualTo(1000L);
    }
}
