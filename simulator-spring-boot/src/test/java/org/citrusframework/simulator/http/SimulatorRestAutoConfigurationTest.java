package org.citrusframework.simulator.http;

import org.citrusframework.report.MessageListeners;
import org.citrusframework.simulator.listener.SimulatorMessageListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

/**
 * @author Thorsten Schlathoelter
 */
@ExtendWith({ MockitoExtension.class })
class SimulatorRestAutoConfigurationTest {

    @Mock
    private SimulatorRestConfigurationProperties simulatorRestConfigurationProperties;

    @Mock
    private SimulatorRestAdapter simulatorRestAdapter;

    @Mock
    private MessageListeners messageListeners;

    @Mock
    private SimulatorMessageListener simulatorMessageListener;

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private SimulatorRestAutoConfiguration simulatorRestAutoConfiguration;

    @Test
    void shouldHandleSingleUrlMappings() {
        doReturn(List.of("/services/rest/**"))
            .when(simulatorRestAdapter)
            .urlMappings(simulatorRestConfigurationProperties);

        doReturn(new HandlerInterceptor[] {}).when(simulatorRestAdapter).interceptors();

        assertEquals(List.of("/services/rest/**"),
            ReflectionTestUtils.invokeMethod(simulatorRestAutoConfiguration, "getUrlMappings"));
        assertEquals("[/services/rest/*]", simulatorRestAutoConfiguration.requestCachingFilter().getUrlPatterns().toString());

        Map<String, ?> simulatorRestHandlerMapping =
            ((SimpleUrlHandlerMapping) simulatorRestAutoConfiguration.simulatorRestHandlerMapping(applicationContext, messageListeners, simulatorMessageListener)).getUrlMap();
        assertThat(simulatorRestHandlerMapping).containsOnlyKeys("/services/rest/**");
    }

    @Test
    void shouldHandleMultipleUrlMappings() {
        doReturn(List.of("/services/rest1/**", "/services/rest2/**")).when(simulatorRestAdapter)
            .urlMappings(simulatorRestConfigurationProperties);

        doReturn(new HandlerInterceptor[] {}).when(simulatorRestAdapter).interceptors();
        assertEquals(List.of("/services/rest1/**", "/services/rest2/**"),
            ReflectionTestUtils.invokeMethod(simulatorRestAutoConfiguration, "getUrlMappings"));
        assertEquals(
            Set.of("/services/rest1/*", "/services/rest2/*"), simulatorRestAutoConfiguration.requestCachingFilter().getUrlPatterns());

        Map<String, ?> simulatorRestHandlerMapping =
            ((SimpleUrlHandlerMapping) simulatorRestAutoConfiguration.simulatorRestHandlerMapping(applicationContext, messageListeners, simulatorMessageListener)).getUrlMap();
        assertThat(simulatorRestHandlerMapping).containsOnlyKeys("/services/rest1/**", "/services/rest2/**");
    }
}
