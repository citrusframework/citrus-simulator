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

package org.citrusframework.simulator.config;

import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.OpenFeatureAPI;
import dev.openfeature.sdk.providers.memory.InMemoryProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.citrusframework.simulator.config.OpenFeatureConfig.EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith({MockitoExtension.class})
class OpenFeatureConfigTest {

    @Mock
    private ApplicationContext applicationContextMock;

    @Mock
    private OpenFeatureConfig.FeatureProviderFactory featureProviderFactoryMock;

    @Mock
    private FeatureProvider customFeatureProviderMock;

    private OpenFeatureConfig openFeatureConfig;
    private OpenFeatureAPI originalInstance;

    @BeforeEach
    void setUp() {
        openFeatureConfig = new OpenFeatureConfig();
        // Store the original instance to restore it after tests
        originalInstance = OpenFeatureAPI.getInstance();
    }

    @AfterEach
    void tearDown() {
        // Reset the OpenFeatureAPI singleton to its original state
        OpenFeatureAPI.getInstance().setProvider(originalInstance.getProvider());
    }

    @Test
    void shouldUseCustomFeatureProviderWhenFactoryExists() {
        doReturn(featureProviderFactoryMock)
            .when(applicationContextMock)
            .getBean(OpenFeatureConfig.FeatureProviderFactory.class);
        doReturn(customFeatureProviderMock)
            .when(featureProviderFactoryMock)
            .getFeatureProvider();

        OpenFeatureAPI result = openFeatureConfig.openFeatureAPI(applicationContextMock);

        assertThat(result.getProvider())
            .isSameAs(customFeatureProviderMock);
    }

    @Test
    void shouldUseInMemoryProviderWhenNoFactoryExists() {
        doThrow(new NoSuchBeanDefinitionException("FeatureProviderFactory"))
            .when(applicationContextMock)
            .getBean(OpenFeatureConfig.FeatureProviderFactory.class);

        OpenFeatureAPI result = openFeatureConfig.openFeatureAPI(applicationContextMock);

        assertThat(result.getProvider())
            .isInstanceOf(InMemoryProvider.class);
    }

    @Test
    void shouldConfigureDefaultFlagCorrectlyWithInMemoryProvider() {
        doThrow(new NoSuchBeanDefinitionException("FeatureProviderFactory"))
            .when(applicationContextMock)
            .getBean(OpenFeatureConfig.FeatureProviderFactory.class);

        OpenFeatureAPI result = openFeatureConfig.openFeatureAPI(applicationContextMock);
        InMemoryProvider provider = (InMemoryProvider) result.getProvider();

        // Verify the flag exists and has correct configuration
        assertThat(provider.getBooleanEvaluation(EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED, null, null).getValue())
            .isFalse(); // Default should be "off" (false)
    }

    @Test
    void shouldReturnSingletonInstance() {
        doThrow(new NoSuchBeanDefinitionException("FeatureProviderFactory"))
            .when(applicationContextMock)
            .getBean(OpenFeatureConfig.FeatureProviderFactory.class);

        OpenFeatureAPI result1 = openFeatureConfig.openFeatureAPI(applicationContextMock);
        OpenFeatureAPI result2 = openFeatureConfig.openFeatureAPI(applicationContextMock);

        assertThat(result1)
            .isSameAs(result2)
            .isSameAs(OpenFeatureAPI.getInstance());
    }
}
