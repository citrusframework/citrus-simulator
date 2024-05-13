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

package org.citrusframework.simulator.service.runner;

import jakarta.annotation.Nullable;
import org.citrusframework.TestCase;
import org.citrusframework.report.TestListeners;
import org.citrusframework.simulator.exception.SimulatorException;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.captor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DefaultScenarioExecutorServiceTest extends ScenarioExecutorServiceTest {


    private DefaultScenarioExecutorService fixture;

    @BeforeEach
    public void beforeEachSetup() {
        super.beforeEachSetup();

        fixture = new DefaultScenarioExecutorService(applicationContextMock, citrusMock, scenarioExecutionServiceMock);
    }

    @Test
    void isScenarioExecutorService() {
        assertThat(fixture)
            .isInstanceOf(ScenarioExecutorService.class);
    }

    @Test
    void runSimulatorScenarioByName() {
        var simulatorScenarioMock = getSimulatorScenarioMock();
        doReturn(simulatorScenarioMock).when(applicationContextMock).getBean(scenarioName, SimulatorScenario.class);

        Long executionId = mockScenarioExecutionCreation();

        mockCitrusTestContext();

        // Note that this does not actually "run" the scenario (because of the mocked executor service), it just creates it.
        Long result = fixture.run(scenarioName, parameters);
        assertEquals(executionId, result);

        verifyTestCaseRunnerHasBeenConfigured(simulatorScenarioMock);
        verifyScenarioHasBeenRunWithScenarioRunner(simulatorScenarioMock);
        verifyNoMoreInteractions(simulatorScenarioMock);
    }

    @Test
    void runScenarioDirectly() {
        Long executionId = mockScenarioExecutionCreation();

        var simulatorScenarioMock = getSimulatorScenarioMock();

        var testContextMock = mockCitrusTestContext();
        var testListenersMock = mock(TestListeners.class);
        doReturn(testListenersMock).when(testContextMock).getTestListeners();

        // Note that this does not actually "run" the scenario (because of the mocked executor service), it just creates it.
        Long result = fixture.run(simulatorScenarioMock, scenarioName, parameters);
        verifyScenarioExecution(executionId, result, simulatorScenarioMock, testListenersMock);

        verifyScenarioHasBeenRunWithScenarioRunner(simulatorScenarioMock);
    }

    @Test
    void exceptionDuringExecutionWillBeCaught() {
        Long executionId = mockScenarioExecutionCreation();

        var simulatorScenarioMock = getSimulatorScenarioMock();

        var testContextMock = mockCitrusTestContext();
        var testListenersMock = mock(TestListeners.class);
        doReturn(testListenersMock).when(testContextMock).getTestListeners();

        // Invoke exception
        var cause = mock(SimulatorException.class);
        doThrow(cause).when(simulatorScenarioMock).run(any(ScenarioRunner.class));

        assertThatThrownBy(() -> fixture.run(simulatorScenarioMock, scenarioName, parameters))
            .isEqualTo(cause);
        verifyScenarioExecution(executionId, null, simulatorScenarioMock, testListenersMock);

        verify(simulatorScenarioMock).registerException(cause);
    }

    private void verifyScenarioExecution(Long executionId, @Nullable Long result, SimulatorScenario simulatorScenario, TestListeners testListenersMock) {
        if (!isNull(result)) {
            assertEquals(executionId, result);
        }

        ArgumentCaptor<ScenarioRunner> scenarioRunnerArgumentCaptor = captor();
        verify(simulatorScenario, times(1)).run(scenarioRunnerArgumentCaptor.capture());

        var scenarioRunner = scenarioRunnerArgumentCaptor.getValue();
        assertEquals(scenarioEndpointMock, scenarioRunner.getScenarioEndpoint());
        assertEquals(executionId, scenarioRunner.getTestCaseRunner().getTestCase().getVariableDefinitions().get(ScenarioExecution.EXECUTION_ID));

        verify(testListenersMock).onTestStart(any(TestCase.class));
        verify(testListenersMock).onTestSuccess(any(TestCase.class));
        verify(testListenersMock).onTestFinish(any(TestCase.class));
        verifyNoMoreInteractions(testListenersMock);
    }
}
