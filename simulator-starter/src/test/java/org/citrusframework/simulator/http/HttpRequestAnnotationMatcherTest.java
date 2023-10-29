package org.citrusframework.simulator.http;

import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class HttpRequestAnnotationMatcherTest {

    private static final RequestMapping REQ_MAP_WITH_PATH_NAME = getRequestMapping(new ScenarioWithPathName());
    private static final RequestMapping REQ_MAP_WITH_PATH_VALUE = getRequestMapping(new ScenarioWithPathValue());
    private static final RequestMapping REQ_MAP_WITH_PATH_PLACEHOLDER = getRequestMapping(new ScenarioWithPathContainingPlaceholder());
    private static final RequestMapping REQ_MAP_WITH_PATH_PATTERN = getRequestMapping(new ScenarioWithPathPattern());
    private static final RequestMapping REQ_MAP_WITH_PUT_METHOD = getRequestMapping(new ScenarioWithPutMethod());
    private static final RequestMapping REQ_MAP_WITH_QUERY_PARAMS = getRequestMapping(new ScenarioWithQueryParams());
    private static final RequestMapping REQ_MAP_WITH_ALL_SUPPORTED_RESTRICTIONS = getRequestMapping(new ScenarioWithAllSupportedRestrictions());

    HttpRequestAnnotationMatcher fixture;

    static Stream<Arguments> checkRequestPathSupported() {
        return Stream.of(
            Arguments.of(
                REQ_MAP_WITH_PATH_NAME,
                setupHttpMessage("/path/name", RequestMethod.GET, Collections.emptyMap()),
                true,
                true
            ),
            Arguments.of(
                REQ_MAP_WITH_PATH_NAME,
                setupHttpMessage("/path/name", RequestMethod.GET, Collections.emptyMap()),
                false,
                true
            ),
            Arguments.of(
                REQ_MAP_WITH_PATH_NAME,
                setupHttpMessage("/path/wrong-path", RequestMethod.GET, Collections.emptyMap()),
                true,
                false
            ),
            Arguments.of(
                REQ_MAP_WITH_PATH_NAME,
                setupHttpMessage("", RequestMethod.GET, Collections.emptyMap()),
                true,
                false
            ),
            Arguments.of(
                REQ_MAP_WITH_PATH_VALUE,
                setupHttpMessage("/path/value", RequestMethod.GET, Collections.emptyMap()),
                true,
                true
            ),
            Arguments.of(
                REQ_MAP_WITH_PATH_VALUE,
                setupHttpMessage("/path/wrong-path", RequestMethod.GET, Collections.emptyMap()),
                true,
                false
            ),
            Arguments.of(
                REQ_MAP_WITH_PATH_VALUE,
                setupHttpMessage("", RequestMethod.GET, Collections.emptyMap()),
                true,
                false
            ),
            Arguments.of(
                REQ_MAP_WITH_PATH_PLACEHOLDER,
                setupHttpMessage("/path/place-holder/123", RequestMethod.GET, Collections.emptyMap()),
                false,
                true
            ),
            Arguments.of(
                REQ_MAP_WITH_PATH_PLACEHOLDER,
                setupHttpMessage("/path/place-holder/123", RequestMethod.GET, Collections.emptyMap()),
                true,
                false
            ),
            Arguments.of(
                REQ_MAP_WITH_PATH_PLACEHOLDER,
                setupHttpMessage("/path/wrong-path", RequestMethod.GET, Collections.emptyMap()),
                true,
                false
            ),
            Arguments.of(
                REQ_MAP_WITH_PATH_PLACEHOLDER,
                setupHttpMessage("/path/wrong-path", RequestMethod.GET, Collections.emptyMap()),
                false,
                false
            ),
            Arguments.of(
                REQ_MAP_WITH_PATH_PATTERN,
                setupHttpMessage("/path/pattern/match-me", RequestMethod.GET, Collections.emptyMap()),
                true,
                false
            ),
            Arguments.of(
                REQ_MAP_WITH_PATH_PATTERN,
                setupHttpMessage("/path/pattern/match-me", RequestMethod.GET, Collections.emptyMap()),
                false,
                true
            ),
            Arguments.of(
                REQ_MAP_WITH_PATH_PATTERN,
                setupHttpMessage("/path/wrong-pattern", RequestMethod.GET, Collections.emptyMap()),
                true,
                false
            ),
            Arguments.of(
                REQ_MAP_WITH_PATH_PATTERN,
                setupHttpMessage("/path/wrong-pattern", RequestMethod.GET, Collections.emptyMap()),
                false,
                false
            ),
            Arguments.of(
                REQ_MAP_WITH_PATH_PATTERN,
                setupHttpMessage("", RequestMethod.GET, Collections.emptyMap()),
                true,
                false
            ),
            Arguments.of(
                REQ_MAP_WITH_PATH_PATTERN,
                setupHttpMessage("", RequestMethod.GET, Collections.emptyMap()),
                false,
                false
            ),
            Arguments.of(
                REQ_MAP_WITH_PUT_METHOD,
                setupHttpMessage("/any-path", RequestMethod.PUT, Collections.emptyMap()),
                true,
                true
            ),
            Arguments.of(
                REQ_MAP_WITH_PUT_METHOD,
                setupHttpMessage("", RequestMethod.GET, Collections.emptyMap()),
                true,
                false
            ),
            Arguments.of(
                REQ_MAP_WITH_QUERY_PARAMS,
                setupHttpMessage("/any-path", RequestMethod.GET, Collections.singletonMap("a", Collections.singleton("1"))),
                true,
                true
            ),
            Arguments.of(
                REQ_MAP_WITH_QUERY_PARAMS,
                setupHttpMessage("/any-path", RequestMethod.GET, Collections.singletonMap("a", Collections.emptySet())),
                true,
                true
            ),
            Arguments.of(
                REQ_MAP_WITH_QUERY_PARAMS,
                setupHttpMessage("/any-path", RequestMethod.GET, Stream.of("a=1", "b=2").map(item -> item.split("=")).collect(Collectors.toMap(keyValuePair -> keyValuePair[0], keyValuePair -> Collections.singleton(keyValuePair[1])))),
                true,
                false
            ),
            Arguments.of(
                REQ_MAP_WITH_QUERY_PARAMS,
                setupHttpMessage("/any-path", RequestMethod.GET, Collections.singletonMap("c", Collections.singleton("3"))),
                true,
                false
            ),
            Arguments.of(
                REQ_MAP_WITH_QUERY_PARAMS,
                setupHttpMessage("/any-path", RequestMethod.GET, Collections.emptyMap()),
                true,
                false
            ),

            Arguments.of(
                REQ_MAP_WITH_ALL_SUPPORTED_RESTRICTIONS,
                setupHttpMessage("/path/value", RequestMethod.GET, Collections.singletonMap("a", Collections.singleton("1"))),
                true,
                true
            ),
            Arguments.of(
                REQ_MAP_WITH_ALL_SUPPORTED_RESTRICTIONS,
                setupHttpMessage("/wrong-path", RequestMethod.GET, Collections.singletonMap("a", Collections.singleton("1"))),
                true,
                false
            ),
            Arguments.of(
                REQ_MAP_WITH_ALL_SUPPORTED_RESTRICTIONS,
                setupHttpMessage("/path/value", RequestMethod.PUT, Collections.singletonMap("a", Collections.singleton("1"))),
                true,
                false
            ),
            Arguments.of(
                REQ_MAP_WITH_ALL_SUPPORTED_RESTRICTIONS,
                setupHttpMessage("/path/value", RequestMethod.GET, Collections.emptyMap()),
                true,
                false
            )
        );
    }

    private static RequestMapping getRequestMapping(SimulatorScenario scenario) {
        return AnnotationUtils.findAnnotation(scenario.getClass(), RequestMapping.class);
    }

    private static HttpMessage setupHttpMessage(String path, RequestMethod method, Map<String, Collection<String>> queryParams) {
        final HttpMessage httpMessage = Mockito.mock(HttpMessage.class);
        when(httpMessage.getPath()).thenReturn(path);
        when(httpMessage.getRequestMethod()).thenReturn(method);
        when(httpMessage.getQueryParams()).thenReturn(queryParams);
        return httpMessage;
    }

    @BeforeEach
    void beforeEachSetup() {
        fixture = HttpRequestAnnotationMatcher.instance();
    }

    @MethodSource
    @ParameterizedTest
    void checkRequestPathSupported(RequestMapping requestMapping, HttpMessage httpMessage, boolean exactMatch, boolean expectedResult) {
        boolean actual = fixture.checkRequestPathSupported(httpMessage, requestMapping, exactMatch)
            && fixture.checkRequestMethodSupported(httpMessage, requestMapping)
            && fixture.checkRequestQueryParamsSupported(httpMessage, requestMapping);
        assertEquals(actual, expectedResult);
    }

    @Scenario("ScenarioWithPathName")
    @RequestMapping("/path/name")
    private static class ScenarioWithPathName extends AbstractSimulatorScenario {
    }

    @Scenario("ScenarioWithPathValue")
    @RequestMapping(value = "/path/value")
    private static class ScenarioWithPathValue extends AbstractSimulatorScenario {
    }

    @Scenario("ScenarioWithPathContainingPlaceholder")
    @RequestMapping(value = "/path/place-holder/{value}")
    private static class ScenarioWithPathContainingPlaceholder extends AbstractSimulatorScenario {
    }

    @Scenario("ScenarioWithPathPattern")
    @RequestMapping(value = "/path/pattern/*")
    private static class ScenarioWithPathPattern extends AbstractSimulatorScenario {
    }

    @Scenario("ScenarioWithPutMethod")
    @RequestMapping(method = {RequestMethod.PUT})
    private static class ScenarioWithPutMethod extends AbstractSimulatorScenario {
    }

    @Scenario("ScenarioWithQueryParams")
    @RequestMapping(params = {"a", "!b"})
    private static class ScenarioWithQueryParams extends AbstractSimulatorScenario {
    }

    @Scenario("ScenarioWithAllSupportedRestrictions")
    @RequestMapping(method = RequestMethod.GET, value = "/path/value", params = {"a", "!b"})
    private static class ScenarioWithAllSupportedRestrictions extends AbstractSimulatorScenario {
    }
}
