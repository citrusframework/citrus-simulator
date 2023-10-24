package org.citrusframework.simulator.repository;

import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.model.TestResult;
import org.citrusframework.simulator.service.dto.TestResultByStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing custom methods in {@link TestResultRepository}.
 */
@IntegrationTest
class TestResultRepositoryIT {

    @Autowired
    private TestResultRepository testResultRepository;

    private List<TestResult> testResults;

    @BeforeEach
    void beforeEachSetup(){
        testResults = testResultRepository.saveAll(
            List.of(
                TestResult.builder()
                    .testName("Test-1")
                    .className(getClass().getSimpleName())
                    .status(TestResult.Status.SUCCESS).build(),
                TestResult.builder()
                    .testName("Test-2")
                    .className(getClass().getSimpleName())
                    .status(TestResult.Status.FAILURE).build()
            )
        );
    }
    @Test
    @Transactional
    void countByStatus(){
        TestResultByStatus testResultByStatus = testResultRepository.countByStatus();

        assertEquals(2, testResultByStatus.total());
        assertEquals(1, testResultByStatus.successful());
        assertEquals(1, testResultByStatus.failed());
    }

    @AfterEach
    void afterEachTeardown(){
        testResultRepository.deleteAll(testResults);
    }
}
