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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.citrusframework.simulator.ui.filter.SpaWebFilter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.SecurityFilterChain;

abstract class AbstractSecurityConfigurationIT {

    @Autowired
    SecurityFilterChain filterChain;

    @ParameterizedTest
    @MethodSource
    void testRequest(String name, String requestUri, String servletPath, String pathInfo, boolean forward) throws ServletException, IOException {
        SpaWebFilter spaWebFilter = (SpaWebFilter) filterChain.getFilters().stream().filter(oneFilter -> oneFilter instanceof SpaWebFilter).findFirst().orElseThrow();

        RequestDispatcher requestDispatcherMock = mock(RequestDispatcher.class);
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        doReturn(requestDispatcherMock).when(requestMock).getRequestDispatcher(any());

        doReturn("PUT").when(requestMock).getMethod();
        doReturn(requestUri.replace("%context%", getContext())).when(requestMock).getRequestURI();
        doReturn(servletPath.replace("%context%", getContext())).when(requestMock).getServletPath();
        doReturn(pathInfo).when(requestMock).getPathInfo();
        doReturn("").when(requestMock).getContextPath();
        doReturn(Collections.enumeration(List.of())).when(requestMock).getAttributeNames();

        HttpServletResponse responseMock = mock(HttpServletResponse.class);
        FilterChain filterChainMock  = mock(FilterChain.class);

        spaWebFilter.doFilter(requestMock, responseMock , filterChainMock);

        if (forward) {
            verify(requestDispatcherMock, times(1)).forward(any(), any());
            verify(filterChainMock, times(0)).doFilter(any(), any());
        } else {
            verify(requestDispatcherMock, times(0)).forward(any(), any());
            verify(filterChainMock).doFilter(any(), any());
        }

    }

    protected abstract String getContext();

    static Stream<Arguments> testRequest() {
        return Stream.of(
            Arguments.of("Rest servlet", "/%context%/rest/my-rest-service", "/%context%/rest/my-rest-service", null, false),
            Arguments.of("Classic servlet", "/%context%/rest/my-rest-service", "/%context%", "/rest/my-rest-service", false),
            Arguments.of("Rest servlet with forward", "/service/rest/my-rest-service", "/service/rest/my-rest-service", null, true),
            Arguments.of("Classic servlet with forward", "/service/rest/my-rest-service", "/service", "/rest/my-rest-service", true));
    }

}
