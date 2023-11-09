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

package org.citrusframework.simulator.ui.filter;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class SpaWebFilter extends OncePerRequestFilter {

    private final String actuatorPath;
    private final String h2ConsolePath;
    private final RequestMatcher simulationEndpointsRequestMatcher;

    public SpaWebFilter(String actuatorPath, String h2ConsolePath, RequestMatcher simulationEndpointsRequestMatcher) {
        this.actuatorPath = actuatorPath;
        this.h2ConsolePath = h2ConsolePath;
        this.simulationEndpointsRequestMatcher = simulationEndpointsRequestMatcher;
    }

    /**
     * Forwards any unmapped paths (except those containing a period) to the client {@code index.html}.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        // Request URI includes the contextPath: if any, remove it.
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (!path.startsWith("/api")
            && !path.startsWith("/v3/api-docs")
            && !path.startsWith(actuatorPath)
            && !path.startsWith(h2ConsolePath)
            && !path.contains(".")
            && !simulationEndpointsRequestMatcher.matches(request)
            && path.matches("/(.*)")) {
            request.getRequestDispatcher("/index.html").forward(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
