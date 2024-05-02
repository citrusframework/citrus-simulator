package org.citrusframework.simulator.service;

import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.TestResult;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.citrusframework.simulator.model.TestResult.Status.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith({MockitoExtension.class})
class ScenarioExecutionServiceTest {

    @Mock
    private EntityManager entityManagerMock;

    @Nested
    class RestrictToDtoProperties {

        @Test
        void shouldFilterTestResultDetails() {
            var testResult = mock(TestResult.class);
            doReturn(SUCCESS).when(testResult).getStatus();
            doReturn("errorMessage").when(testResult).getErrorMessage();
            doReturn("stackTrace").when(testResult).getStackTrace();

            var scenarioExecution = new ScenarioExecution();
            scenarioExecution.setTestResult(testResult);

            var restrictedScenarioExecution = ScenarioExecutionService.restrictToDtoProperties(scenarioExecution, entityManagerMock);

            var restrictedTestResult = restrictedScenarioExecution.getTestResult();
            assertEquals(testResult.getStatus(), restrictedTestResult.getStatus());
            assertEquals(testResult.getErrorMessage(), restrictedTestResult.getErrorMessage());
            assertEquals(testResult.getStackTrace(), restrictedTestResult.getStackTrace());

            verify(testResult, never()).getTestParameters();
            verify(entityManagerMock).detach(scenarioExecution);
        }

        @Test
        void shouldHandleNullTestResult() {
            var execution = new ScenarioExecution();

            var restrictedExecution = ScenarioExecutionService.restrictToDtoProperties(execution, entityManagerMock);

            assertNull(restrictedExecution.getTestResult());

            verifyNoInteractions(entityManagerMock);
        }
    }
}
