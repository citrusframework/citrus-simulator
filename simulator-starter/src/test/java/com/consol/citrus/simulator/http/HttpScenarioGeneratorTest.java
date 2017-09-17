package com.consol.citrus.simulator.http;

import org.mockito.Mockito;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

/**
 * @author Christoph Deppisch
 */
public class HttpScenarioGeneratorTest {

    private ConfigurableListableBeanFactory beanFactory = Mockito.mock(ConfigurableListableBeanFactory.class);

    @Test
    public void testGenerateScenarios() {
        HttpScenarioGenerator scenarioGenerator = new HttpScenarioGenerator(new ClassPathResource("swagger/swagger-api.json"));

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
}
