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

package org.citrusframework.simulator.service;

import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.OpenFeatureAPI;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.simulator.common.FeatureFlagNotEnabledException;
import org.citrusframework.simulator.scenario.DefaultScenarioEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.citrusframework.simulator.config.OpenFeatureConfig.EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED;
import static org.citrusframework.simulator.service.TestClassLoader.loadTestClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
class ScenarioRegistrationServiceTest {

    @Mock
    private GenericApplicationContext applicationContextMock;

    @Mock
    private OpenFeatureAPI openFeatureAPIMock;

    @Mock
    private Client clientMock;

    @Mock
    private ScenarioLookupService scenarioLookupServiceMock;

    private ScenarioRegistrationService fixture;

    @BeforeEach
    void beforeEachSetup() {
        doReturn(clientMock).when(openFeatureAPIMock).getClient();
        fixture = new ScenarioRegistrationService(applicationContextMock, openFeatureAPIMock, scenarioLookupServiceMock);
    }

    @Nested
    class RegisterScenarioFromJavaSourceCode {

        @Test
        void loadScenarioWithEndpoint() throws IOException, FeatureFlagNotEnabledException {
            String scenarioName = "ScenarioWithEndpoint";
            var scenarioWithEndpoint = loadTestClass("org.citrusframework.simulator.service.ScenarioWithEndpoint");

            doReturn(true).when(clientMock).getBooleanValue(EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED, false);

            var simulatorScenario = fixture.registerScenarioFromJavaSourceCode(scenarioName, scenarioWithEndpoint);

            assertThat(simulatorScenario.getScenarioEndpoint())
                .isNotNull()
                .isInstanceOf(DefaultScenarioEndpoint.class);

            verify(applicationContextMock).registerBeanDefinition(eq(scenarioName), any(BeanDefinition.class));
            verify(scenarioLookupServiceMock).evictAndReloadScenarioCache();
        }

        @Test
        void loadScenarioWithoutEndpoint() throws IOException, FeatureFlagNotEnabledException {
            String scenarioName = "ScenarioWithoutEndpoint";
            var scenarioWithoutEndpoint = loadTestClass("org.citrusframework.simulator.service.ScenarioWithoutEndpoint");

            doReturn(true).when(clientMock).getBooleanValue(EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED, false);

            var simulatorScenario = fixture.registerScenarioFromJavaSourceCode(scenarioName, scenarioWithoutEndpoint);

            assertThat(simulatorScenario.getScenarioEndpoint())
                .isNull();

            verify(applicationContextMock).registerBeanDefinition(eq(scenarioName), any(BeanDefinition.class));
            verify(scenarioLookupServiceMock).evictAndReloadScenarioCache();
        }

        @Test
        void scenarioNameMustMatchClassName() throws IOException {
            var scenarioWithEndpoint = loadTestClass("org.citrusframework.simulator.service.ScenarioWithEndpoint");

            doReturn(true).when(clientMock).getBooleanValue(EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED, false);

            assertThatThrownBy(() -> fixture.registerScenarioFromJavaSourceCode("ScenarioWithoutEndpoint", scenarioWithEndpoint))
                .isInstanceOf(CitrusRuntimeException.class)
                .hasRootCauseInstanceOf(ClassNotFoundException.class)
                .hasMessageEndingWith("Class ScenarioWithoutEndpoint not compiled");
        }

        @Test
        void requiresBeanDefinitionRegistry() throws IOException {
            fixture = new ScenarioRegistrationService(mock(ApplicationContext.class), openFeatureAPIMock, scenarioLookupServiceMock);

            doReturn(true).when(clientMock).getBooleanValue(EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED, false);

            var scenarioWithoutEndpoint = loadTestClass("org.citrusframework.simulator.service.ScenarioWithoutEndpoint");

            assertThatThrownBy(() -> fixture.registerScenarioFromJavaSourceCode("ScenarioWithoutEndpoint", scenarioWithoutEndpoint))
                .isInstanceOf(CitrusRuntimeException.class)
                .hasRootCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageEndingWith("Cannot register simulation into bean registry, application context is not of type BeanDefinitionRegistry!");
        }

        @Test
        void throwsExceptionWhenFeatureFlagNotEnabled() throws IOException {
            var scenarioWithoutEndpoint = loadTestClass("org.citrusframework.simulator.service.ScenarioWithoutEndpoint");

            doReturn(false).when(clientMock).getBooleanValue(EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED, false);

            assertThatThrownBy(() -> fixture.registerScenarioFromJavaSourceCode("ScenarioWithoutEndpoint", scenarioWithoutEndpoint))
                .isInstanceOf(FeatureFlagNotEnabledException.class)
                .hasMessage("Feature flag 'org.citrusframework.simulator.scenario.loading_at_runtime_enabled' not enabeld!");
        }
    }
}
