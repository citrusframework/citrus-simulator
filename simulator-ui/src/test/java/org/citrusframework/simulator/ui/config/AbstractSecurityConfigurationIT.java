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

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.citrusframework.simulator.ui.filter.SpaWebFilter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

abstract class AbstractSecurityConfigurationIT {

    @Autowired
    SecurityFilterChain filterChain;

    static Stream<Arguments> testRequest() {
        return Stream.of(
            arguments("Rest servlet", "/%context%/rest/my-rest-service", "/%context%/rest/my-rest-service", null, false),
            arguments("Classic servlet", "/%context%/rest/my-rest-service", "/%context%", "/rest/my-rest-service", false),
            arguments("Rest servlet with forward", "/service/rest/my-rest-service", "/service/rest/my-rest-service", null, true),
            arguments("Classic servlet with forward", "/service/rest/my-rest-service", "/service", "/rest/my-rest-service", true));
    }

    @MethodSource
    @ParameterizedTest
    void testRequest(String name, String requestUri, String servletPath, String pathInfo, boolean forward) throws ServletException, IOException {
        SpaWebFilter spaWebFilter = (SpaWebFilter) filterChain.getFilters().stream().filter(oneFilter -> oneFilter instanceof SpaWebFilter).findFirst().orElseThrow();

        RequestDispatcher requestDispatcherMock = mock(RequestDispatcher.class);
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("PUT", requestUri.replace("%context%", getContext())) {
            @Override
            public RequestDispatcher getRequestDispatcher(String path) {
                return requestDispatcherMock;
            }
        };
        mockHttpServletRequest.setServletPath(servletPath.replace("%context%", getContext()));
        mockHttpServletRequest.setPathInfo(pathInfo);
        mockHttpServletRequest.setContextPath("");

        HttpServletResponse responseMock = new MockHttpServletResponse();
        FilterChain filterChainMock = mock(FilterChain.class);

        spaWebFilter.doFilter(mockHttpServletRequest, responseMock, filterChainMock);

        if (forward) {
            verify(requestDispatcherMock, times(1)).forward(any(), any());
            verify(filterChainMock, times(0)).doFilter(any(), any());
        } else {
            verify(requestDispatcherMock, times(0)).forward(any(), any());
            verify(filterChainMock).doFilter(any(), any());
        }
    }

    protected abstract String getContext();
}
