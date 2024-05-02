package org.citrusframework.simulator.listener;

import org.assertj.core.api.Condition;
import org.citrusframework.DefaultTestCase;
import org.citrusframework.TestAction;
import org.citrusframework.TestCase;
import org.citrusframework.TestResult;
import org.citrusframework.actions.SleepAction;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.simulator.service.ScenarioActionService;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.citrusframework.simulator.model.ScenarioExecution.EXECUTION_ID;
import static org.citrusframework.simulator.model.TestResult.Status.FAILURE;
import static org.citrusframework.simulator.model.TestResult.Status.SUCCESS;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith({MockitoExtension.class})
class SimulatorStatusListenerTest {

    @Mock
    private ScenarioActionService scenarioActionServiceMock;

    @Mock
    private ScenarioExecutionService scenarioExecutionServiceMock;

    private SimulatorStatusListener fixture;

    @BeforeEach
    void setup() {
        fixture = new SimulatorStatusListener(scenarioActionServiceMock, scenarioExecutionServiceMock);
    }

    @Nested
    class OnTestStart {

        @Test
        void shouldConstructKeyFromDefaultTestCase() {
            // Arrange
            var testName = "shouldConstructKeyFromDefaultTestCase";

            var defaultTestCaseMock = mock(DefaultTestCase.class);
            doReturn(testName).when(defaultTestCaseMock).getName();
            doReturn(getClass()).when(defaultTestCaseMock).getTestClass();
            doReturn(Map.of("key", "value")).when(defaultTestCaseMock).getParameters();

            // Act
            fixture.onTestStart(defaultTestCaseMock);

            // Assert
            assertThatRunningTestHasBeenRegistered(testName, "key=value", r -> assertThat(r.getParameters()).hasSize(1).containsOnlyKeys("key"));
        }

        @Test
        void cannotConstructKeyFromOtherTestCaseImplementation() {
            // Arrange
            var testName = "cannotConstructKeyFromOtherTestCaseImplementation";

            var testCaseMock = mock(TestCase.class);
            doReturn(testName).when(testCaseMock).getName();
            doReturn(getClass()).when(testCaseMock).getTestClass();

            // Act
            fixture.onTestStart(testCaseMock);

            // Assert
            assertThatRunningTestHasBeenRegistered(testName, "", r -> assertThat(r.getParameters()).isEmpty());
        }

        private void assertThatRunningTestHasBeenRegistered(String testName, String key, Consumer<TestResult> parameterVerifier) {
            assertThat(getRunningTests())
                .hasSize(1)
                .hasKeySatisfying(new Condition<>("equals constructed key") {
                    @Override
                    public boolean matches(String value) {
                        return value.equals(key);
                    }
                })
                .extracting(runningTests -> runningTests.get(key))
                .satisfies(
                    t -> assertThat(t.getTestName()).isEqualTo(testName),
                    t -> assertThat(t.isSuccess()).isTrue(),
                    parameterVerifier::accept
                );
        }
    }

    @Nested
    class OnTestFinish {

        @Test
        void shouldRemoveEntryFromRunningTests() {
            // Arrange
            var defaultTestCaseMock = mock(DefaultTestCase.class);
            doReturn(Map.of("key", "value")).when(defaultTestCaseMock).getParameters();

            var runningTests = new HashMap<String, TestResult>();
            var key = "key=value";
            runningTests.put(key, mock(TestResult.class));

            setField(fixture, "runningTests", runningTests, Map.class);

            // Act
            fixture.onTestFinish(defaultTestCaseMock);

            // Assert
            assertThat(runningTests)
                .isEmpty();
        }
    }

    @Nested
    class OnTestSuccess {

        private static long createAndGetScenarioExecutionId(TestCase testCaseMock) {
            var scenarioExecutionId = 1234L;
            doReturn(Map.of(EXECUTION_ID, scenarioExecutionId)).when(testCaseMock).getVariableDefinitions();
            return scenarioExecutionId;
        }

        @Test
        void shouldCompleteScenarioExecutionWithParameters() {
            // Arrange
            var defaultTestCaseMock = mock(DefaultTestCase.class);
            doReturn("shouldCompleteScenarioExecutionWithParameters").when(defaultTestCaseMock).getName();
            doReturn(getClass()).when(defaultTestCaseMock).getTestClass();
            doReturn(Map.of("key", "value")).when(defaultTestCaseMock).getParameters();

            var scenarioExecutionId = createAndGetScenarioExecutionId(defaultTestCaseMock);

            // Act
            fixture.onTestSuccess(defaultTestCaseMock);

            // Assert
            verify(scenarioExecutionServiceMock).completeScenarioExecution(eq(scenarioExecutionId), argThat(r -> r.getStatus() == SUCCESS && r.getTestParameters().size() == 1));
        }

