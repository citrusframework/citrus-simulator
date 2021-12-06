package org.citrusframework.simulator.http;

import io.swagger.models.Operation;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

/**
 * @author Christoph Deppisch
 */
public class HttpScenarioGeneratorTest {

    private ConfigurableListableBeanFactory beanFactory = Mockito.mock(ConfigurableListableBeanFactory.class);
    private DefaultListableBeanFactory beanRegistry = Mockito.mock(DefaultListableBeanFactory.class);

    @Test
    public void testGenerateScenarios() {
        HttpScenarioGenerator scenarioGenerator = new HttpScenarioGenerator(new ClassPathResource("swagger/swagger-api.json"));

        reset(beanFactory);

        doAnswer(invocation -> {
            HttpOperationScenario scenario = (HttpOperationScenario) invocation.getArguments()[1];

            Assert.assertNotNull(scenario.getOperation());
            Assert.assertEquals(scenario.getPath(), "/v2/pet");
            Assert.assertEquals(scenario.getMethod(), HttpMethod.POST);

            return null;
        }).when(beanFactory).registerSingleton(eq("addPet"), any(HttpOperationScenario.class));

        doAnswer(invocation -> {
            HttpOperationScenario scenario = (HttpOperationScenario) invocation.getArguments()[1];

            Assert.assertNotNull(scenario.getOperation());
            Assert.assertEquals(scenario.getPath(), "/v2/pet/{petId}");
            Assert.assertEquals(scenario.getMethod(), HttpMethod.GET);

            return null;
        }).when(beanFactory).registerSingleton(eq("getPetById"), any(HttpOperationScenario.class));

        doAnswer(invocation -> {
            HttpOperationScenario scenario = (HttpOperationScenario) invocation.getArguments()[1];

            Assert.assertNotNull(scenario.getOperation());
            Assert.assertEquals(scenario.getPath(), "/v2/pet/{petId}");
            Assert.assertEquals(scenario.getMethod(), HttpMethod.DELETE);

            return null;
        }).when(beanFactory).registerSingleton(eq("deletePet"), any(HttpOperationScenario.class));

        scenarioGenerator.postProcessBeanFactory(beanFactory);

        verify(beanFactory).registerSingleton(eq("addPet"), any(HttpOperationScenario.class));
        verify(beanFactory).registerSingleton(eq("getPetById"), any(HttpOperationScenario.class));
        verify(beanFactory).registerSingleton(eq("deletePet"), any(HttpOperationScenario.class));
    }

    @Test
    public void testGenerateScenariosWithBeandDefinitionRegistry() {
        HttpScenarioGenerator scenarioGenerator = new HttpScenarioGenerator(new ClassPathResource("swagger/swagger-api.json"));

        reset(beanRegistry);

        doAnswer(invocation -> {
            BeanDefinition scenario = (BeanDefinition) invocation.getArguments()[1];

            Assert.assertEquals(scenario.getConstructorArgumentValues().getArgumentValue(0, String.class).getValue(), "/v2/pet");
            Assert.assertEquals(scenario.getConstructorArgumentValues().getArgumentValue(1, HttpMethod.class).getValue(), HttpMethod.POST);
            Assert.assertNotNull(scenario.getConstructorArgumentValues().getArgumentValue(2, Operation.class).getValue());
            Assert.assertNull(scenario.getPropertyValues().get("inboundDataDictionary"));
            Assert.assertNull(scenario.getPropertyValues().get("outboundDataDictionary"));

            return null;
        }).when(beanRegistry).registerBeanDefinition(eq("addPet"), any(BeanDefinition.class));

        doAnswer(invocation -> {
            BeanDefinition scenario = (BeanDefinition) invocation.getArguments()[1];

            Assert.assertEquals(scenario.getConstructorArgumentValues().getArgumentValue(0, String.class).getValue(), "/v2/pet/{petId}");
            Assert.assertEquals(scenario.getConstructorArgumentValues().getArgumentValue(1, HttpMethod.class).getValue(), HttpMethod.GET);
            Assert.assertNotNull(scenario.getConstructorArgumentValues().getArgumentValue(2, Operation.class).getValue());
            Assert.assertNull(scenario.getPropertyValues().get("inboundDataDictionary"));
            Assert.assertNull(scenario.getPropertyValues().get("outboundDataDictionary"));

            return null;
        }).when(beanRegistry).registerBeanDefinition(eq("getPetById"), any(BeanDefinition.class));

        doAnswer(invocation -> {
            BeanDefinition scenario = (BeanDefinition) invocation.getArguments()[1];

            Assert.assertEquals(scenario.getConstructorArgumentValues().getArgumentValue(0, String.class).getValue(), "/v2/pet/{petId}");
            Assert.assertEquals(scenario.getConstructorArgumentValues().getArgumentValue(1, HttpMethod.class).getValue(), HttpMethod.DELETE);
            Assert.assertNotNull(scenario.getConstructorArgumentValues().getArgumentValue(2, Operation.class).getValue());
            Assert.assertNull(scenario.getPropertyValues().get("inboundDataDictionary"));
            Assert.assertNull(scenario.getPropertyValues().get("outboundDataDictionary"));

            return null;
        }).when(beanRegistry).registerBeanDefinition(eq("deletePet"), any(BeanDefinition.class));

        scenarioGenerator.postProcessBeanFactory(beanRegistry);

        verify(beanRegistry).registerBeanDefinition(eq("addPet"), any(BeanDefinition.class));
        verify(beanRegistry).registerBeanDefinition(eq("getPetById"), any(BeanDefinition.class));
        verify(beanRegistry).registerBeanDefinition(eq("deletePet"), any(BeanDefinition.class));
    }

