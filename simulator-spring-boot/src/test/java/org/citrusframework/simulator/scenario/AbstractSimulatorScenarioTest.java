package org.citrusframework.simulator.scenario;

import org.citrusframework.TestCaseRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
class AbstractSimulatorScenarioTest {

    @Mock
    private TestCaseRunner testCaseRunnerMock;

    @Test
    void isTestCaseRunnerAware() {
        var fixture = new AbstractSimulatorScenario() {
        };

        fixture.setTestCaseRunner(testCaseRunnerMock);

        assertThat(fixture.getTestCaseRunner())
            .isEqualTo(testCaseRunnerMock);
    }
}
