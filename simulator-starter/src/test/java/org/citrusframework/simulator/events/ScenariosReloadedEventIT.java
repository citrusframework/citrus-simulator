package org.citrusframework.simulator.events;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Objects;
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
import org.springframework.data.domain.Pageable;

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
                scenarioResource.getScenarios(Pageable.unpaged()).getBody()
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
