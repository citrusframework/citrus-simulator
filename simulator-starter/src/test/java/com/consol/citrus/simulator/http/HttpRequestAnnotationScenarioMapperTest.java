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
        scenarioMapper.getScenarios().add(new FooListScenario());
        scenarioMapper.getScenarios().add(new FooScenario());
        scenarioMapper.getScenarios().add(new FooDetailScenario());
        scenarioMapper.getScenarios().add(new BarListScenario());
        scenarioMapper.getScenarios().add(new BarScenario());
        scenarioMapper.getScenarios().add(new BarDetailScenario());

        HttpMessage request = new HttpMessage();
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

    @Scenario("fooListScenario")
    @RequestMapping("/issues/foos")
    private class FooListScenario extends AbstractSimulatorScenario {
    }

    @Scenario("fooScenario")
    @RequestMapping("/issues/foo/{id}")
    private class FooScenario extends AbstractSimulatorScenario {
    }

    @Scenario("fooDetailScenario")
    @RequestMapping("/issues/foo/detail")
    private class FooDetailScenario extends AbstractSimulatorScenario {
    }

    @Scenario("barListScenario")
    @RequestMapping("/issues/bars")
    private class BarListScenario extends AbstractSimulatorScenario {
    }

    @Scenario("barScenario")
    @RequestMapping("/issues/bar/{id}")
    private class BarScenario extends AbstractSimulatorScenario {
    }

    @Scenario("barDetailScenario")
    @RequestMapping("/issues/bar/detail")
    private class BarDetailScenario extends AbstractSimulatorScenario {
    }

}