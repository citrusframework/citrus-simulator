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

import org.citrusframework.simulator.model.TestResult;
import org.citrusframework.simulator.repository.MessageHeaderRepository;
import org.citrusframework.simulator.repository.MessageRepository;
import org.citrusframework.simulator.repository.ScenarioActionRepository;
import org.citrusframework.simulator.repository.ScenarioExecutionRepository;
import org.citrusframework.simulator.repository.ScenarioParameterRepository;
import org.citrusframework.simulator.repository.TestParameterRepository;
import org.citrusframework.simulator.repository.TestResultRepository;
import org.citrusframework.simulator.service.dto.TestResultByStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TestResultServiceImplTest {

    @Mock
    private TestResultRepository testResultRepositoryMock;

    @Mock
    private TestParameterRepository testParameterRepositoryMock;

    @Mock
    private ScenarioExecutionRepository scenarioExecutionRepositoryMock;

    @Mock
    private ScenarioActionRepository scenarioActionRepositoryMock;

    @Mock
    private ScenarioParameterRepository scenarioParameterRepositoryMock;

    @Mock
    private MessageRepository messageRepositoryMock;

    @Mock
    private MessageHeaderRepository messageHeaderRepositoryMock;

    private TestResultServiceImpl fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new TestResultServiceImpl(
            testResultRepositoryMock,
            testParameterRepositoryMock,
            scenarioExecutionRepositoryMock,
            scenarioActionRepositoryMock,
            scenarioParameterRepositoryMock,
            messageRepositoryMock,
            messageHeaderRepositoryMock
        );
    }

    @Test
    void testSave() {
        TestResult testResult = new TestResult();
        doReturn(testResult).when(testResultRepositoryMock).save(testResult);

        TestResult result = fixture.save(testResult);

        assertEquals(testResult, result);
    }

    @Test
    void testFindAll() {
        Pageable pageable = mock(Pageable.class);
        Page<TestResult> mockPage = mock(Page.class);
        doReturn(mockPage).when(testResultRepositoryMock).findAll(pageable);

        Page<TestResult> result = fixture.findAll(pageable);

        assertEquals(mockPage, result);
    }

    @Test
    void testFindOne() {
        Long id = 1L;

        TestResult testResult = new TestResult();
        Optional<TestResult> optionalTestResult = Optional.of(testResult);
        doReturn(optionalTestResult).when(testResultRepositoryMock).findById(id);

        Optional<TestResult> maybeTestResult = fixture.findOne(id);

        assertTrue(maybeTestResult.isPresent());
        assertEquals(testResult, maybeTestResult.get());
    }

    @Test
    void testCountByStatus() {
        TestResultByStatus testResultByStatus = new TestResultByStatus(1L, 1L);
        doReturn(testResultByStatus).when(testResultRepositoryMock).countByStatus();

        TestResultByStatus result = fixture.countByStatus();
        assertEquals(testResultByStatus, result);
    }

    @Test
    void delete() {
        fixture.deleteAll();

        InOrder inOrder = inOrder(
            messageHeaderRepositoryMock,
            messageRepositoryMock,
            scenarioParameterRepositoryMock,
            scenarioActionRepositoryMock,
            testParameterRepositoryMock,
            scenarioExecutionRepositoryMock,
            testResultRepositoryMock
        );

        inOrder.verify(messageHeaderRepositoryMock).deleteAllInBatch();
        inOrder.verify(messageRepositoryMock).deleteAllInBatch();
        inOrder.verify(scenarioParameterRepositoryMock).deleteAllInBatch();
        inOrder.verify(scenarioActionRepositoryMock).deleteAllInBatch();
        inOrder.verify(testParameterRepositoryMock).deleteAllInBatch();
        inOrder.verify(scenarioExecutionRepositoryMock).deleteAllInBatch();
        inOrder.verify(testResultRepositoryMock).deleteAllInBatch();
    }
}
