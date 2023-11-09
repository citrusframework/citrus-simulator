package org.citrusframework.simulator.service.impl;

import org.citrusframework.Citrus;
import org.citrusframework.CitrusContext;
import org.citrusframework.TestCase;
import org.citrusframework.context.TestContext;
import org.citrusframework.report.TestListeners;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DefaultScenarioExecutorServiceImplTest {

    private static ScenarioEndpoint scenarioEndpointMock;
    private static AtomicBoolean customScenarioExecuted;

    @Mock
    private ApplicationContext applicationContextMock;

    @Mock
    private Citrus citrusMock;

    @Mock
    private ScenarioExecutionService scenarioExecutionServiceMock;

    @Mock
    private SimulatorConfigurationProperties propertiesMock;

    @Mock
    private ExecutorService executorServiceMock;

    private DefaultScenarioExecutorServiceImpl fixture;

    private String scenarioName = "testScenario";
    private List<ScenarioParameter> parameters = List.of(
        ScenarioParameter.builder()
            .name("param1")
            .value("value1")
            .build(),
        ScenarioParameter.builder()
            .name("param2")
            .value("value2")
            .build()
    );

    @BeforeEach
    public void beforeEachSetup() {
        doReturn(1).when(propertiesMock).getExecutorThreads();

        fixture = new DefaultScenarioExecutorServiceImpl(applicationContextMock, citrusMock, scenarioExecutionServiceMock, propertiesMock);
        ReflectionTestUtils.setField(fixture, "executorService", executorServiceMock, ExecutorService.class);

        scenarioEndpointMock = mock(ScenarioEndpoint.class);
        customScenarioExecuted = new AtomicBoolean(false);
    }

    @Test
    void runSimulatorScenarioByName() {
        SimulatorScenario simulatorScenarioMock = mock(SimulatorScenario.class);
        doReturn(simulatorScenarioMock).when(applicationContextMock).getBean(scenarioName, SimulatorScenario.class);

        Long executionId = mockScenarioExecutionCreation();

        // Note that this does not actually "run" the scenario (because of the mocked executor service), it just creates it
        Long result = fixture.run(scenarioName, parameters);
        assertEquals(executionId, result);

        ArgumentCaptor<Runnable> scenarioRunnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(executorServiceMock).submit(scenarioRunnableArgumentCaptor.capture());

        // Now, we need more mocks!
        mockCitrusTestContext();

        // This invokes the scenario execution with the captured runnable
        scenarioRunnableArgumentCaptor.getValue().run();

        verifyNoInteractions(simulatorScenarioMock);
    }

    @Test
    void runScenarioDirectly() {
        Long executionId = mockScenarioExecutionCreation();

        SimulatorScenario simulatorScenario = spy(new CustomSimulatorScenario());

        // Note that this does not actually "run" the scenario (because of the mocked executor service), it just creates it
        Long result = fixture.run(simulatorScenario, scenarioName, parameters);
        assertEquals(executionId, result);

        ArgumentCaptor<Runnable> scenarioRunnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(executorServiceMock).submit(scenarioRunnableArgumentCaptor.capture());

        // Now, we need more mocks!
        TestContext testContextMock = mockCitrusTestContext();
        TestListeners testListenersMock = mock(TestListeners.class);
        doReturn(testListenersMock).when(testContextMock).getTestListeners();

        // This invokes the scenario execution with the captured runnable
        scenarioRunnableArgumentCaptor.getValue().run();

        ArgumentCaptor<ScenarioRunner> scenarioRunnerArgumentCaptor = ArgumentCaptor.forClass(ScenarioRunner.class);
        verify(simulatorScenario).run(scenarioRunnerArgumentCaptor.capture());

        ScenarioRunner scenarioRunner = scenarioRunnerArgumentCaptor.getValue();
        assertEquals(scenarioEndpointMock, scenarioRunner.scenarioEndpoint());
        assertEquals(executionId, scenarioRunner.getTestCaseRunner().getTestCase().getVariableDefinitions().get(ScenarioExecution.EXECUTION_ID));

        verify(testListenersMock).onTestStart(any(TestCase.class));
        verify(testListenersMock).onTestSuccess(any(TestCase.class));
        verify(testListenersMock).onTestFinish(any(TestCase.class));
        verifyNoMoreInteractions(testListenersMock);

        assertTrue(customScenarioExecuted.get());
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

    private Long mockScenarioExecutionCreation() {
        Long executionId = 1L;
        ScenarioExecution scenarioExecutionMock = mock(ScenarioExecution.class);
        doReturn(executionId).when(scenarioExecutionMock).getExecutionId();
        doReturn(scenarioExecutionMock).when(scenarioExecutionServiceMock).createAndSaveExecutionScenario(scenarioName, parameters);
        return executionId;
    }

    private TestContext mockCitrusTestContext() {
        CitrusContext citrusContextMock = mock(CitrusContext.class);
        doReturn(citrusContextMock).when(citrusMock).getCitrusContext();
        TestContext testContextMock = mock(TestContext.class);
        doReturn(testContextMock).when(citrusContextMock).createTestContext();
        return testContextMock;
    }

    public static class CustomSimulatorScenario implements SimulatorScenario {

        @Override
        public ScenarioEndpoint getScenarioEndpoint() {
            return scenarioEndpointMock;
        }

        @Override
        public void run(ScenarioRunner runner) {
            customScenarioExecuted.set(true);
        }
    }
}
