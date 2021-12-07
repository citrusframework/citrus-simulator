package org.citrusframework.simulator.http;

import com.consol.citrus.http.message.HttpMessage;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.mockito.Mockito;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

public class HttpRequestAnnotationMatcherTest {

    HttpRequestAnnotationMatcher cut = HttpRequestAnnotationMatcher.instance();

    private static final RequestMapping REQ_MAP_WITH_PATH_NAME = getRequestMapping(new ScenarioWithPathName());
    private static final RequestMapping REQ_MAP_WITH_PATH_VALUE = getRequestMapping(new ScenarioWithPathValue());
    private static final RequestMapping REQ_MAP_WITH_PATH_PLACEHOLDER = getRequestMapping(new ScenarioWithPathContainingPlaceholder());
    private static final RequestMapping REQ_MAP_WITH_PATH_PATTERN = getRequestMapping(new ScenarioWithPathPattern());
    private static final RequestMapping REQ_MAP_WITH_PUT_METHOD = getRequestMapping(new ScenarioWithPutMethod());
    private static final RequestMapping REQ_MAP_WITH_QUERY_PARAMS = getRequestMapping(new ScenarioWithQueryParams());
    private static final RequestMapping REQ_MAP_WITH_ALL_SUPPORTED_RESTRICTIONS = getRequestMapping(new ScenarioWithAllSupportedRestrictions());

