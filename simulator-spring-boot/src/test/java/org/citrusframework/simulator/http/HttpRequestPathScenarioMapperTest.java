package org.citrusframework.simulator.http;

import io.swagger.models.Operation;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * @author Christoph Deppisch
 */
@ExtendWith(MockitoExtension.class)
class HttpRequestPathScenarioMapperTest {

    @Mock
    private SimulatorConfigurationProperties simulatorConfigurationMock;

    private HttpRequestPathScenarioMapper fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new HttpRequestPathScenarioMapper();
        fixture.setConfiguration(simulatorConfigurationMock);

        doReturn("default").when(simulatorConfigurationMock).getDefaultScenario();
    }

    @Test
    void testGetMappingKey() {
        Operation operation = Mockito.mock(Operation.class);

        fixture.setScenarioList(Arrays.asList(new HttpOperationScenario("/issues/foos", RequestMethod.GET, operation, Collections.emptyMap()),
            new HttpOperationScenario("/issues/foos", RequestMethod.POST, operation, Collections.emptyMap()),
            new HttpOperationScenario("/issues/foo/{id}", RequestMethod.GET, operation, Collections.emptyMap()),
            new HttpOperationScenario("/issues/foo/detail", RequestMethod.GET, operation, Collections.emptyMap()),
            new HttpOperationScenario("/issues/bars", RequestMethod.GET, operation, Collections.emptyMap()),
            new HttpOperationScenario("/issues/bar/{id}", RequestMethod.GET, operation, Collections.emptyMap()),
            new HttpOperationScenario("/issues/bar/detail", RequestMethod.GET, operation, Collections.emptyMap())));

        when(operation.getOperationId())
            .thenReturn("fooListScenario")
            .thenReturn("fooListPostScenario")
            .thenReturn("barListScenario")
            .thenReturn("fooScenario")
            .thenReturn("barScenario")
            .thenReturn("fooDetailScenario")
            .thenReturn("barDetailScenario");

        assertEquals(fixture.getMappingKey(new HttpMessage().method(HttpMethod.GET)), "default");
        assertEquals(fixture.getMappingKey(new HttpMessage().method(HttpMethod.POST)), "default");
        assertEquals(fixture.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues")), "default");
        assertEquals(fixture.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues/foos")), "fooListScenario");
        assertEquals(fixture.getMappingKey(new HttpMessage().method(HttpMethod.POST).path("/issues/foos")), "fooListPostScenario");
        assertEquals(fixture.getMappingKey(new HttpMessage().method(HttpMethod.PUT).path("/issues/foos")), "default");
        assertEquals(fixture.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues/bars")), "barListScenario");
        assertEquals(fixture.getMappingKey(new HttpMessage().method(HttpMethod.DELETE).path("/issues/bars")), "default");
        assertEquals(fixture.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues/foo/1")), "fooScenario");
        assertEquals(fixture.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues/bar/1")), "barScenario");
        assertEquals(fixture.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues/foo/detail")), "fooDetailScenario");
        assertEquals(fixture.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues/bar/detail")), "barDetailScenario");

        fixture.setUseDefaultMapping(false);

        HttpMessage httpGetMessage = new HttpMessage().method(HttpMethod.GET);
        assertThrows(CitrusRuntimeException.class, () -> fixture.getMappingKey(httpGetMessage));

        HttpMessage httpGetIssuesMessage = new HttpMessage().method(HttpMethod.GET).path("/issues");
        assertThrows(CitrusRuntimeException.class, () -> fixture.getMappingKey(httpGetIssuesMessage));
    }
}
