package org.citrusframework.simulator.scenario;

import jakarta.annotation.Nullable;
import org.apache.commons.lang3.NotImplementedException;
import org.citrusframework.DefaultTestCaseRunner;
import org.citrusframework.TestCaseRunner;
import org.citrusframework.context.TestContext;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.simulator.exception.SimulatorException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.captor;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith({MockitoExtension.class})
class SimulatorScenarioTest {

    @Mock
    private ScenarioEndpoint scenarioEndpointMock;

    @Nested
    class RegisterException {

        @Test
        void withoutRegisteredTestCaseRunner() {
            var fixture = new TestSimulatorScenario(null);

            var cause = mock(SimulatorException.class);

            fixture.registerException(cause);

            verify(scenarioEndpointMock).fail(cause);
        }

        @Test
        void withRandomTestCaseRunnerImplementation() {
            var testCaseRunnerMock = mock(TestCaseRunner.class);

            var fixture = new TestSimulatorScenario(testCaseRunnerMock);

            var cause = mock(SimulatorException.class);

            fixture.registerException(cause);

            verify(scenarioEndpointMock).fail(cause);
            verifyNoInteractions(testCaseRunnerMock);
        }

        @Test
        void withDefaultTestCaseRunner() {
            var testCaseRunnerMock = mock(DefaultTestCaseRunner.class);
            var testContextMock = mock(TestContext.class);
            doReturn(testContextMock).when(testCaseRunnerMock).getContext();

            var fixture = new TestSimulatorScenario(testCaseRunnerMock);

            var cause = mock(CitrusRuntimeException.class);

            fixture.registerException(cause);

            verify(scenarioEndpointMock).fail(cause);

            ArgumentCaptor<CitrusRuntimeException> exceptionArgumentCaptor = captor();
            verify(testContextMock).addException(exceptionArgumentCaptor.capture());

            assertThat(exceptionArgumentCaptor.getValue())
                .cause()
                .isEqualTo(cause);
        }
    }

    private class TestSimulatorScenario implements SimulatorScenario {

        private @Nullable
        final TestCaseRunner testCaseRunner;

        private TestSimulatorScenario(@Nullable TestCaseRunner testCaseRunner) {
            this.testCaseRunner = testCaseRunner;
        }

        @Override
        public ScenarioEndpoint getScenarioEndpoint() {
            return scenarioEndpointMock;
        }

        @Nullable
        @Override
        public TestCaseRunner getTestCaseRunner() {
            return testCaseRunner;
        }

        @Override
        public void setTestCaseRunner(TestCaseRunner testCaseRunner) {
            throw new NotImplementedException("Not implemented for this testcase!");
        }
    }
}
