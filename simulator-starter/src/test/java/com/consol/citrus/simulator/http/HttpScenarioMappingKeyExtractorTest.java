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
public class HttpScenarioMappingKeyExtractorTest {
    private HttpScenarioMappingKeyExtractor mappingKeyExtractor = new HttpScenarioMappingKeyExtractor();

    @Mock
    private SimulatorConfigurationProperties simulatorConfiguration;

    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mappingKeyExtractor.setConfiguration(simulatorConfiguration);

        when(simulatorConfiguration.getDefaultScenario()).thenReturn("default");
    }

    @Test
    public void testGetMappingKey() {
        Operation operation = Mockito.mock(Operation.class);

        mappingKeyExtractor.getHttpScenarios().add(new HttpOperationScenario("/issues/foos", HttpMethod.GET, operation, Collections.emptyMap()));
        mappingKeyExtractor.getHttpScenarios().add(new HttpOperationScenario("/issues/foo/{id}", HttpMethod.GET, operation, Collections.emptyMap()));
        mappingKeyExtractor.getHttpScenarios().add(new HttpOperationScenario("/issues/foo/detail", HttpMethod.GET, operation, Collections.emptyMap()));
        mappingKeyExtractor.getHttpScenarios().add(new HttpOperationScenario("/issues/bars", HttpMethod.GET, operation, Collections.emptyMap()));
        mappingKeyExtractor.getHttpScenarios().add(new HttpOperationScenario("/issues/bar/{id}", HttpMethod.GET, operation, Collections.emptyMap()));
        mappingKeyExtractor.getHttpScenarios().add(new HttpOperationScenario("/issues/bar/detail", HttpMethod.GET, operation, Collections.emptyMap()));

        when(operation.getOperationId())
                .thenReturn("fooListScenario")
                .thenReturn("barListScenario")
                .thenReturn("fooScenario")
                .thenReturn("barScenario")
                .thenReturn("fooDetailScenario")
                .thenReturn("barDetailScenario");

        HttpMessage request = new HttpMessage();
        request.method(HttpMethod.GET);
        Assert.assertEquals(mappingKeyExtractor.getMappingKey(request), "default");

        request.path("/issues");
        Assert.assertEquals(mappingKeyExtractor.getMappingKey(request), "default");

        request.path("/issues/foos");
        Assert.assertEquals(mappingKeyExtractor.getMappingKey(request), "fooListScenario");

        request.path("/issues/bars");
        Assert.assertEquals(mappingKeyExtractor.getMappingKey(request), "barListScenario");

        request.path("/issues/foo/1");
        Assert.assertEquals(mappingKeyExtractor.getMappingKey(request), "fooScenario");

        request.path("/issues/bar/1");
        Assert.assertEquals(mappingKeyExtractor.getMappingKey(request), "barScenario");

        request.path("/issues/foo/detail");
        Assert.assertEquals(mappingKeyExtractor.getMappingKey(request), "fooDetailScenario");

        request.path("/issues/bar/detail");
        Assert.assertEquals(mappingKeyExtractor.getMappingKey(request), "barDetailScenario");
    }

}