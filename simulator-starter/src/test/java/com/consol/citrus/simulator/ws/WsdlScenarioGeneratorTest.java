package com.consol.citrus.simulator.ws;

import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Christoph Deppisch
 */
public class WsdlScenarioGeneratorTest {

    private ConfigurableListableBeanFactory beanFactory = Mockito.mock(ConfigurableListableBeanFactory.class);

    private String input = "<v1:TestRequest name=\"string\" id=\"100\" flag=\"false\" xmlns:v1=\"http://www.citrusframework.org/schema/samples/TestService/v1\">\n" +
            "  <v1:name>string</v1:name>\n" +
            "  <v1:id>100</v1:id>\n" +
            "  <v1:flag>true</v1:flag>\n" +
            "  <v1:restricted>stringstri</v1:restricted>\n" +
            "</v1:TestRequest>";

    private String output = "<v1:TestResponse name=\"string\" id=\"100\" flag=\"false\" xmlns:v1=\"http://www.citrusframework.org/schema/samples/TestService/v1\">\n" +
            "  <v1:name>string</v1:name>\n" +
            "  <v1:id>100</v1:id>\n" +
            "  <v1:flag>true</v1:flag>\n" +
            "  <v1:restricted>stringstri</v1:restricted>\n" +
            "</v1:TestResponse>";

    @Test(dataProvider = "wsdlDataProvider")
    public void testGenerateScenarios(String scenarioName, WsdlScenarioGenerator.WsdlScenarioNamingStrategy namingStrategy, String soapAction, String input, String output) {
        WsdlScenarioGenerator scenarioGenerator = new WsdlScenarioGenerator(new ClassPathResource("schema/TestService.wsdl"));
        scenarioGenerator.setSimulatorConfiguration(new SimulatorConfigurationProperties());

        scenarioGenerator.setNamingStrategy(namingStrategy);

        doAnswer(invocation -> {
            WsdlOperationScenario scenario = (WsdlOperationScenario) invocation.getArguments()[1];

            Assert.assertEquals(scenario.getSoapAction(), soapAction);
            Assert.assertEquals(scenario.getInput(), input);
            Assert.assertEquals(scenario.getOutput(), output);

            return null;
        }).when(beanFactory).registerSingleton(eq(scenarioName), any(WsdlOperationScenario.class));

        scenarioGenerator.postProcessBeanFactory(beanFactory);

        verify(beanFactory).registerSingleton(eq(scenarioName), any(WsdlOperationScenario.class));
    }

    @DataProvider
    public Object[][] wsdlDataProvider() {
        return new Object[][] {
            new Object[] { "TestRequest", WsdlScenarioGenerator.WsdlScenarioNamingStrategy.INPUT, "/TestService/test", input, output },
            new Object[] { "test", WsdlScenarioGenerator.WsdlScenarioNamingStrategy.OPERATION, "/TestService/test", input, output },
            new Object[] { "/TestService/test", WsdlScenarioGenerator.WsdlScenarioNamingStrategy.SOAP_ACTION, "/TestService/test", input, output }
        };
    }

}