package org.citrusframework.simulator.ui.config;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServletUtilsTest {

    static Stream<Arguments> extractContextPath() {
        return Stream.of(
            Arguments.of("/a/b", "/a/b"),
            Arguments.of("/a/b/*", "/a/b"),
            Arguments.of("/a/b/**", "/a/b"),
            Arguments.of("/a/b/**/*", "/a/b")
        );
    }

    @MethodSource
    @ParameterizedTest
    void extractContextPath(String urlMapping, String contextPath) {
        assertEquals(contextPath, ServletUtils.extractContextPath(urlMapping));
    }
}
