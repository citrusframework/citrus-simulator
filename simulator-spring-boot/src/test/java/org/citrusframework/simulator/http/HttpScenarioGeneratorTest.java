/*
 * Copyright the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.citrusframework.simulator.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.apicurio.datamodels.openapi.models.OasOperation;
import org.citrusframework.openapi.OpenApiSpecification;
import org.citrusframework.spi.Resources.ClasspathResource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * @author Christoph Deppisch
 */
@ExtendWith(MockitoExtension.class)
class HttpScenarioGeneratorTest {

    private HttpScenarioGenerator fixture;

    @ParameterizedTest
    @ValueSource(strings={"v2", "v3"})
    void generateHttpScenarios(String version) {
        ConfigurableListableBeanFactory beanFactoryMock = mock();

        mockBeanFactory(beanFactoryMock);

        OpenApiSpecification openApiSpecification = createOpenApiSpecification(version);
        fixture = new HttpScenarioGenerator(openApiSpecification);

        String addPetScenarioId = "POST_/api/petstore/"+version+"/pet";
        String getPetScenarioId = "GET_/api/petstore/"+version+"/pet/{petId}";
        String deletePetScenarioId = "DELETE_/api/petstore/"+version+"/pet/{petId}";

        String context = "/api/petstore/"+ version ;
        doAnswer(invocation -> {
            HttpOperationScenario scenario = (HttpOperationScenario) invocation.getArguments()[1];
            assertScenarioProperties(scenario, context+"/pet", addPetScenarioId, "POST");
            return null;
        }).when(beanFactoryMock).registerSingleton(eq(addPetScenarioId), any(HttpOperationScenario.class));

        doAnswer(invocation -> {
            HttpOperationScenario scenario = (HttpOperationScenario) invocation.getArguments()[1];
            assertScenarioProperties(scenario, context+"/pet/{petId}", getPetScenarioId, "GET");
            return null;
        }).when(beanFactoryMock).registerSingleton(eq(getPetScenarioId), any(HttpOperationScenario.class));

        doAnswer(invocation -> {
            HttpOperationScenario scenario = (HttpOperationScenario) invocation.getArguments()[1];
            assertScenarioProperties(scenario, context+"/pet/{petId}", deletePetScenarioId, "DELETE");
            return null;
        }).when(beanFactoryMock).registerSingleton(eq(deletePetScenarioId), any(HttpOperationScenario.class));

        fixture.postProcessBeanFactory(beanFactoryMock);

        verify(beanFactoryMock).registerSingleton(eq(addPetScenarioId), any(HttpOperationScenario.class));
        verify(beanFactoryMock).registerSingleton(eq(getPetScenarioId), any(HttpOperationScenario.class));
        verify(beanFactoryMock).registerSingleton(eq(deletePetScenarioId), any(HttpOperationScenario.class));
    }

    private static OpenApiSpecification createOpenApiSpecification(String version) {
        OpenApiSpecification openApiSpecification = OpenApiSpecification.from(new ClasspathResource(
            "swagger/petstore-" + version + ".json"));
        openApiSpecification.setRootContextPath("/api");
        return openApiSpecification;
    }

    @ParameterizedTest
    @ValueSource(strings={"v2", "v3"})
    void testGenerateScenariosWithBeanDefinitionRegistry(String version) {

        DefaultListableBeanFactory beanRegistryMock = mock();
        mockBeanFactory(beanRegistryMock);

        OpenApiSpecification openApiSpecification = createOpenApiSpecification(version);
        fixture = new HttpScenarioGenerator(openApiSpecification);

        String context = openApiSpecification.getFullContextPath();

        doAnswer(invocation -> {
            BeanDefinition scenario = (BeanDefinition) invocation.getArguments()[1];
            assertBeanDefinition(scenario, context+"/pet", "POST_/api/petstore/"+version+"/pet", "post", false);
            return null;
        }).when(beanRegistryMock).registerBeanDefinition(eq("POST_/api/petstore/"+version+"/pet"), any(BeanDefinition.class));

        doAnswer(invocation -> {
            BeanDefinition scenario = (BeanDefinition) invocation.getArguments()[1];
            assertBeanDefinition(scenario, context+"/pet/{petId}", "GET_/api/petstore/"+version+"/pet/{petId}", "get", false);
            return null;
        }).when(beanRegistryMock).registerBeanDefinition(eq("GET_/api/petstore/"+version+"/pet/{petId}"), any(BeanDefinition.class));

        doAnswer(invocation -> {
            BeanDefinition scenario = (BeanDefinition) invocation.getArguments()[1];
            assertBeanDefinition(scenario, context+"/pet/{petId}", "DELETE_/api/petstore/"+version+"/pet/{petId}", "delete", false);
            return null;
        }).when(beanRegistryMock).registerBeanDefinition(eq("DELETE_/api/petstore/"+version+"/pet/{petId}"), any(BeanDefinition.class));

        fixture.postProcessBeanFactory(beanRegistryMock);

        verify(beanRegistryMock).registerBeanDefinition(eq("POST_/api/petstore/"+version+"/pet"), any(BeanDefinition.class));
        verify(beanRegistryMock).registerBeanDefinition(eq("GET_/api/petstore/"+version+"/pet/{petId}"), any(BeanDefinition.class));
        verify(beanRegistryMock).registerBeanDefinition(eq("DELETE_/api/petstore/"+version+"/pet/{petId}"), any(BeanDefinition.class));
    }

