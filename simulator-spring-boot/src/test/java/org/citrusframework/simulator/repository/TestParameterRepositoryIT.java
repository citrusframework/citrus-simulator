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

import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.model.TestParameter;
import org.citrusframework.simulator.model.TestResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
class TestParameterRepositoryIT {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestParameterRepository testParameterRepository;

    private TestParameter testParameter1;
    private TestParameter testParameter2;

    @BeforeEach
    void beforeEachSetup() {
        TestResult testResult = TestResult.builder()
            .className(getClass().getSimpleName())
            .testName("Test-1")
            .build();
        entityManager.persist(testResult);

        testParameter1 = TestParameter.builder()
            .key("key-1")
            .value("sweet honey")
            .testResult(testResult)
            .build();
        entityManager.persist(testParameter1);

        testParameter2 = TestParameter.builder()
            .key("key-2")
            .value("sour whisky")
            .testResult(testResult)
            .build();
        entityManager.persist(testParameter2);
    }

    @Test
    @Transactional
    void findByCompositeId() {
        verifyOnlyOneTestParameterFoundByCompositeId(testParameter1, testParameterRepository::findByCompositeId);
        verifyOnlyOneTestParameterFoundByCompositeId(testParameter2, testParameterRepository::findByCompositeId);
    }

    @Test
    @Transactional
    void findOneByTestParameterIdTestResultIdEqualsAndTestParameterIdKeyEquals() {
        verifyOnlyOneTestParameterFoundByCompositeId(testParameter1, testParameterRepository::findOneByTestParameterIdTestResultIdEqualsAndTestParameterIdKeyEquals);
        verifyOnlyOneTestParameterFoundByCompositeId(testParameter2, testParameterRepository::findOneByTestParameterIdTestResultIdEqualsAndTestParameterIdKeyEquals);
    }

    private void verifyOnlyOneTestParameterFoundByCompositeId(TestParameter testParameter, BiFunction<Long, String, Optional<TestParameter>> findByCompositeId) {
        Optional<TestParameter> result = findByCompositeId.apply(
            testParameter.getTestResult().getId(),
            testParameter.getKey()
        );

        assertTrue(result.isPresent(), "TestParameter by composite-ID should have been found!");
        assertEquals(testParameter, result.get());
    }
}