    @DataProvider
    public Object[][] dpScenarios() {
        return new Object[][]{
                new Object[]{
                        REQ_MAP_WITH_PATH_NAME,
                        setupHttpMessage("/path/name", HttpMethod.GET, Collections.emptyMap()),
                        true,
                        true
                },
                new Object[]{
                        REQ_MAP_WITH_PATH_NAME,
                        setupHttpMessage("/path/name", HttpMethod.GET, Collections.emptyMap()),
                        false,
                        true
                },
                new Object[]{
                        REQ_MAP_WITH_PATH_NAME,
                        setupHttpMessage("/path/wrong-path", HttpMethod.GET, Collections.emptyMap()),
                        true,
                        false
                },
                new Object[]{
                        REQ_MAP_WITH_PATH_NAME,
                        setupHttpMessage("", HttpMethod.GET, Collections.emptyMap()),
                        true,
                        false
                },

                new Object[]{
                        REQ_MAP_WITH_PATH_VALUE,
                        setupHttpMessage("/path/value", HttpMethod.GET, Collections.emptyMap()),
                        true,
                        true
                },
                new Object[]{
                        REQ_MAP_WITH_PATH_VALUE,
                        setupHttpMessage("/path/wrong-path", HttpMethod.GET, Collections.emptyMap()),
                        true,
                        false
                },
                new Object[]{
                        REQ_MAP_WITH_PATH_VALUE,
                        setupHttpMessage("", HttpMethod.GET, Collections.emptyMap()),
                        true,
                        false
                },

                new Object[]{
                        REQ_MAP_WITH_PATH_PLACEHOLDER,
                        setupHttpMessage("/path/place-holder/123", HttpMethod.GET, Collections.emptyMap()),
                        false,
                        true
                },
                new Object[]{
                        REQ_MAP_WITH_PATH_PLACEHOLDER,
                        setupHttpMessage("/path/place-holder/123", HttpMethod.GET, Collections.emptyMap()),
                        true,
                        false
                },
                new Object[]{
                        REQ_MAP_WITH_PATH_PLACEHOLDER,
                        setupHttpMessage("/path/wrong-path", HttpMethod.GET, Collections.emptyMap()),
                        true,
                        false
                },
                new Object[]{
                        REQ_MAP_WITH_PATH_PLACEHOLDER,
                        setupHttpMessage("/path/wrong-path", HttpMethod.GET, Collections.emptyMap()),
                        false,
                        false
                },

                new Object[]{
                        REQ_MAP_WITH_PATH_PATTERN,
                        setupHttpMessage("/path/pattern/match-me", HttpMethod.GET, Collections.emptyMap()),
                        true,
                        false
                },
                new Object[]{
                        REQ_MAP_WITH_PATH_PATTERN,
                        setupHttpMessage("/path/pattern/match-me", HttpMethod.GET, Collections.emptyMap()),
                        false,
                        true
                },
                new Object[]{
                        REQ_MAP_WITH_PATH_PATTERN,
                        setupHttpMessage("/path/wrong-pattern", HttpMethod.GET, Collections.emptyMap()),
                        true,
                        false
                },
                new Object[]{
                        REQ_MAP_WITH_PATH_PATTERN,
                        setupHttpMessage("/path/wrong-pattern", HttpMethod.GET, Collections.emptyMap()),
                        false,
                        false
                },
                new Object[]{
                        REQ_MAP_WITH_PATH_PATTERN,
                        setupHttpMessage("", HttpMethod.GET, Collections.emptyMap()),
                        true,
                        false
                },
                new Object[]{
                        REQ_MAP_WITH_PATH_PATTERN,
                        setupHttpMessage("", HttpMethod.GET, Collections.emptyMap()),
                        false,
                        false
                },

                new Object[]{
                        REQ_MAP_WITH_PUT_METHOD,
                        setupHttpMessage("/any-path", HttpMethod.PUT, Collections.emptyMap()),
                        true,
                        true
                },
                new Object[]{
                        REQ_MAP_WITH_PUT_METHOD,
                        setupHttpMessage("", HttpMethod.GET, Collections.emptyMap()),
                        true,
                        false
                },

                new Object[]{
                        REQ_MAP_WITH_QUERY_PARAMS,
                        setupHttpMessage("/any-path", HttpMethod.GET, Collections.singletonMap("a", Collections.singleton("1"))),
                        true,
                        true
                },
                new Object[]{
                        REQ_MAP_WITH_QUERY_PARAMS,
                        setupHttpMessage("/any-path", HttpMethod.GET, Collections.singletonMap("a", Collections.emptySet())),
                        true,
                        true
                },
                new Object[]{
                        REQ_MAP_WITH_QUERY_PARAMS,
                        setupHttpMessage("/any-path", HttpMethod.GET, Stream.of("a=1","b=2").map(item -> item.split("=")).collect(Collectors.toMap(keyValuePair -> keyValuePair[0], keyValuePair -> Collections.singleton(keyValuePair[1])))),
                        true,
                        false
                },
                new Object[]{
                        REQ_MAP_WITH_QUERY_PARAMS,
                        setupHttpMessage("/any-path", HttpMethod.GET, Collections.singletonMap("c", Collections.singleton("3"))),
                        true,
                        false
                },
                new Object[]{
                        REQ_MAP_WITH_QUERY_PARAMS,
                        setupHttpMessage("/any-path", HttpMethod.GET, Collections.emptyMap()),
                        true,
                        false
                },

                new Object[]{
                        REQ_MAP_WITH_ALL_SUPPORTED_RESTRICTIONS,
                        setupHttpMessage("/path/value", HttpMethod.GET, Collections.singletonMap("a", Collections.singleton("1"))),
                        true,
                        true
                },
                new Object[]{
                        REQ_MAP_WITH_ALL_SUPPORTED_RESTRICTIONS,
                        setupHttpMessage("/wrong-path", HttpMethod.GET, Collections.singletonMap("a", Collections.singleton("1"))),
                        true,
                        false
                },
                new Object[]{
                        REQ_MAP_WITH_ALL_SUPPORTED_RESTRICTIONS,
                        setupHttpMessage("/path/value", HttpMethod.PUT, Collections.singletonMap("a", Collections.singleton("1"))),
                        true,
                        false
                },
                new Object[]{
                        REQ_MAP_WITH_ALL_SUPPORTED_RESTRICTIONS,
                        setupHttpMessage("/path/value", HttpMethod.GET, Collections.emptyMap()),
                        true,
                        false
                },
        };
    }

    @Test(dataProvider = "dpScenarios")
    public void testCheckRequestSupported(RequestMapping requestMapping, HttpMessage httpMessage, boolean exactMatch, boolean expectedResult) throws Exception {
        boolean actual = cut.checkRequestPathSupported(httpMessage, requestMapping, exactMatch)
                && cut.checkRequestMethodSupported(httpMessage, requestMapping)
                && cut.checkRequestQueryParamsSupported(httpMessage, requestMapping);
        Assert.assertEquals(actual, expectedResult);
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

    private static RequestMapping getRequestMapping(SimulatorScenario scenario) {
        return AnnotationUtils.findAnnotation(scenario.getClass(), RequestMapping.class);
    }

    private HttpMessage setupHttpMessage(String path, HttpMethod method, Map<String, Collection<String>> queryParams) {
        final HttpMessage httpMessage = Mockito.mock(HttpMessage.class);
        when(httpMessage.getPath()).thenReturn(path);
        when(httpMessage.getRequestMethod()).thenReturn(method);
        when(httpMessage.getQueryParams()).thenReturn(queryParams);
        return httpMessage;
    }
}
