package org.citrusframework.simulator.service;

import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.model.ScenarioAction;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith({MockitoExtension.class})
class ScenarioActionServiceTest {

    @Mock
    private EntityManager entityManagerMock;

    @Nested
    class RestrictToDtoProperties {

        @Test
        void shouldFilterMessageDetails() {
            var scenarioExecution = mock(ScenarioExecution.class);
            doReturn(1234L).when(scenarioExecution).getExecutionId();
            doReturn("scenario-name").when(scenarioExecution).getScenarioName();

            var scenarioAction = new ScenarioAction();
            scenarioAction.setScenarioExecution(scenarioExecution);

            var restrictedScenarioAction = ScenarioActionService.restrictToDtoProperties(scenarioAction, entityManagerMock);

            var restrictedScenarioExecution = restrictedScenarioAction.getScenarioExecution();
            assertEquals(scenarioExecution.getExecutionId(), restrictedScenarioExecution.getExecutionId());
            assertEquals(scenarioExecution.getScenarioName(), restrictedScenarioExecution.getScenarioName());

            verify(scenarioExecution, never()).getScenarioParameters();
            verify(scenarioExecution, never()).getScenarioActions();
            verify(scenarioExecution, never()).getScenarioMessages();

            verify(entityManagerMock).detach(scenarioAction);
        }

        @Test
        void shouldHandleNullMessage() {
            var scenarioAction = new ScenarioAction();

            var restrictedScenarioAction = ScenarioActionService.restrictToDtoProperties(scenarioAction, entityManagerMock);

            assertNull(restrictedScenarioAction.getScenarioExecution());

            verifyNoInteractions(entityManagerMock);
        }
    }
}
