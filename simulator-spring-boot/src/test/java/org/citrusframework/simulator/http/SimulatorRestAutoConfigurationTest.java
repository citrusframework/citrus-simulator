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

package org.citrusframework.simulator.http;

import org.citrusframework.report.MessageListeners;
import org.citrusframework.simulator.endpoint.SimulatorEndpointAdapter;
import org.citrusframework.simulator.listener.SimulatorMessageListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.util.ReflectionTestUtils.invokeMethod;

/**
 * @author Thorsten Schlathoelter
 */
@ExtendWith({ MockitoExtension.class })
class SimulatorRestAutoConfigurationTest {

    @Mock
    private MessageListeners messageListeners;

    @Mock
    private SimulatorEndpointAdapter simulatorRestEndpointAdapter;

    @Mock
    private SimulatorMessageListener simulatorMessageListener;

    @Mock
    private SimulatorRestAdapter simulatorRestAdapter;

    @Mock
    private SimulatorRestConfigurationProperties simulatorRestConfigurationProperties;

    @InjectMocks
    private SimulatorRestAutoConfiguration simulatorRestAutoConfiguration;

    @Test
    void shouldHandleSingleUrlMappings() {
        doReturn(List.of("/services/rest/**"))
            .when(simulatorRestAdapter)
            .urlMappings(simulatorRestConfigurationProperties);

        doReturn(new HandlerInterceptor[] {}).when(simulatorRestAdapter).interceptors();

        assertEquals(List.of("/services/rest/**"), invokeMethod(simulatorRestAutoConfiguration, "getUrlMappings"));
        assertEquals("[/services/rest/*]", simulatorRestAutoConfiguration.requestCachingFilter().getUrlPatterns().toString());

        Map<String, ?> simulatorRestHandlerMapping = ((SimpleUrlHandlerMapping) simulatorRestAutoConfiguration.simulatorRestHandlerMapping(messageListeners, simulatorRestEndpointAdapter, simulatorMessageListener)).getUrlMap();
        assertThat(simulatorRestHandlerMapping).containsOnlyKeys("/services/rest/**");
    }

    @Test
    void shouldHandleMultipleUrlMappings() {
        doReturn(List.of("/services/rest1/**", "/services/rest2/**"))
            .when(simulatorRestAdapter)
            .urlMappings(simulatorRestConfigurationProperties);

        doReturn(new HandlerInterceptor[] {}).when(simulatorRestAdapter).interceptors();
        assertEquals(List.of("/services/rest1/**", "/services/rest2/**"), invokeMethod(simulatorRestAutoConfiguration, "getUrlMappings"));
        assertEquals(Set.of("/services/rest1/*", "/services/rest2/*"), simulatorRestAutoConfiguration.requestCachingFilter().getUrlPatterns());

        Map<String, ?> simulatorRestHandlerMapping = ((SimpleUrlHandlerMapping) simulatorRestAutoConfiguration.simulatorRestHandlerMapping(messageListeners, simulatorRestEndpointAdapter, simulatorMessageListener)).getUrlMap();
        assertThat(simulatorRestHandlerMapping).containsOnlyKeys("/services/rest1/**", "/services/rest2/**");
    }
}
