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

package org.citrusframework.simulator.ui.config;

import org.citrusframework.simulator.http.SimulatorRestAdapter;
import org.citrusframework.simulator.http.SimulatorRestConfigurationProperties;
import org.citrusframework.simulator.ws.SimulatorWebServiceAdapter;
import org.citrusframework.simulator.ws.SimulatorWebServiceConfigurationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimulationMappingsTest {

    @Mock
    private SimulatorRestConfigurationProperties simulatorRestConfigurationPropertiesMock;

    @Mock
    private SimulatorRestAdapter simulatorRestAdapterMock;

    @Mock
    private SimulatorWebServiceConfigurationProperties simulatorWebServiceConfigurationPropertiesMock;

    @Mock
    private SimulatorWebServiceAdapter simulatorWebServiceAdapterMock;

    private SimulationMappings fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new SimulationMappings(simulatorRestConfigurationPropertiesMock, simulatorRestAdapterMock, simulatorWebServiceConfigurationPropertiesMock, simulatorWebServiceAdapterMock);
    }

    @Nested
    class GetServletMappingsTest {

        @Test
        void shouldReturnEmptyListWhenNoWebServiceAdapterAndPropertiesAreNull() {
            fixture = new SimulationMappings(null, null, null, null);

            assertThat(fixture.getServletMappings())
                .isEmpty();
        }

        @Test
        void shouldReturnEmptyListWhenWebServicePropertiesIsNull() {
            fixture = new SimulationMappings(null, null, null, simulatorWebServiceAdapterMock);

            assertThat(fixture.getServletMappings())
                .isEmpty();
        }

        @Test
        void shouldReturnServletMappingsFromAdapterWhenBothAdapterAndPropertiesArePresent() {
            List<String> expectedMappings = List.of("/soap", "/ws");
            when(simulatorWebServiceAdapterMock.servletMappings(simulatorWebServiceConfigurationPropertiesMock))
                .thenReturn(expectedMappings);

            assertThat(fixture.getServletMappings())
                .isEqualTo(expectedMappings);
        }

        @Test
        void shouldReturnEmptyListFromAdapterWhenAdapterReturnsNull() {
            when(simulatorWebServiceAdapterMock.servletMappings(simulatorWebServiceConfigurationPropertiesMock))
                .thenReturn(null);

            assertThat(fixture.getServletMappings())
                .isEmpty();
        }

        @Test
        void shouldReturnServletMappingsFromPropertiesWhenAdapterReturnsNullButPropertiesHaveMappings() {
            when(simulatorWebServiceAdapterMock.servletMappings(simulatorWebServiceConfigurationPropertiesMock))
                .thenReturn(null);
            List<String> expectedMappings = List.of("/service");
            when(simulatorWebServiceConfigurationPropertiesMock.getServletMappings())
                .thenReturn(expectedMappings);

            assertThat(fixture.getServletMappings())
                .isEqualTo(expectedMappings);
        }

        @Test
        void shouldReturnServletMappingsFromPropertiesWhenAdapterIsNull() {
            fixture = new SimulationMappings(null, null, simulatorWebServiceConfigurationPropertiesMock, null);
            List<String> expectedMappings = List.of("/webservice");
            when(simulatorWebServiceConfigurationPropertiesMock.getServletMappings())
                .thenReturn(expectedMappings);

            assertThat(fixture.getServletMappings())
                .isEqualTo(expectedMappings);
        }

        @Test
        void shouldReturnEmptyListWhenPropertiesHaveNullServletMappings() {
            fixture = new SimulationMappings(null, null, simulatorWebServiceConfigurationPropertiesMock, null);
            when(simulatorWebServiceConfigurationPropertiesMock.getServletMappings())
                .thenReturn(null);

            assertThat(fixture.getServletMappings())
                .isEmpty();
        }

        @Test
        void shouldReturnMultipleServletMappingsFromAdapter() {
            List<String> expectedMappings = new ArrayList<>();
            expectedMappings.add("/soap/endpoint1");
            expectedMappings.add("/soap/endpoint2");
            expectedMappings.add("/ws/secure");
            when(simulatorWebServiceAdapterMock.servletMappings(simulatorWebServiceConfigurationPropertiesMock))
                .thenReturn(expectedMappings);

            assertThat(fixture.getServletMappings())
                .hasSize(3)
                .isEqualTo(expectedMappings);
        }
    }

    @Nested
    class GetUrlMappingsTest {

        @Test
        void shouldReturnEmptyListWhenNoRestAdapterAndPropertiesAreNull() {
            fixture = new SimulationMappings(null, null, null, null);

            assertThat(fixture.getUrlMappings())
                .isEmpty();
        }

        @Test
        void shouldReturnEmptyListWhenRestPropertiesIsNull() {
            fixture = new SimulationMappings(null, simulatorRestAdapterMock, null, null);

            assertThat(fixture.getUrlMappings())
                .isEmpty();
        }

        @Test
        void shouldReturnUrlMappingsFromAdapterWhenBothAdapterAndPropertiesArePresent() {
            List<String> expectedMappings = List.of("/api/v1", "/rest");
            when(simulatorRestAdapterMock.urlMappings(simulatorRestConfigurationPropertiesMock))
                .thenReturn(expectedMappings);

            assertThat(fixture.getUrlMappings())
                .isEqualTo(expectedMappings);
        }

        @Test
        void shouldReturnUrlMappingsFromPropertiesWhenAdapterIsNull() {
            fixture = new SimulationMappings(simulatorRestConfigurationPropertiesMock, null, null, null);
            List<String> expectedMappings = List.of("/resource");
            when(simulatorRestConfigurationPropertiesMock.getUrlMappings())
                .thenReturn(expectedMappings);

            assertThat(fixture.getUrlMappings())
                .isEqualTo(expectedMappings);
        }

        @Test
        void shouldReturnEmptyListWhenPropertiesHaveNullUrlMappings() {
            fixture = new SimulationMappings(simulatorRestConfigurationPropertiesMock, null, null, null);
            when(simulatorRestConfigurationPropertiesMock.getUrlMappings())
                .thenReturn(null);

            assertThat(fixture.getUrlMappings())
                .isEmpty();
        }

        @Test
        void shouldReturnMultipleUrlMappingsFromAdapter() {
            List<String> expectedMappings = new ArrayList<>();
            expectedMappings.add("/api/users");
            expectedMappings.add("/api/products");
            expectedMappings.add("/api/orders");
            when(simulatorRestAdapterMock.urlMappings(simulatorRestConfigurationPropertiesMock))
                .thenReturn(expectedMappings);

            assertThat(fixture.getUrlMappings())
                .hasSize(3)
                .isEqualTo(expectedMappings)
                .containsExactly("/api/users", "/api/products", "/api/orders");
        }

        @Test
        void shouldReturnUrlMappingsFromAdapterWhenBothAdapterAndPropertiesPresent() {
            List<String> adapterMappings = List.of("/api/v2", "/custom");
            when(simulatorRestAdapterMock.urlMappings(simulatorRestConfigurationPropertiesMock))
                .thenReturn(adapterMappings);
            List<String> propertiesMappings = List.of("/fallback");
            lenient().
                when(simulatorRestConfigurationPropertiesMock.getUrlMappings())
                .thenReturn(propertiesMappings);

            assertThat(fixture.getUrlMappings())
                .isEqualTo(adapterMappings)
                .containsExactly("/api/v2", "/custom");

            verify(simulatorRestConfigurationPropertiesMock, never()).getUrlMappings();
        }
    }

    @Nested
    class CombinedMappingTests {

        @Test
        void shouldReturnBothServletAndUrlMappings() {
            List<String> servletMappings = List.of("/soap");
            List<String> urlMappings = List.of("/api");

            when(simulatorWebServiceAdapterMock.servletMappings(simulatorWebServiceConfigurationPropertiesMock))
                .thenReturn(servletMappings);
            when(simulatorRestAdapterMock.urlMappings(simulatorRestConfigurationPropertiesMock))
                .thenReturn(urlMappings);

            assertThat(fixture.getServletMappings())
                .isEqualTo(servletMappings)
                .containsExactly("/soap");

            assertThat(fixture.getUrlMappings())
                .isEqualTo(urlMappings)
                .containsExactly("/api");
        }

        @Test
        void shouldHandleEmptyListsFromAdapters() {
            List<String> emptyList = new ArrayList<>();
            when(simulatorWebServiceAdapterMock.servletMappings(simulatorWebServiceConfigurationPropertiesMock))
                .thenReturn(emptyList);
            when(simulatorRestAdapterMock.urlMappings(simulatorRestConfigurationPropertiesMock))
                .thenReturn(emptyList);

            assertThat(fixture.getServletMappings())
                .isEmpty();

            assertThat(fixture.getUrlMappings())
                .isEmpty();
        }

        @Test
        void shouldHandleNullsGracefullyAcrossAllComponents() {
            fixture = new SimulationMappings(null, null, null, null);

            assertThat(fixture.getServletMappings())
                .isEmpty();

            assertThat(fixture.getUrlMappings())
                .isEmpty();
        }
    }
}
