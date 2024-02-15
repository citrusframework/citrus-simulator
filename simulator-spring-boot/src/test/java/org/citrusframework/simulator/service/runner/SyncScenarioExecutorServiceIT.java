package org.citrusframework.simulator.service.runner;

import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@TestPropertySource(properties={"citrus.simulator.mode=sync"})
class SyncScenarioExecutorServiceIT {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void isDefaultScenarioExecutorService() {
        assertThat(applicationContext.getBean(ScenarioExecutorService.class))
            .isInstanceOf(DefaultScenarioExecutorService.class)
            .isNotInstanceOf(AsyncScenarioExecutorService.class);
    }
}
