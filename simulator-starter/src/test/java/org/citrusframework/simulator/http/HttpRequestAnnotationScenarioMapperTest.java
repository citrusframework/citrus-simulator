package org.citrusframework.simulator.http;

import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * @author Christoph Deppisch
 */
@ExtendWith(MockitoExtension.class)
class HttpRequestAnnotationScenarioMapperTest {

    @Mock
    private SimulatorConfigurationProperties simulatorConfiguration;

    private HttpRequestAnnotationScenarioMapper fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new HttpRequestAnnotationScenarioMapper();
        fixture.setConfiguration(simulatorConfiguration);

        when(simulatorConfiguration.getDefaultScenario()).thenReturn("default");
    }

    @Test
    void testGetMappingKey() {
        fixture.setScenarioList(Arrays.asList(new IssueScenario(),
            new FooScenario(),
            new GetFooScenario(),
            new PutFooScenario(),
            new OtherScenario()));

        assertEquals(fixture.getMappingKey(new HttpMessage().path("/issues/foo")), "FooScenario");
        assertEquals(fixture.getMappingKey(new HttpMessage().path("/issues/foo").method(HttpMethod.GET)), "GetFooScenario");
        assertEquals(fixture.getMappingKey(new HttpMessage().path("/issues/foo").method(HttpMethod.PUT)), "PutFooScenario");
        assertEquals(fixture.getMappingKey(new HttpMessage().path("/issues/other")), "OtherScenario");
        assertEquals(fixture.getMappingKey(new HttpMessage().path("/issues/bar").method(HttpMethod.GET)), "IssueScenario");
        assertEquals(fixture.getMappingKey(new HttpMessage().path("/issues/bar").method(HttpMethod.DELETE)), "IssueScenario");
        assertEquals(fixture.getMappingKey(new HttpMessage().path("/issues/bar").method(HttpMethod.PUT)), "default");
        assertEquals(fixture.getMappingKey(new HttpMessage().path("/issues/bar")), "default");
        assertEquals(fixture.getMappingKey(null), "default");

        fixture.setUseDefaultMapping(false);

        assertThrows(CitrusRuntimeException.class, () -> fixture.getMappingKey(new HttpMessage().path("/issues/bar").method(HttpMethod.PUT)));
        assertThrows(CitrusRuntimeException.class, () -> fixture.getMappingKey(new HttpMessage().path("/issues/bar")));
        assertThrows(CitrusRuntimeException.class, () -> fixture.getMappingKey(null));
    }

    @Scenario("FooScenario")
    @RequestMapping(value = "/issues/foo", method = RequestMethod.POST)
    private static class FooScenario extends AbstractSimulatorScenario {
    }

    @Scenario("GetFooScenario")
    @RequestMapping(value = "/issues/foo", method = RequestMethod.GET)
    private static class GetFooScenario extends AbstractSimulatorScenario {
    }

    @Scenario("PutFooScenario")
    @RequestMapping(value = "/issues/foo", method = RequestMethod.PUT)
    private static class PutFooScenario extends AbstractSimulatorScenario {
    }

    @Scenario("IssueScenario")
    @RequestMapping(value = "/issues/{name}", method = { RequestMethod.GET, RequestMethod.DELETE })
    private static class IssueScenario extends AbstractSimulatorScenario {
    }

    @Scenario("OtherScenario")
    @RequestMapping("/issues/other")
    private static class OtherScenario extends AbstractSimulatorScenario {
    }
}
