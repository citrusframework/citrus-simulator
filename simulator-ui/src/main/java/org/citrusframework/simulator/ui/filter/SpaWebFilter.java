package org.citrusframework.simulator.ui.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class SpaWebFilter extends OncePerRequestFilter {

    private final RequestMatcher simulatorRestRequestMatcher;

    public SpaWebFilter(RequestMatcher simulatorRestRequestMatcher) {
        this.simulatorRestRequestMatcher = simulatorRestRequestMatcher;
    }

    /**
     * Forwards any unmapped paths (except those containing a period) to the client {@code index.html}.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Request URI includes the contextPath: if any, removed it.
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (!path.startsWith("/api") && !path.startsWith("/v3/api-docs") && !path.contains(".") && !simulatorRestRequestMatcher.matches(request) && path.matches("/(.*)")) {
            request.getRequestDispatcher("/index.html").forward(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
