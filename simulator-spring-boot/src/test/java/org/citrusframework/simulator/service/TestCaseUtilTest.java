package org.citrusframework.simulator.service;

import org.citrusframework.TestCase;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Long.parseLong;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.citrusframework.simulator.service.TestCaseUtil.getScenarioExecutionId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class TestCaseUtilTest {

    @Mock
    private TestCase testCase;

    @Nested
    class GetScenarioExecutionId {

        @Test
        void shouldGetScenarioExecutionIdWhenPresent() {
            var executionId = "1234";
            doReturn(Map.of(ScenarioExecution.EXECUTION_ID, executionId)).when(testCase).getVariableDefinitions();

            long retrievedId = getScenarioExecutionId(testCase);

            assertEquals(parseLong(executionId), retrievedId);
        }

        @Test
        void shouldThrowExceptionWhenExecutionIdMissing() {
            when(testCase.getVariableDefinitions()).thenReturn(new HashMap<>());

            assertThatThrownBy(() -> getScenarioExecutionId(testCase))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TestCase does not contain 'scenarioExecutionId'!");
        }
    }
}
