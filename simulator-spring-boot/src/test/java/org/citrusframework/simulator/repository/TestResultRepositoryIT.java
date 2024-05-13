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
    void beforeEachSetup() {
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
    void countByStatus() {
        TestResultByStatus testResultByStatus = testResultRepository.countByStatus();

        assertEquals(2, testResultByStatus.total());
        assertEquals(1, testResultByStatus.successful());
        assertEquals(1, testResultByStatus.failed());
    }

    @AfterEach
    void afterEachTeardown() {
        testResultRepository.deleteAll(testResults);
    }
}
