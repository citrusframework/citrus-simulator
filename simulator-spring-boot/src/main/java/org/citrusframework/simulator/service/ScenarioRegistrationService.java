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

import dev.openfeature.sdk.OpenFeatureAPI;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.simulator.common.FeatureFlagNotEnabledException;
import org.citrusframework.simulator.scenario.DefaultScenarioEndpoint;
import org.citrusframework.simulator.scenario.ScenarioEndpointConfiguration;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.scenario.SimulatorScenarioWithEndpoint;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import static org.citrusframework.simulator.config.OpenFeatureConfig.EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED;
import static org.citrusframework.simulator.scenario.DynamicClassLoader.compileAndLoad;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

@Service
public class ScenarioRegistrationService {

    private final ApplicationContext applicationContext;
    private final OpenFeatureAPI openFeatureAPI;
    private final ScenarioLookupService scenarioLookupService;

    public ScenarioRegistrationService(ApplicationContext applicationContext, OpenFeatureAPI openFeatureAPI, ScenarioLookupService scenarioLookupService) {
        this.applicationContext = applicationContext;
        this.openFeatureAPI = openFeatureAPI;
        this.scenarioLookupService = scenarioLookupService;
    }

    public SimulatorScenario registerScenarioFromJavaSourceCode(String scenarioName, String javaSourceCode) throws FeatureFlagNotEnabledException {
        if (!openFeatureAPI.getClient().getBooleanValue(EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED, false)) {
            throw new FeatureFlagNotEnabledException(EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED);
        }

        try {
            Class<SimulatorScenario> loadedClass = compileAndLoad(scenarioName, javaSourceCode);
            SimulatorScenario simulatorScenario = loadedClass.getDeclaredConstructor().newInstance();

            if (simulatorScenario instanceof SimulatorScenarioWithEndpoint simulatorScenarioWithEndpoint) {
                simulatorScenarioWithEndpoint.setScenarioEndpoint(new DefaultScenarioEndpoint(new ScenarioEndpointConfiguration()));
            }

            registerScenarioBean(scenarioName, loadedClass);

            scenarioLookupService.evictAndReloadScenarioCache();

            return simulatorScenario;
        } catch (Exception e) {
            throw new CitrusRuntimeException(e);
        }
    }

    private void registerScenarioBean(String scenarioName, Class<SimulatorScenario> loadedClass) {
        if (!(applicationContext instanceof BeanDefinitionRegistry beanDefinitionRegistry)) {
            throw new IllegalArgumentException("Cannot register simulation into bean registry, application context is not of type BeanDefinitionRegistry!");
        }

        var beanDefinition = genericBeanDefinition(loadedClass).getBeanDefinition();
        beanDefinitionRegistry.registerBeanDefinition(scenarioName, beanDefinition);
    }
}
