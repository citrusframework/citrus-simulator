package org.citrusframework.simulator.service.runner;

import org.citrusframework.TestCase;
import org.citrusframework.report.TestListeners;
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
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DefaultScenarioExecutorServiceTest extends ScenarioExecutorServiceTest {

    @Mock
    private ApplicationContext applicationContextMock;

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
        var simulatorScenarioMock = mock(SimulatorScenario.class);
        doReturn(simulatorScenarioMock).when(applicationContextMock).getBean(scenarioName, SimulatorScenario.class);

        Long executionId = mockScenarioExecutionCreation();

        mockCitrusTestContext();

        // Note that this does not actually "run" the scenario (because of the mocked executor service), it just creates it.
        Long result = fixture.run(scenarioName, parameters);
        assertEquals(executionId, result);

        verify(simulatorScenarioMock).getScenarioEndpoint();
        verify(simulatorScenarioMock).run(any(ScenarioRunner.class));
        verifyNoMoreInteractions(simulatorScenarioMock);
    }

    @Test
    void runScenarioDirectly() {
        Long executionId = mockScenarioExecutionCreation();

        var simulatorScenario = spy(new CustomSimulatorScenario());

        var testContextMock = mockCitrusTestContext();
        var testListenersMock = mock(TestListeners.class);
        doReturn(testListenersMock).when(testContextMock).getTestListeners();

        // Note that this does not actually "run" the scenario (because of the mocked executor service), it just creates it.
        Long result = fixture.run(simulatorScenario, scenarioName, parameters);
        verifyScenarioExecution(executionId, result, simulatorScenario, testListenersMock);

        assertTrue(isCustomScenarioExecuted());
    }

    @Test
    void exceptionDuringExecutionWillBeCatched() {
        Long executionId = mockScenarioExecutionCreation();

        var simulatorScenario = spy(new CustomSimulatorScenario());

        var testContextMock = mockCitrusTestContext();
        var testListenersMock = mock(TestListeners.class);
        doReturn(testListenersMock).when(testContextMock).getTestListeners();

        // Invoke exception
        doThrow(new IllegalArgumentException()).when(simulatorScenario).run(any(ScenarioRunner.class));

        // Note that this does not actually "run" the scenario (because of the mocked executor service), it just creates it.
        // This method-invocation may not throw, despite the above exception!
        Long result = fixture.run(simulatorScenario, scenarioName, parameters);
        verifyScenarioExecution(executionId, result, simulatorScenario, testListenersMock);

        assertFalse(isCustomScenarioExecuted());
    }

    private void verifyScenarioExecution(Long executionId, Long result, CustomSimulatorScenario simulatorScenario, TestListeners testListenersMock) {
        assertEquals(executionId, result);

        ArgumentCaptor<ScenarioRunner> scenarioRunnerArgumentCaptor = ArgumentCaptor.forClass(ScenarioRunner.class);
        verify(simulatorScenario, times(1)).run(scenarioRunnerArgumentCaptor.capture());

        var scenarioRunner = scenarioRunnerArgumentCaptor.getValue();
        assertEquals(scenarioEndpointMock, scenarioRunner.scenarioEndpoint());
        assertEquals(executionId, scenarioRunner.getTestCaseRunner().getTestCase().getVariableDefinitions().get(ScenarioExecution.EXECUTION_ID));

        verify(testListenersMock).onTestStart(any(TestCase.class));
        verify(testListenersMock).onTestSuccess(any(TestCase.class));
        verify(testListenersMock).onTestFinish(any(TestCase.class));
        verifyNoMoreInteractions(testListenersMock);
    }
}
