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

package org.citrusframework.simulator.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ScenarioExecutionTest {

    private ScenarioExecution fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new ScenarioExecution();
    }

    @Test
    void withTestResult() {
        var testResult = mock(TestResult.class);

        fixture.withTestResult(testResult);

        assertThat(fixture)
            .extracting(ScenarioExecution::getTestResult)
            .isEqualTo(testResult);
        verify(testResult).setScenarioExecution(fixture);
    }

    @Test
    void addScenarioParameter() {
        var scenarioParameter = mock(ScenarioParameter.class);

        fixture.addScenarioParameter(scenarioParameter);

        assertThat(fixture.getScenarioParameters()).containsExactly(scenarioParameter);
        verify(scenarioParameter).setScenarioExecution(fixture);
    }

    @Test
    void addScenarioAction() {
        var scenarioAction = mock(ScenarioAction.class);

        fixture.addScenarioAction(scenarioAction);

        assertThat(fixture.getScenarioActions()).containsExactly(scenarioAction);
        verify(scenarioAction).setScenarioExecution(fixture);
    }

    @Test
    void addScenarioMessage() {
        var message = mock(Message.class);

        fixture.addScenarioMessage(message);

        assertThat(fixture.getScenarioMessages()).containsExactly(message);
        verify(message).setScenarioExecution(fixture);
    }

    @Test
    void equalsVerifier() throws Exception {
        EntityTestUtils.equalsVerifier(ScenarioExecution.class);

        ScenarioExecution execution1 = new ScenarioExecution();
        execution1.setExecutionId(1L);

        ScenarioExecution execution2 = new ScenarioExecution();
        execution2.setExecutionId(execution1.getExecutionId());

        assertThat(execution1).isEqualTo(execution2);

        execution2.setExecutionId(2L);
        assertThat(execution1).isNotEqualTo(execution2);

        execution1.setExecutionId(null);
        assertThat(execution1).isNotEqualTo(execution2);
    }
}