        @Test
        void shouldCompleteScenarioExecutionWithoutParameters() {
            // Arrange
            var testCaseMock = mock(TestCase.class);
            doReturn("shouldCompleteScenarioExecutionWithoutParameters").when(testCaseMock).getName();
            doReturn(getClass()).when(testCaseMock).getTestClass();

            var scenarioExecutionId = createAndGetScenarioExecutionId(testCaseMock);

            // Act
            fixture.onTestSuccess(testCaseMock);

            // Assert
            verify(scenarioExecutionServiceMock).completeScenarioExecution(eq(scenarioExecutionId), argThat(r -> r.getStatus() == SUCCESS && r.getTestParameters().isEmpty()));
        }
    }

    @Nested
    class OnTestFailure {

        @Test
        void shouldCompleteScenarioExecutionWithParameters() {
            // Arrange
            var defaultTestCaseMock = mock(DefaultTestCase.class);
            doReturn("shouldCompleteScenarioExecutionWithParameters").when(defaultTestCaseMock).getName();
            doReturn(getClass()).when(defaultTestCaseMock).getTestClass();
            doReturn(Map.of("key", "value")).when(defaultTestCaseMock).getParameters();

            var scenarioExecutionId = 1234L;
            doReturn(Map.of(EXECUTION_ID, scenarioExecutionId)).when(defaultTestCaseMock).getVariableDefinitions();

            var errorMessage = "Test failure cause!";
            var cause = new CitrusRuntimeException(errorMessage);

            // Act
            fixture.onTestFailure(defaultTestCaseMock, cause);

            // Assert
            verify(scenarioExecutionServiceMock).completeScenarioExecution(eq(scenarioExecutionId), argThat(r -> r.getStatus() == FAILURE && r.getErrorMessage().equals(errorMessage) && r.getTestParameters().size() == 1));
        }

        @Test
        void shouldCompleteScenarioExecutionWithoutParameters() {
            // Arrange
            var testCaseMock = mock(TestCase.class);
            doReturn("shouldCompleteScenarioExecutionWithoutParameters").when(testCaseMock).getName();
            doReturn(getClass()).when(testCaseMock).getTestClass();

            var scenarioExecutionId = 1234L;
            doReturn(Map.of(EXECUTION_ID, scenarioExecutionId)).when(testCaseMock).getVariableDefinitions();

            var errorMessage = "Test failure cause!";
            var cause = new CitrusRuntimeException(errorMessage);

            // Act
            fixture.onTestFailure(testCaseMock, cause);

            // Assert
            verify(scenarioExecutionServiceMock).completeScenarioExecution(eq(scenarioExecutionId), argThat(r -> r.getStatus() == FAILURE && r.getErrorMessage().equals(errorMessage) && r.getTestParameters().isEmpty()));
        }
    }

    @Nested
    class OnTestActionStart {

        @Mock
        private TestCase testCaseMock;

        @Test
        void shouldCompleteTestAction() {
            var testActionMock = mock(TestAction.class);

            fixture.onTestActionStart(testCaseMock, testActionMock);

            verify(scenarioActionServiceMock).createForScenarioExecutionAndSave(testCaseMock, testActionMock);
        }

        @Test
        void shouldIgnoreSleepAction() {
            var testActionMock = mock(SleepAction.class);

            fixture.onTestActionStart(testCaseMock, testActionMock);

            verifyNoInteractions(testCaseMock, scenarioActionServiceMock, scenarioExecutionServiceMock);
        }
    }

    @Nested
    class OnTestActionFinish {

        @Mock
        private TestCase testCaseMock;

        @Test
        void shouldCompleteTestAction() {
            var testActionMock = mock(TestAction.class);

            fixture.onTestActionFinish(testCaseMock, testActionMock);

            verify(scenarioActionServiceMock).completeTestAction(testCaseMock, testActionMock);
        }

        @Test
        void shouldIgnoreSleepAction() {
            var testActionMock = mock(SleepAction.class);

            fixture.onTestActionFinish(testCaseMock, testActionMock);

            verifyNoInteractions(testCaseMock, scenarioActionServiceMock, scenarioExecutionServiceMock);
        }
    }

    private Map<String, TestResult> getRunningTests() {
        return (Map<String, TestResult>) getField(fixture, SimulatorStatusListener.class, "runningTests");
    }
}
