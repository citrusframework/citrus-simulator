package org.citrusframework.simulator.http;

import java.util.Arrays;
import java.util.Collections;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.http.message.HttpMessage;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import io.swagger.models.Operation;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;

/**
 * @author Christoph Deppisch
 */
public class HttpRequestPathScenarioMapperTest {
    private HttpRequestPathScenarioMapper scenarioMapper = new HttpRequestPathScenarioMapper();

    @Mock
    private SimulatorConfigurationProperties simulatorConfiguration;

    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
        scenarioMapper.setConfiguration(simulatorConfiguration);

        when(simulatorConfiguration.getDefaultScenario()).thenReturn("default");
    }

    @Test
    public void testGetMappingKey() {
        Operation operation = Mockito.mock(Operation.class);

        scenarioMapper.setScenarioList(Arrays.asList(new HttpOperationScenario("/issues/foos", HttpMethod.GET, operation, Collections.emptyMap()),
                                                        new HttpOperationScenario("/issues/foos", HttpMethod.POST, operation, Collections.emptyMap()),
                                                        new HttpOperationScenario("/issues/foo/{id}", HttpMethod.GET, operation, Collections.emptyMap()),
                                                        new HttpOperationScenario("/issues/foo/detail", HttpMethod.GET, operation, Collections.emptyMap()),
                                                        new HttpOperationScenario("/issues/bars", HttpMethod.GET, operation, Collections.emptyMap()),
                                                        new HttpOperationScenario("/issues/bar/{id}", HttpMethod.GET, operation, Collections.emptyMap()),
                                                        new HttpOperationScenario("/issues/bar/detail", HttpMethod.GET, operation, Collections.emptyMap())));

        when(operation.getOperationId())
                .thenReturn("fooListScenario")
                .thenReturn("fooListPostScenario")
                .thenReturn("barListScenario")
                .thenReturn("fooScenario")
                .thenReturn("barScenario")
                .thenReturn("fooDetailScenario")
                .thenReturn("barDetailScenario");

        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().method(HttpMethod.GET)), "default");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().method(HttpMethod.POST)), "default");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues")), "default");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues/foos")), "fooListScenario");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().method(HttpMethod.POST).path("/issues/foos")), "fooListPostScenario");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().method(HttpMethod.PUT).path("/issues/foos")), "default");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues/bars")), "barListScenario");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().method(HttpMethod.DELETE).path("/issues/bars")), "default");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues/foo/1")), "fooScenario");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues/bar/1")), "barScenario");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues/foo/detail")), "fooDetailScenario");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues/bar/detail")), "barDetailScenario");

        scenarioMapper.setUseDefaultMapping(false);

        Assert.assertThrows(CitrusRuntimeException.class, () -> scenarioMapper.getMappingKey(new HttpMessage().method(HttpMethod.GET)));
        Assert.assertThrows(CitrusRuntimeException.class, () -> scenarioMapper.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues")));
    }

}
