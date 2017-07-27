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
public class AnnotationRequestMappingKeyExtractorTest {

    private AnnotationRequestMappingKeyExtractor mappingKeyExtractor = new AnnotationRequestMappingKeyExtractor();

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
        mappingKeyExtractor.getScenarios().add(new FooListScenario());
        mappingKeyExtractor.getScenarios().add(new FooScenario());
        mappingKeyExtractor.getScenarios().add(new FooDetailScenario());
        mappingKeyExtractor.getScenarios().add(new BarListScenario());
        mappingKeyExtractor.getScenarios().add(new BarScenario());
        mappingKeyExtractor.getScenarios().add(new BarDetailScenario());

        HttpMessage request = new HttpMessage();
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