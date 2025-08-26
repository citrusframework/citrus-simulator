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

package org.citrusframework.simulator.scenario;

import org.citrusframework.DefaultTestActions;
import org.citrusframework.DefaultTestCaseRunner;
import org.citrusframework.context.TestContext;
import org.citrusframework.message.DefaultMessageProcessors;
import org.citrusframework.validation.DefaultValidations;
import org.citrusframework.variable.DefaultVariableExtractors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
class ScenarioRunnerTest {

    @Mock
    private TestContext testContextMock;

    @Mock
    private ScenarioEndpoint scenarioEndpointMock;

    @Mock
    private ApplicationContext applicationContextMock;

    ScenarioRunner fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new ScenarioRunner(scenarioEndpointMock, applicationContextMock, testContextMock);
    }

    @Test
    void constructor_initializesFields() {
        assertThat(fixture)
            .hasNoNullFieldsOrProperties();
    }

    @Test
    void actions_isInstanceOf_DefaultTestActions() {
        assertThat(fixture.actions())
            .isInstanceOf(DefaultTestActions.class);
    }

    @Test
    void containers_isInstanceOf_DefaultTestActions() {
        assertThat(fixture.containers())
            .isInstanceOf(DefaultTestActions.class);
    }

    @Test
    void validation_isInstanceOf_DefaultValidations() {
        assertThat(fixture.validation())
            .isInstanceOf(DefaultValidations.class);
    }

    @Test
    void extractor_isInstanceOf_DefaultVariableExtractors() {
        assertThat(fixture.extractor())
            .isInstanceOf(DefaultVariableExtractors.class);
    }

    @Test
    void processor_isInstanceOf_DefaultMessageProcessors() {
        assertThat(fixture.processor())
            .isInstanceOf(DefaultMessageProcessors.class);
    }

    @Test
    void delegate_isInstanceOf_DefaultTestCaseRunner() {
        assertThat(fixture.getTestCaseRunner())
            .isInstanceOf(DefaultTestCaseRunner.class)
            .extracting(testCaseRunner -> ((DefaultTestCaseRunner) testCaseRunner).getContext())
            .isEqualTo(testContextMock);
    }
}
