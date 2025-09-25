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

import org.citrusframework.TestCase;
import org.citrusframework.context.TestContext;
import org.citrusframework.report.TestListeners;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.exception.SimulatorException;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.event.ContextClosedEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.citrusframework.simulator.service.ScenarioExecutorService.ExecutionRequestAndResponse.NOOP_EXECUTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.captor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class AsyncScenarioExecutorServiceTest extends ScenarioExecutorServiceTest {

    private static final int THREAD_POOL_SIZE = 1234;

    @Mock
    private ExecutorService executorServiceMock;

    @Mock
    private SimulatorConfigurationProperties propertiesMock;

    private AsyncScenarioExecutorService fixture;

    @BeforeEach
    public void beforeEachSetup() {
        super.beforeEachSetup();

        doReturn(THREAD_POOL_SIZE).when(propertiesMock).getExecutorThreads();

        fixture = new AsyncScenarioExecutorService(applicationContextMock, citrusMock, scenarioExecutionServiceMock, propertiesMock);
        setField(fixture, "executorService", executorServiceMock, ExecutorService.class);
    }

    @Test
    void isScenarioExecutorService() {
        assertThat(fixture)
            .isInstanceOf(ScenarioExecutorService.class)
            .isInstanceOf(DefaultScenarioExecutorService.class);
    }

    @Test
    void constructorCreatesThreadPool() {
        assertThat(new AsyncScenarioExecutorService(applicationContextMock, citrusMock, scenarioExecutionServiceMock, propertiesMock))
            .hasNoNullFieldsOrProperties()
            .extracting("executorService")
            .isInstanceOf(ThreadPoolExecutor.class)
            .hasFieldOrPropertyWithValue("corePoolSize", THREAD_POOL_SIZE)
            .hasFieldOrPropertyWithValue("maximumPoolSize", THREAD_POOL_SIZE);
    }

    @Test
    void runSimulatorScenarioByName() {
        var simulatorScenarioMock = getSimulatorScenarioMock();

        doReturn(simulatorScenarioMock).when(applicationContextMock).getBean(scenarioName, SimulatorScenario.class);

        Long executionId = mockScenarioExecutionCreation();

        // Note that this does not actually "run" the scenario (because of the mocked executor service), it just creates it.
        Long result = fixture.run(scenarioName, parameters, NOOP_EXECUTION);
        assertEquals(executionId, result);

        ArgumentCaptor<Runnable> scenarioRunnableArgumentCaptor = captor();
        verify(executorServiceMock).execute(scenarioRunnableArgumentCaptor.capture());

        // Now, we need more mocks!
        var testContextMock = mockCitrusTestContext();

        // This invokes the scenario execution with the captured runnable
        scenarioRunnableArgumentCaptor.getValue().run();

        verifyTestCaseRunnerHasBeenConfigured(simulatorScenarioMock);
        verifyScenarioHasBeenRunWithScenarioRunner(simulatorScenarioMock, testContextMock);
        verifyNoMoreInteractions(simulatorScenarioMock);
    }

    @Test
    void runScenarioDirectly() {
        Long executionId = mockScenarioExecutionCreation();

        var simulatorScenarioMock = getSimulatorScenarioMock();

        // Note that this does not actually "run" the scenario (because of the mocked executor service), it just creates it.
        Long result = fixture.run(simulatorScenarioMock, scenarioName, parameters, NOOP_EXECUTION);
        var testContextMock = verifyScenarioExecution(executionId, result, simulatorScenarioMock);

        verifyScenarioHasBeenRunWithScenarioRunner(simulatorScenarioMock, testContextMock);
    }

    @Test
    void exceptionDuringExecutionWillBeCaught() {
        Long executionId = mockScenarioExecutionCreation();

        var simulatorScenarioMock = getSimulatorScenarioMock();

        // Invoke exception
        var cause = mock(SimulatorException.class);
        doThrow(cause).when(simulatorScenarioMock).run(any(ScenarioRunner.class));

        // Note that this does not actually "run" the scenario (because of the mocked executor service), it just creates it.
        // This method-invocation may not throw, despite the above exception!
        Long result = fixture.run(simulatorScenarioMock, scenarioName, parameters, NOOP_EXECUTION);
        verifyScenarioExecution(executionId, result, simulatorScenarioMock);

        verify(simulatorScenarioMock).registerException(cause);
    }

    @Test
    void shutdownExecutorOnDestroy() throws Exception {
        fixture.destroy();
        verify(executorServiceMock).shutdownNow();
    }

    @Test
    void shutdownExecutorOnApplicationContextEvent() {
        fixture.onApplicationEvent(new ContextClosedEvent(applicationContextMock));
        verify(executorServiceMock).shutdownNow();
    }

    private TestContext verifyScenarioExecution(Long executionId, Long result, SimulatorScenario simulatorScenario) {
        assertEquals(executionId, result);

        ArgumentCaptor<Runnable> scenarioRunnableArgumentCaptor = captor();
        verify(executorServiceMock).execute(scenarioRunnableArgumentCaptor.capture());

        // Now, we need more mocks!
        var testContextMock = mockCitrusTestContext();
        var testListenersMock = mock(TestListeners.class);
        doReturn(testListenersMock).when(testContextMock).getTestListeners();

        // This invokes the scenario execution with the captured runnable
        scenarioRunnableArgumentCaptor.getValue().run();

        ArgumentCaptor<ScenarioRunner> scenarioRunnerArgumentCaptor = captor();
        verify(simulatorScenario, times(1)).run(scenarioRunnerArgumentCaptor.capture());

        var scenarioRunner = scenarioRunnerArgumentCaptor.getValue();
        assertEquals(scenarioEndpointMock, scenarioRunner.getScenarioEndpoint());
        assertEquals(executionId, scenarioRunner.getTestCaseRunner().getTestCase().getVariableDefinitions().get(ScenarioExecution.EXECUTION_ID));

        verify(testListenersMock).onTestStart(any(TestCase.class));
        verify(testListenersMock).onTestExecutionEnd(any(TestCase.class));
        verify(testListenersMock).onTestSuccess(any(TestCase.class));
        verify(testListenersMock).onTestFinalization(any(TestCase.class));
        verifyNoMoreInteractions(testListenersMock);

        return testContextMock;
    }
}
