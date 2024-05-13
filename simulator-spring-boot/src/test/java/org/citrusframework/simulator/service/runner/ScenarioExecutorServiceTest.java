package org.citrusframework.simulator.service.runner;

import org.citrusframework.Citrus;
import org.citrusframework.CitrusContext;
import org.citrusframework.DefaultTestCaseRunner;
import org.citrusframework.TestCaseRunner;
import org.citrusframework.context.TestContext;
import org.citrusframework.report.TestListeners;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.captor;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

abstract class ScenarioExecutorServiceTest {

    protected static ScenarioEndpoint scenarioEndpointMock;

    @Mock
    protected ApplicationContext applicationContextMock;

    @Mock
    protected Citrus citrusMock;

    @Mock
    protected ScenarioExecutionService scenarioExecutionServiceMock;

    protected final String scenarioName = "testScenario";
    protected final List<ScenarioParameter> parameters = List.of(
        ScenarioParameter.builder()
            .name("param1")
            .value("value1")
            .build(),
        ScenarioParameter.builder()
            .name("param2")
            .value("value2")
            .build()
    );

    protected static SimulatorScenario getSimulatorScenarioMock() {
        var simulatorScenarioMock = mock(SimulatorScenario.class);
        doReturn(scenarioEndpointMock).when(simulatorScenarioMock).getScenarioEndpoint();
        return simulatorScenarioMock;
    }

    protected static void verifyTestCaseRunnerHasBeenConfigured(SimulatorScenario simulatorScenarioMock) {
        ArgumentCaptor<TestCaseRunner> runnerArgumentCaptor = captor();
        verify(simulatorScenarioMock).setTestCaseRunner(runnerArgumentCaptor.capture());

        assertThat(runnerArgumentCaptor.getValue())
            .isInstanceOf(DefaultTestCaseRunner.class)
            .hasNoNullFieldsOrProperties();
    }

    protected void beforeEachSetup() {
        scenarioEndpointMock = mock(ScenarioEndpoint.class);
    }

    protected Long mockScenarioExecutionCreation() {
        Long executionId = 1L;
        var scenarioExecutionMock = mock(ScenarioExecution.class);
        doReturn(executionId).when(scenarioExecutionMock).getExecutionId();
        doReturn(scenarioExecutionMock).when(scenarioExecutionServiceMock).createAndSaveExecutionScenario(scenarioName, parameters);
        return executionId;
    }

    protected TestContext mockCitrusTestContext() {
        var citrusContextMock = mock(CitrusContext.class);
        doReturn(citrusContextMock).when(citrusMock).getCitrusContext();
        var testContextMock = mock(TestContext.class);
        doReturn(testContextMock).when(citrusContextMock).createTestContext();

        var testListenersMock = mock(TestListeners.class);
        lenient().doReturn(testListenersMock).when(testContextMock).getTestListeners();
        return testContextMock;
    }

    protected void verifyScenarioHasBeenRunWithScenarioRunner(SimulatorScenario simulatorScenarioMock) {
        ArgumentCaptor<ScenarioRunner> scenarioRunnerArgumentCaptor = captor();
        verify(simulatorScenarioMock).run(scenarioRunnerArgumentCaptor.capture());

        assertThat(scenarioRunnerArgumentCaptor.getValue())
            .hasNoNullFieldsOrProperties()
            .satisfies(
                r -> assertThat(r.getScenarioEndpoint()).isEqualTo(scenarioEndpointMock),
                r -> assertThat(r.getApplicationContext()).isEqualTo(applicationContextMock)
            );
    }
}
