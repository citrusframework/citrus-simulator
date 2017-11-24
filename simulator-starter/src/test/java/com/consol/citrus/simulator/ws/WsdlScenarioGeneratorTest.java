package com.consol.citrus.simulator.ws;

import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
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
    private DefaultListableBeanFactory beanRegistry = Mockito.mock(DefaultListableBeanFactory.class);

    private String input = String.format("<v1:TestRequest name=\"string\" id=\"100\" flag=\"false\" xmlns:v1=\"http://www.citrusframework.org/schema/samples/TestService/v1\">%n" +
            "  <v1:name>string</v1:name>%n" +
            "  <v1:id>100</v1:id>%n" +
            "  <v1:flag>true</v1:flag>%n" +
            "  <v1:restricted>stringstri</v1:restricted>%n" +
            "</v1:TestRequest>");

    private String output = String.format("<v1:TestResponse name=\"string\" id=\"100\" flag=\"false\" xmlns:v1=\"http://www.citrusframework.org/schema/samples/TestService/v1\">%n" +
            "  <v1:name>string</v1:name>%n" +
            "  <v1:id>100</v1:id>%n" +
            "  <v1:flag>true</v1:flag>%n" +
            "  <v1:restricted>stringstri</v1:restricted>%n" +
            "</v1:TestResponse>");

    @Test(dataProvider = "wsdlDataProvider")
    public void testGenerateScenarios(String scenarioName, WsdlScenarioGenerator.WsdlScenarioNamingStrategy namingStrategy, String soapAction, String input, String output) {
        WsdlScenarioGenerator scenarioGenerator = new WsdlScenarioGenerator(new ClassPathResource("schema/TestService.wsdl"));

        reset(beanFactory);

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

    @Test(dataProvider = "wsdlDataProvider")
    public void testGenerateScenariosWithRegistry(String scenarioName, WsdlScenarioGenerator.WsdlScenarioNamingStrategy namingStrategy, String soapAction, String input, String output) {
        WsdlScenarioGenerator scenarioGenerator = new WsdlScenarioGenerator(new ClassPathResource("schema/TestService.wsdl"));

        reset(beanRegistry);
        
        scenarioGenerator.setNamingStrategy(namingStrategy);

        doAnswer(invocation -> {
            BeanDefinition scenario = (BeanDefinition) invocation.getArguments()[1];

            Assert.assertEquals(scenario.getPropertyValues().get("soapAction"), soapAction);
            Assert.assertEquals(scenario.getPropertyValues().get("input"), input);
            Assert.assertEquals(scenario.getPropertyValues().get("output"), output);
            Assert.assertNull(scenario.getPropertyValues().get("inboundDataDictionary"));
            Assert.assertNull(scenario.getPropertyValues().get("outboundDataDictionary"));

            return null;
        }).when(beanRegistry).registerBeanDefinition(eq(scenarioName), any(BeanDefinition.class));

        scenarioGenerator.postProcessBeanFactory(beanRegistry);

        verify(beanRegistry).registerBeanDefinition(eq(scenarioName), any(BeanDefinition.class));
    }

    @Test
    public void testGenerateScenariosWithDataDictionaries() {
        WsdlScenarioGenerator scenarioGenerator = new WsdlScenarioGenerator(new ClassPathResource("schema/TestService.wsdl"));

        reset(beanRegistry);
        
        BeanDefinition inboundXmlDataDictionary = Mockito.mock(BeanDefinition.class);
        BeanDefinition outboundXmlDataDictionary = Mockito.mock(BeanDefinition.class);

        when(beanRegistry.containsBeanDefinition("inboundXmlDataDictionary")).thenReturn(true);
        when(beanRegistry.containsBeanDefinition("outboundXmlDataDictionary")).thenReturn(true);

        when(beanRegistry.getBeanDefinition("inboundXmlDataDictionary")).thenReturn(inboundXmlDataDictionary);
        when(beanRegistry.getBeanDefinition("outboundXmlDataDictionary")).thenReturn(outboundXmlDataDictionary);

        doAnswer(invocation -> {
            BeanDefinition scenario = (BeanDefinition) invocation.getArguments()[1];

            Assert.assertEquals(scenario.getPropertyValues().get("soapAction"), "/TestService/test");
            Assert.assertEquals(scenario.getPropertyValues().get("input"), input);
            Assert.assertEquals(scenario.getPropertyValues().get("output"), output);
            Assert.assertNotNull(scenario.getPropertyValues().get("inboundDataDictionary"));
            Assert.assertNotNull(scenario.getPropertyValues().get("outboundDataDictionary"));

            return null;
        }).when(beanRegistry).registerBeanDefinition(eq("TestRequest"), any(BeanDefinition.class));

        scenarioGenerator.postProcessBeanFactory(beanRegistry);

        verify(beanRegistry).registerBeanDefinition(eq("TestRequest"), any(BeanDefinition.class));
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