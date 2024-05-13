package org.citrusframework.simulator.service.runner;

import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.TestResult;
import org.citrusframework.simulator.repository.ScenarioExecutionRepository;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.citrusframework.simulator.model.TestResult.Status.SUCCESS;

@IntegrationTest
class DefaultScenarioExecutorServiceIT {

    private static final String SCENARIO_NAME = "DefaultScenarioExecutorServiceIT";

    @Autowired
    private ScenarioExecutorService scenarioExecutorService;

    @Autowired
    private ScenarioExecutionRepository scenarioExecutionRepository;

    @Test
    void isDefaultScenarioExecutorService() {
        assertThat(scenarioExecutorService)
            .isInstanceOf(DefaultScenarioExecutorService.class)
            .isNotInstanceOf(AsyncScenarioExecutorService.class);
    }

    @Test
    void throwsExceptionGivenInexistendScenarioName() {
        var beanName = "sherlock";

        assertThatThrownBy(() -> scenarioExecutorService.run(beanName, emptyList()))
            .isInstanceOf(NoSuchBeanDefinitionException.class)
            .hasMessage("No bean named '%s' available".formatted(beanName));
    }

    @Test
    void resultsBeingPersistedSynchronously() {
        var executionId = scenarioExecutorService.run(SCENARIO_NAME, emptyList());

        assertThat(scenarioExecutionRepository.findOneByExecutionId(executionId))
            .hasValueSatisfying(scenarioExecution -> assertThat(scenarioExecution)
                .hasNoNullFieldsOrPropertiesExcept("errorMessage")
                .extracting(ScenarioExecution::getTestResult)
                .extracting(TestResult::getStatus)
                .isEqualTo(SUCCESS));
    }

    @Scenario(SCENARIO_NAME)
    private static class TestSimulatorScenario extends AbstractSimulatorScenario {
    }
}