    @Test
    public void testGenerateScenariosWithDataDictionaries() {
        HttpScenarioGenerator scenarioGenerator = new HttpScenarioGenerator(new ClassPathResource("swagger/swagger-api.json"));

        reset(beanRegistry);

        BeanDefinition inboundJsonDataDictionary = Mockito.mock(BeanDefinition.class);
        BeanDefinition outboundJsonDataDictionary = Mockito.mock(BeanDefinition.class);

        when(beanRegistry.containsBeanDefinition("inboundJsonDataDictionary")).thenReturn(true);
        when(beanRegistry.containsBeanDefinition("outboundJsonDataDictionary")).thenReturn(true);

        when(beanRegistry.getBeanDefinition("inboundJsonDataDictionary")).thenReturn(inboundJsonDataDictionary);
        when(beanRegistry.getBeanDefinition("outboundJsonDataDictionary")).thenReturn(outboundJsonDataDictionary);

        doAnswer(invocation -> {
            BeanDefinition scenario = (BeanDefinition) invocation.getArguments()[1];

            Assert.assertEquals(scenario.getConstructorArgumentValues().getArgumentValue(0, String.class).getValue(), "/v2/pet");
            Assert.assertEquals(scenario.getConstructorArgumentValues().getArgumentValue(1, HttpMethod.class).getValue(), HttpMethod.POST);
            Assert.assertNotNull(scenario.getConstructorArgumentValues().getArgumentValue(2, Operation.class).getValue());
            Assert.assertNotNull(scenario.getPropertyValues().get("inboundDataDictionary"));
            Assert.assertNotNull(scenario.getPropertyValues().get("outboundDataDictionary"));

            return null;
        }).when(beanRegistry).registerBeanDefinition(eq("addPet"), any(BeanDefinition.class));

        doAnswer(invocation -> {
            BeanDefinition scenario = (BeanDefinition) invocation.getArguments()[1];

            Assert.assertEquals(scenario.getConstructorArgumentValues().getArgumentValue(0, String.class).getValue(), "/v2/pet/{petId}");
            Assert.assertEquals(scenario.getConstructorArgumentValues().getArgumentValue(1, HttpMethod.class).getValue(), HttpMethod.GET);
            Assert.assertNotNull(scenario.getConstructorArgumentValues().getArgumentValue(2, Operation.class).getValue());
            Assert.assertNotNull(scenario.getPropertyValues().get("inboundDataDictionary"));
            Assert.assertNotNull(scenario.getPropertyValues().get("outboundDataDictionary"));

            return null;
        }).when(beanRegistry).registerBeanDefinition(eq("getPetById"), any(BeanDefinition.class));

        doAnswer(invocation -> {
            BeanDefinition scenario = (BeanDefinition) invocation.getArguments()[1];

            Assert.assertEquals(scenario.getConstructorArgumentValues().getArgumentValue(0, String.class).getValue(), "/v2/pet/{petId}");
            Assert.assertEquals(scenario.getConstructorArgumentValues().getArgumentValue(1, HttpMethod.class).getValue(), HttpMethod.DELETE);
            Assert.assertNotNull(scenario.getConstructorArgumentValues().getArgumentValue(2, Operation.class).getValue());
            Assert.assertNotNull(scenario.getPropertyValues().get("inboundDataDictionary"));
            Assert.assertNotNull(scenario.getPropertyValues().get("outboundDataDictionary"));

            return null;
        }).when(beanRegistry).registerBeanDefinition(eq("deletePet"), any(BeanDefinition.class));

        scenarioGenerator.postProcessBeanFactory(beanRegistry);

        verify(beanRegistry).registerBeanDefinition(eq("addPet"), any(BeanDefinition.class));
        verify(beanRegistry).registerBeanDefinition(eq("getPetById"), any(BeanDefinition.class));
        verify(beanRegistry).registerBeanDefinition(eq("deletePet"), any(BeanDefinition.class));
    }
}
