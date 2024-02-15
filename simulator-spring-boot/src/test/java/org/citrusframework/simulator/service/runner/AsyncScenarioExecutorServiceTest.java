package org.citrusframework.simulator.service.runner;

import org.citrusframework.TestCase;
import org.citrusframework.report.TestListeners;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
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
import org.springframework.context.event.ContextClosedEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.captor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class AsyncScenarioExecutorServiceTest extends ScenarioExecutorServiceTest {

    private static final int THREAD_POOL_SIZE = 1234;

    @Mock
    private ApplicationContext applicationContextMock;

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
        var simulatorScenarioMock = mock(SimulatorScenario.class);
        doReturn(simulatorScenarioMock).when(applicationContextMock).getBean(scenarioName, SimulatorScenario.class);

        Long executionId = mockScenarioExecutionCreation();

        // Note that this does not actually "run" the scenario (because of the mocked executor service), it just creates it.
        Long result = fixture.run(scenarioName, parameters);
        assertEquals(executionId, result);

        ArgumentCaptor<Runnable> scenarioRunnableArgumentCaptor = captor();
        verify(executorServiceMock).submit(scenarioRunnableArgumentCaptor.capture());

        // Now, we need more mocks!
        mockCitrusTestContext();

        // This invokes the scenario execution with the captured runnable
        scenarioRunnableArgumentCaptor.getValue().run();

        verify(simulatorScenarioMock).getScenarioEndpoint();
        verify(simulatorScenarioMock).run(any(ScenarioRunner.class));
        verifyNoMoreInteractions(simulatorScenarioMock);
    }

    @Test
    void runScenarioDirectly() {
        Long executionId = mockScenarioExecutionCreation();

        var simulatorScenario = spy(new CustomSimulatorScenario());

        // Note that this does not actually "run" the scenario (because of the mocked executor service), it just creates it.
        Long result = fixture.run(simulatorScenario, scenarioName, parameters);
        verifyScenarioExecution(executionId, result, simulatorScenario);

        assertTrue(isCustomScenarioExecuted());
    }

    @Test
    void exceptionDuringExecutionWillBeCatched() {
        Long executionId = mockScenarioExecutionCreation();

        var simulatorScenario = spy(new CustomSimulatorScenario());

        // Invoke exception
        doThrow(new IllegalArgumentException()).when(simulatorScenario).run(any(ScenarioRunner.class));

        // Note that this does not actually "run" the scenario (because of the mocked executor service), it just creates it.
        // This method-invocation may not throw, despite the above exception!
        Long result = fixture.run(simulatorScenario, scenarioName, parameters);
        verifyScenarioExecution(executionId, result, simulatorScenario);

        assertFalse(isCustomScenarioExecuted());
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

    private void verifyScenarioExecution(Long executionId, Long result, AsyncScenarioExecutorServiceTest.CustomSimulatorScenario simulatorScenario) {
        assertEquals(executionId, result);

        ArgumentCaptor<Runnable> scenarioRunnableArgumentCaptor = captor();
        verify(executorServiceMock).submit(scenarioRunnableArgumentCaptor.capture());

        // Now, we need more mocks!
        var testContextMock = mockCitrusTestContext();
        var testListenersMock = mock(TestListeners.class);
        doReturn(testListenersMock).when(testContextMock).getTestListeners();

        // This invokes the scenario execution with the captured runnable
        scenarioRunnableArgumentCaptor.getValue().run();

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
