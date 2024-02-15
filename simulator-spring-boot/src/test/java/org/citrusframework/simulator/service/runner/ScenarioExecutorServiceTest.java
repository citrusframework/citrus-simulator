package org.citrusframework.simulator.service.runner;

import org.citrusframework.Citrus;
import org.citrusframework.CitrusContext;
import org.citrusframework.context.TestContext;
import org.citrusframework.report.TestListeners;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

abstract class ScenarioExecutorServiceTest {

    protected static ScenarioEndpoint scenarioEndpointMock;

    private static AtomicBoolean customScenarioExecuted;

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

    protected void beforeEachSetup() {
        scenarioEndpointMock = mock(ScenarioEndpoint.class);
        customScenarioExecuted = new AtomicBoolean(false);
    }

    protected boolean isCustomScenarioExecuted() {
        return customScenarioExecuted.get();
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

    protected static class BaseCustomSimulatorScenario implements SimulatorScenario {

        @Override
        public ScenarioEndpoint getScenarioEndpoint() {
            return scenarioEndpointMock;
        }

        @Override
        public void run(ScenarioRunner runner) {
            Assertions.fail("This method should never be called");
        }
    }

    protected static class CustomSimulatorScenario extends BaseCustomSimulatorScenario {

        @Override
        public void run(ScenarioRunner runner) {
            customScenarioExecuted.set(true);
        }
    }
}
