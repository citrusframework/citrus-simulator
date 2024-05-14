package org.citrusframework.simulator.service.impl;

import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.TestParameter;
import org.citrusframework.simulator.model.TestResult;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.citrusframework.simulator.service.TestParameterService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.springframework.beans.factory.annotation.Autowired;

import static java.time.Instant.now;
import static org.assertj.core.api.Assertions.assertThat;

@Isolated
@IntegrationTest
class ScenarioExecutionServiceIT {

    @Autowired
    private TestParameterService testParameterService;

    @Autowired
    private ScenarioExecutionService fixture;

    @Nested
    class CompleteScenarioExecution {

        @Test
        void cascadesUpdateEvent() {
            var scenarioExecution = fixture.save(
                ScenarioExecution.builder()
                    .scenarioName("cascadesUpdateEvent")
                    .startDate(now())
                    .build()
            );

            var result = fixture.completeScenarioExecution(
                scenarioExecution.getExecutionId(),
                TestResult.builder()
                    .testName("cascadesUpdateEvent")
                    .className(getClass().getSimpleName())
                    .build()
                    .addTestParameter(
                        TestParameter.builder()
                            .key("key")
                            .value("value")
                            .build()));

            assertThat(result.getTestResult().getTestParameters())
                .allSatisfy(
                    p -> assertThat(p.getTestParameterId().testResultId)
                        .isNotNull()
                );
        }
    }
}