    @ParameterizedTest
    @ValueSource(strings={"v2", "v3"})
    void testGenerateScenariosWithDataDictionariesAtRootContext(String version) {
        DefaultListableBeanFactory beanRegistryMock = mock();
        mockBeanFactory(beanRegistryMock);

        OpenApiSpecification openApiSpecification = createOpenApiSpecification(version);
        openApiSpecification.setRootContextPath("/services/rest2");

        fixture = new HttpScenarioGenerator(openApiSpecification);

        String addPetScenarioId = "POST_/services/rest2/petstore/"+version+"/pet";
        String getPetScenarioId = "GET_/services/rest2/petstore/"+version+"/pet/{petId}";
        String deletePetScenarioId = "DELETE_/services/rest2/petstore/"+version+"/pet/{petId}";

        String context = openApiSpecification.getFullContextPath();

        doReturn(true).when(beanRegistryMock).containsBeanDefinition("inboundJsonDataDictionary");
        doReturn(true).when(beanRegistryMock).containsBeanDefinition("outboundJsonDataDictionary");

        doAnswer(invocation -> {
            BeanDefinition scenario = (BeanDefinition) invocation.getArguments()[1];
            assertBeanDefinition(scenario, context+"/pet",  addPetScenarioId, "post", true);
            return null;
        }).when(beanRegistryMock).registerBeanDefinition(eq(addPetScenarioId), any(BeanDefinition.class));

        doAnswer(invocation -> {
            BeanDefinition scenario = (BeanDefinition) invocation.getArguments()[1];
            assertBeanDefinition(scenario, context+"/pet/{petId}", getPetScenarioId, "get", true);
            return null;
        }).when(beanRegistryMock).registerBeanDefinition(eq(getPetScenarioId), any(BeanDefinition.class));

        doAnswer(invocation -> {
            BeanDefinition scenario = (BeanDefinition) invocation.getArguments()[1];
            assertBeanDefinition(scenario, context+"/pet/{petId}",deletePetScenarioId,"delete", true);
            return null;
        }).when(beanRegistryMock).registerBeanDefinition(eq(deletePetScenarioId), any(BeanDefinition.class));

        fixture.postProcessBeanFactory(beanRegistryMock);

        verify(beanRegistryMock).registerBeanDefinition(eq(addPetScenarioId), any(BeanDefinition.class));
        verify(beanRegistryMock).registerBeanDefinition(eq(getPetScenarioId), any(BeanDefinition.class));
        verify(beanRegistryMock).registerBeanDefinition(eq(deletePetScenarioId), any(BeanDefinition.class));
    }

    private void mockBeanFactory(BeanFactory beanFactory) {
        doThrow(new BeansException("No such bean") {
        }).when(beanFactory).getBean(HttpResponseActionBuilderProvider.class);
    }

    private void assertBeanDefinition(BeanDefinition scenario, String path, String scenarioId, String method, boolean withDictionaries) {
        assertThat(getConstructorArgument(scenario, 0)).isEqualTo( path);
        assertThat(getConstructorArgument(scenario, 1)).isEqualTo( scenarioId);
        assertThat(getConstructorArgument(scenario, 2)).isInstanceOf(OpenApiSpecification.class);
        assertThat(getConstructorArgument(scenario, 3)).isInstanceOf(OasOperation.class);
        assertThat(((OasOperation)getConstructorArgument(scenario, 3)).getMethod()).isEqualTo(method);

        if (withDictionaries) {
            assertThat(scenario.getPropertyValues().get("inboundDataDictionary")).isNotNull();
            assertThat(scenario.getPropertyValues().get("outboundDataDictionary")).isNotNull();
        } else {
            assertThat(scenario.getPropertyValues().get("inboundDataDictionary")).isNull();
            assertThat(scenario.getPropertyValues().get("outboundDataDictionary")).isNull();
        }
    }

    private static Object getConstructorArgument(BeanDefinition scenario, int index) {
        ValueHolder argumentValue = scenario.getConstructorArgumentValues()
            .getArgumentValue(index, String.class);
        assertThat(argumentValue).isNotNull();
        return argumentValue.getValue();
    }

    private void assertScenarioProperties(HttpOperationScenario scenario, String path, String operationId, String method) {
        assertThat(scenario).extracting(HttpOperationScenario::getPath, HttpOperationScenario::getScenarioId).containsExactly(path, operationId);
        assertThat(scenario.getMethod()).isEqualTo(method);
        assertThat(scenario.getOperation()).isNotNull().extracting(OasOperation::getMethod).isEqualTo(method.toLowerCase());
    }
}
