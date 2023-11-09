package org.citrusframework.simulator.ui.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.stream.Stream;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpaWebFilterTest {

    private static final String ACTUATOR_PATH = "/api/manage";
    private static final String H2_CONSOLE_PATH = "/h2-console";

    @Mock
    private RequestMatcher simulatorRestRequestMatcherMock;

    @Mock
    private HttpServletRequest requestMock;

    @Mock
    private HttpServletResponse responseMock;

    @Mock
    private FilterChain filterChainMock;

    @Mock
    private RequestDispatcher requestDispatcherMock;

    private SpaWebFilter fixture;

    public static Stream<Arguments> shouldNotForwardPathToIndexHtml() {
        return Stream.of(
            Arguments.of("/api", ""),
            Arguments.of("/api/somepath", ""),
            Arguments.of(ACTUATOR_PATH, ""),
            Arguments.of(H2_CONSOLE_PATH, ""),
            Arguments.of("/v3/api-docs", ""),
            Arguments.of("/v3/api-docs/somepath", ""),
            Arguments.of("/some/absolute/path.", ""),
            Arguments.of("path/without/leading/slash", ""),
            Arguments.of("/server-1/api", "/server-1"),
            Arguments.of("/server-1/api/somepath", "/server-1"),
            Arguments.of("/server-1/v3/api-docs", "/server-1"),
            Arguments.of("/server-1/v3/api-docs/somepath", "/server-1"),
            Arguments.of("/server-1/some/absolute/path.", "/server-1"),
            Arguments.of("/server-1path/without/leading/slash", "/server-1")
        );
    }

    @BeforeEach
    void beforeEachSetup() {
        fixture = new SpaWebFilter(ACTUATOR_PATH, H2_CONSOLE_PATH, simulatorRestRequestMatcherMock);
    }

    @MethodSource
    @ParameterizedTest
    void shouldNotForwardPathToIndexHtml(String requestUri, String contextPath) throws ServletException, IOException {
        when(requestMock.getRequestURI()).thenReturn(requestUri);
        when(requestMock.getContextPath()).thenReturn(contextPath);

        fixture.doFilterInternal(requestMock, responseMock, filterChainMock);

        verify(filterChainMock).doFilter(requestMock, responseMock);
        verify(requestDispatcherMock, never()).forward(requestMock, responseMock);
    }

    @Test
    void shouldNotForwardRequestMatchingPathToIndexHtml() throws ServletException, IOException {
        String requestUri = "/request-path";

        when(requestMock.getRequestURI()).thenReturn(requestUri);
        when(requestMock.getContextPath()).thenReturn("");

        doReturn(true).when(simulatorRestRequestMatcherMock).matches(requestMock);

        fixture.doFilterInternal(requestMock, responseMock, filterChainMock);

        verify(filterChainMock).doFilter(requestMock, responseMock);
        verify(requestDispatcherMock, never()).forward(requestMock, responseMock);
    }

    @Test
    void shouldForwardInvalidPathToIndexHtml() throws ServletException, IOException {
        when(requestMock.getRequestDispatcher("/index.html")).thenReturn(requestDispatcherMock);

        when(requestMock.getRequestURI()).thenReturn("/somepath");
        when(requestMock.getContextPath()).thenReturn("");
        when(simulatorRestRequestMatcherMock.matches(requestMock)).thenReturn(false);

        fixture.doFilterInternal(requestMock, responseMock, filterChainMock);

        verify(requestDispatcherMock).forward(requestMock, responseMock);
        verify(filterChainMock, never()).doFilter(requestMock, responseMock);
    }
}
