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
import dev.openfeature.sdk.providers.memory.Flag;
import dev.openfeature.sdk.providers.memory.InMemoryProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Slf4j
@Configuration
public class OpenFeatureConfig {

    public static final String EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED = "org.citrusframework.simulator.scenario.loading_at_runtime_enabled";

    @Bean
    public OpenFeatureAPI openFeatureAPI(ApplicationContext applicationContext) {
        OpenFeatureAPI openFeatureAPI = OpenFeatureAPI.getInstance();
        openFeatureAPI.setProviderAndWait(getFeatureProviderOrDefault(applicationContext));
        return openFeatureAPI;
    }

    private FeatureProvider getFeatureProviderOrDefault(ApplicationContext applicationContext) {
        try {
            return applicationContext.getBean(FeatureProviderFactory.class).getFeatureProvider();
        } catch (NoSuchBeanDefinitionException e) {
            logger.warn("No feature flag provider configured, using default settings!");
        }

        return new InMemoryProvider(Map.of(
            EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED, Flag.builder()
                .variant("on", true)
                .variant("off", false)
                .defaultVariant("off")
                .build()
        ));
    }

    public interface FeatureProviderFactory {

        FeatureProvider getFeatureProvider();
    }
}
