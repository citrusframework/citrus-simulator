package com.consol.citrus.simulator.http;

import com.consol.citrus.http.message.HttpMessage;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.scenario.AbstractSimulatorScenario;
import com.consol.citrus.simulator.scenario.Scenario;
import org.mockito.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Christoph Deppisch
 */
public class HttpRequestAnnotationScenarioMapperTest {

    private HttpRequestAnnotationScenarioMapper scenarioMapper = new HttpRequestAnnotationScenarioMapper();

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
        scenarioMapper.getScenarios().add(new FooScenario());

        HttpMessage request = new HttpMessage();
        request.path("/issues/foo");
        Assert.assertEquals(scenarioMapper.getMappingKey(request), "FooScenario");

        Assert.assertEquals(scenarioMapper.getMappingKey(null), "default");
    }

    @Scenario("FooScenario")
    @RequestMapping("/issues/foo")
    private class FooScenario extends AbstractSimulatorScenario {
    }
}