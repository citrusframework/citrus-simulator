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

package org.citrusframework.simulator.service.impl;

import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.model.TestResult;
import org.citrusframework.simulator.repository.ScenarioExecutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.data.domain.Pageable.unpaged;

@ExtendWith(MockitoExtension.class)
class ScenarioExecutionServiceImplTest {

    @Mock
    private ScenarioExecutionRepository scenarioExecutionRepositoryMock;

    @Mock
    private TimeProvider timeProviderMock;

    private ScenarioExecution sampleScenarioExecution;

    private ScenarioExecutionServiceImpl fixture;

    @BeforeEach
    void beforeEachSetup() {
        sampleScenarioExecution = new ScenarioExecution();

        fixture = new ScenarioExecutionServiceImpl(scenarioExecutionRepositoryMock);
        ReflectionTestUtils.setField(fixture, "timeProvider", timeProviderMock, TimeProvider.class);
    }

    @Test
    void testSave() {
        doReturn(sampleScenarioExecution).when(scenarioExecutionRepositoryMock).save(sampleScenarioExecution);

        ScenarioExecution savedScenarioExecution = fixture.save(sampleScenarioExecution);
        assertEquals(sampleScenarioExecution, savedScenarioExecution);
    }

    @Test
    void testFindAll() {
        Pageable pageable = unpaged();
        Page<ScenarioExecution> page = new PageImpl<>(List.of(sampleScenarioExecution));

        doReturn(page).when(scenarioExecutionRepositoryMock).findAll(pageable);

        Page<ScenarioExecution> result = fixture.findAll(pageable);

        assertEquals(page, result);
    }

    @Test
    void testFindOne() {
        Long scenarioExecutionId = 1L;

        doReturn(Optional.of(sampleScenarioExecution)).when(scenarioExecutionRepositoryMock).findOneByExecutionId(scenarioExecutionId);

        Optional<ScenarioExecution> maybeScenarioExecution = fixture.findOne(scenarioExecutionId);

        assertTrue(maybeScenarioExecution.isPresent());
        assertEquals(sampleScenarioExecution, maybeScenarioExecution.get());
    }

    @Test
    void findOneLazy() {
        Long scenarioExecutionId = 1L;

        doReturn(Optional.of(sampleScenarioExecution)).when(scenarioExecutionRepositoryMock).findById(scenarioExecutionId);

        Optional<ScenarioExecution> maybeScenarioExecution = fixture.findOneLazy(scenarioExecutionId);

        assertTrue(maybeScenarioExecution.isPresent());
        assertEquals(sampleScenarioExecution, maybeScenarioExecution.get());
    }

    @Test
    void testCreateAndSaveExecutionScenario() {
        String scenarioName = "sampleScenario";

        Instant now = Instant.now();
        doReturn(now).when(timeProviderMock).getTimeNow();

        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ScenarioExecution.class)).when(scenarioExecutionRepositoryMock).save(any(ScenarioExecution.class));

        ScenarioParameter scenarioParameter = new ScenarioParameter();
        List<ScenarioParameter> scenarioParameters = List.of(scenarioParameter);

        ScenarioExecution result = fixture.createAndSaveExecutionScenario(scenarioName, scenarioParameters);

        assertEquals(scenarioName, result.getScenarioName());
        assertEquals(now, result.getStartDate());
        assertThat(result.getScenarioParameters())
            .hasSize(1)
            .containsExactly(scenarioParameter);
    }

    @Nested
    class CompleteScenarioExecution {

        private final Long scenarioExecutionId = 1234L;
        private final Instant now = Instant.now();

        @Mock
        private TestResult testResultMock;

        @Test
        void withTestResult() {
            doReturn(Optional.of(sampleScenarioExecution)).when(scenarioExecutionRepositoryMock).findOneByExecutionId(scenarioExecutionId);
            doReturn(sampleScenarioExecution).when(scenarioExecutionRepositoryMock).save(sampleScenarioExecution);
            doReturn(now).when(timeProviderMock).getTimeNow();

            var result = fixture.completeScenarioExecution(scenarioExecutionId, testResultMock);

            assertEquals(testResultMock, result.getTestResult());
            assertEquals(now, result.getEndDate());
        }

        @Test
        void withNoScenarioExecutionFound() {
            String testName = "testCase";
            doReturn(testName).when(testResultMock).getTestName();

            doReturn(empty()).when(scenarioExecutionRepositoryMock).findOneByExecutionId(scenarioExecutionId);

            assertThatThrownBy(() -> fixture.completeScenarioExecution(scenarioExecutionId, testResultMock))
                .isInstanceOf(CitrusRuntimeException.class)
                .hasMessage(format("Error while completing ScenarioExecution for test %s", testName));
        }

        @Test
        void withScenarioExecutionThatHasAlreadyBeenEnded() {
            sampleScenarioExecution.setEndDate(now);
            doReturn(Optional.of(sampleScenarioExecution)).when(scenarioExecutionRepositoryMock).findOneByExecutionId(scenarioExecutionId);

            assertDoesNotThrow(() -> fixture.completeScenarioExecution(scenarioExecutionId, testResultMock));

            verifyNoInteractions(testResultMock);
            verifyNoInteractions(timeProviderMock);
        }
    }
}
