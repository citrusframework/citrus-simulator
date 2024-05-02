package org.citrusframework.simulator.service.impl;

import org.citrusframework.simulator.model.TestResult;
import org.citrusframework.simulator.repository.TestResultRepository;
import org.citrusframework.simulator.service.dto.TestResultByStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TestResultServiceImplTest {

    @Mock
    private TestResultRepository testResultRepositoryMock;

    private TestResultServiceImpl fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new TestResultServiceImpl(testResultRepositoryMock);
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
        verify(testResultRepositoryMock).deleteAll();
    }
}
