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

            verifyNoInteractions(scenarioEndpointMock);
        }

        @Test
        void withRandomTestCaseRunnerImplementation() {
            var testCaseRunnerMock = mock(TestCaseRunner.class);

            var fixture = new TestSimulatorScenario(testCaseRunnerMock);

            var cause = mock(SimulatorException.class);

            fixture.registerException(cause);

            verifyNoInteractions(scenarioEndpointMock, testCaseRunnerMock);
        }

        @Test
        void withDefaultTestCaseRunner() {
            var testCaseRunnerMock = mock(DefaultTestCaseRunner.class);
            var testContextMock = mock(TestContext.class);
            doReturn(testContextMock).when(testCaseRunnerMock).getContext();

            var fixture = new TestSimulatorScenario(testCaseRunnerMock);

            var cause = mock(CitrusRuntimeException.class);

            fixture.registerException(cause);

            verify(scenarioEndpointMock).fail(cause, testContextMock);

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
