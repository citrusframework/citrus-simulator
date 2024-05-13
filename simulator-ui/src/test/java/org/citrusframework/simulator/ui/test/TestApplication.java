package org.citrusframework.simulator.ui.test;

import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Note, this may not rest in the root package of the {@code simulator-ui} because of the automatic package scan
 * which {@link SpringBootApplication} performs. Hence, the {@code test} package.
 */
@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean("DEFAULT_SCENARIO")
    public SimulatorScenario defaultScenario() {
        return new AbstractSimulatorScenario() {
        };
    }
}
