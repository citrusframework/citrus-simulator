package com.consol.citrus.simulator.http;

import com.consol.citrus.http.message.HttpMessage;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.scenario.AbstractSimulatorScenario;
import com.consol.citrus.simulator.scenario.Scenario;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;

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
        scenarioMapper.getScenarios().add(new IssueScenario());
        scenarioMapper.getScenarios().add(new FooScenario());
        scenarioMapper.getScenarios().add(new GetFooScenario());
        scenarioMapper.getScenarios().add(new PutFooScenario());
        scenarioMapper.getScenarios().add(new OtherScenario());

        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().path("/issues/foo")), "FooScenario");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().path("/issues/foo").method(HttpMethod.GET)), "GetFooScenario");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().path("/issues/foo").method(HttpMethod.PUT)), "PutFooScenario");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().path("/issues/other")), "OtherScenario");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().path("/issues/bar").method(HttpMethod.GET)), "IssueScenario");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().path("/issues/bar").method(HttpMethod.DELETE)), "IssueScenario");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().path("/issues/bar").method(HttpMethod.PUT)), "default");
        Assert.assertEquals(scenarioMapper.getMappingKey(new HttpMessage().path("/issues/bar")), "default");
        Assert.assertEquals(scenarioMapper.getMappingKey(null), "default");
    }

    @Scenario("FooScenario")
    @RequestMapping(value = "/issues/foo", method = RequestMethod.POST)
    private class FooScenario extends AbstractSimulatorScenario {
    }

    @Scenario("GetFooScenario")
    @RequestMapping(value = "/issues/foo", method = RequestMethod.GET)
    private class GetFooScenario extends AbstractSimulatorScenario {
    }

    @Scenario("PutFooScenario")
    @RequestMapping(value = "/issues/foo", method = RequestMethod.PUT)
    private class PutFooScenario extends AbstractSimulatorScenario {
    }

    @Scenario("IssueScenario")
    @RequestMapping(value = "/issues/{name}", method = { RequestMethod.GET, RequestMethod.DELETE })
    private class IssueScenario extends AbstractSimulatorScenario {
    }

    @Scenario("OtherScenario")
    @RequestMapping("/issues/other")
    private class OtherScenario extends AbstractSimulatorScenario {
    }
}