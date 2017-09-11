package com.consol.citrus.simulator.http;

import com.consol.citrus.http.message.HttpMessage;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import io.swagger.models.Operation;
import org.mockito.*;
import org.springframework.http.HttpMethod;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;

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

        scenarioMapper.getHttpScenarios().add(new HttpOperationScenario("/issues/foos", HttpMethod.GET, operation, Collections.emptyMap()));
        scenarioMapper.getHttpScenarios().add(new HttpOperationScenario("/issues/foo/{id}", HttpMethod.GET, operation, Collections.emptyMap()));
        scenarioMapper.getHttpScenarios().add(new HttpOperationScenario("/issues/foo/detail", HttpMethod.GET, operation, Collections.emptyMap()));
        scenarioMapper.getHttpScenarios().add(new HttpOperationScenario("/issues/bars", HttpMethod.GET, operation, Collections.emptyMap()));
        scenarioMapper.getHttpScenarios().add(new HttpOperationScenario("/issues/bar/{id}", HttpMethod.GET, operation, Collections.emptyMap()));
        scenarioMapper.getHttpScenarios().add(new HttpOperationScenario("/issues/bar/detail", HttpMethod.GET, operation, Collections.emptyMap()));

        when(operation.getOperationId())
                .thenReturn("fooListScenario")
                .thenReturn("barListScenario")
                .thenReturn("fooScenario")
                .thenReturn("barScenario")
                .thenReturn("fooDetailScenario")
                .thenReturn("barDetailScenario");

        HttpMessage request = new HttpMessage();
        request.method(HttpMethod.GET);
        Assert.assertEquals(scenarioMapper.getMappingKey(request), "default");

        request.path("/issues");
        Assert.assertEquals(scenarioMapper.getMappingKey(request), "default");

        request.path("/issues/foos");
        Assert.assertEquals(scenarioMapper.getMappingKey(request), "fooListScenario");

        request.path("/issues/bars");
        Assert.assertEquals(scenarioMapper.getMappingKey(request), "barListScenario");

        request.path("/issues/foo/1");
        Assert.assertEquals(scenarioMapper.getMappingKey(request), "fooScenario");

        request.path("/issues/bar/1");
        Assert.assertEquals(scenarioMapper.getMappingKey(request), "barScenario");

        request.path("/issues/foo/detail");
        Assert.assertEquals(scenarioMapper.getMappingKey(request), "fooDetailScenario");

        request.path("/issues/bar/detail");
        Assert.assertEquals(scenarioMapper.getMappingKey(request), "barDetailScenario");
    }

}