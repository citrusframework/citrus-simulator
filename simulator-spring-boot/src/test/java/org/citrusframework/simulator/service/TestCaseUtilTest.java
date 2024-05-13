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
