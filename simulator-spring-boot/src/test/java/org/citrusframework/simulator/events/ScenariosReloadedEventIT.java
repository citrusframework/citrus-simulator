/*
 * Copyright 2024 the original author or authors.
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

package org.citrusframework.simulator.events;

import org.apache.commons.lang3.NotImplementedException;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.service.ScenarioLookupService;
import org.citrusframework.simulator.web.rest.ScenarioResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.data.domain.Pageable.unpaged;

@IntegrationTest
class ScenariosReloadedEventIT {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ScenarioLookupService scenarioLookupService;

    @Autowired
    private ScenarioResource scenarioResource;

    @Test
    void publishEvent() {
        int scenariosBeforeReload = countScenarioResourceScenarios();

        SimulatorScenario simulatorScenario = new ScenariosReloadedEventITScenario();

        ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        beanFactory.registerSingleton(simulatorScenario.getClass().getSimpleName(), simulatorScenario);

        scenarioLookupService.evictAndReloadScenarioCache();

        int scenariosAfterReload = countScenarioResourceScenarios();

        assertEquals(scenariosBeforeReload + 1, scenariosAfterReload,
            "evictAndReloadScenarioCache should detect and publish the new Scenario");
    }

    private int countScenarioResourceScenarios() {
        return Objects.requireNonNull(
                scenarioResource.getScenarios(Optional.empty(), unpaged()).getBody()
            )
            .size();
    }

    private static final class ScenariosReloadedEventITScenario implements SimulatorScenario {

        @Override
        public ScenarioEndpoint getScenarioEndpoint() {
            throw new NotImplementedException();
        }
    }
}
