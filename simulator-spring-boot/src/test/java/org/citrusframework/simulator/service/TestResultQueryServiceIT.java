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

import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.model.TestParameter;
import org.citrusframework.simulator.model.TestResult;
import org.citrusframework.simulator.model.TestResult_;
import org.citrusframework.simulator.repository.TestResultRepository;
import org.citrusframework.simulator.service.criteria.TestResultCriteria;
import org.citrusframework.simulator.service.filter.LongFilter;
import org.citrusframework.simulator.service.filter.StringFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Isolated
@Transactional
@IntegrationTest
class TestResultQueryServiceIT {

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestResultQueryService fixture;

    private List<TestResult> testResults;

    @BeforeEach
    void beforeEachSetup() {
        testResults = new LinkedList<>();
        testResults.add(
            TestResult.builder()
                .testName("B")
                .className(getClass().getSimpleName())
                .errorMessage("an error has occurred :o")
                .build());
        testResults.add(
            TestResult.builder()
                .testName("A")
                .className(getClass().getSimpleName())
                .errorMessage("z-index is something in web development, isn't it?")
                .build());
        testResults.add(
            TestResult.builder()
                .testName("C")
                .className(getClass().getSimpleName())
                .errorMessage("an error has occurred :o")
                .build());

        testResultRepository.saveAll(testResults);

        var testParameter1 = TestParameter.builder()
            .key("key")
            .value("value")
            .build();
        testResults.get(0).addTestParameter(testParameter1);
        entityManager.persist(testParameter1);

        var testParameter2 = TestParameter.builder()
            .key("the chosen one")
            .value("is?")
            .build();
        testResults.get(1).addTestParameter(testParameter2);
        entityManager.persist(testParameter2);

        var testParameter3 = TestParameter.builder()
            .key("foo")
            .value("bar")
            .build();
        testResults.get(2).addTestParameter(testParameter3);
        entityManager.persist(testParameter3);
    }

    @Nested
    class FindByCriteria {

        @Test
        void selectTestResultIdLessThan() {
            var testResultCriteria = new TestResultCriteria();
            testResultCriteria.setId((LongFilter) new LongFilter().setLessThan(testResults.get(1).getId()));

            assertThatTestResultAtIndexSelectedByCriteria(testResultCriteria, 0);
        }

        @Test
        void selectMessageIdLessThanOrEqualTo() {
            var testResultCriteria = new TestResultCriteria();
            testResultCriteria.setId((LongFilter) new LongFilter().setLessThanOrEqual(testResults.get(1).getId()));

            Page<TestResult> testResultPage = fixture.findByCriteria(
                testResultCriteria,
                PageRequest.of(0, 2, Sort.by(ASC, TestResult_.ID))
            );

            assertThat(testResultPage.getTotalPages()).isEqualTo(1);
            assertThat(testResultPage.getTotalElements()).isEqualTo(2L);
            assertThat(testResultPage.getContent())
                .hasSize(2)
                .containsExactly(
                    testResults.get(0),
                    testResults.get(1));
        }

        @Test
        void selectTestResultIdGreaterThan() {
            var testResultCriteria = new TestResultCriteria();
            testResultCriteria.setId((LongFilter) new LongFilter().setGreaterThan(testResults.get(1).getId()));

            assertThatTestResultAtIndexSelectedByCriteria(testResultCriteria, 2);
        }

        @Test
        void selectWithJoinToTestParameters() {
            var testResultCriteria = new TestResultCriteria();
            testResultCriteria.setTestParameterKey(new StringFilter().setContains("chosen"));

            assertThatTestResultAtIndexSelectedByCriteria(testResultCriteria, 1);
        }

        static Stream<Arguments> testPagination() {
            return Stream.of(
                arguments(1, 3),
                arguments(2, 2),
                arguments(3, 1)
            );
        }

        @MethodSource
        @ParameterizedTest
        void testPagination(int pageSize, int expectedTotalPages) {
            Page<TestResult> testResultPage = fixture.findByCriteria(
                new TestResultCriteria(),
                PageRequest.of(0, pageSize, Sort.by(ASC, TestResult_.ID))
            );

            assertThat(testResultPage.getTotalPages()).isEqualTo(expectedTotalPages);
            assertThat(testResultPage.getTotalElements()).isEqualTo(3L);
            assertThat(testResultPage.getContent()).hasSize(pageSize);
        }

        @Test
        void selectSecondPage() {
            Page<TestResult> testResultPage = fixture.findByCriteria(
                new TestResultCriteria(),
                PageRequest.of(1, 1, Sort.by(ASC, TestResult_.ID))
            );

            assertThat(testResultPage.getTotalPages()).isEqualTo(3);
            assertThat(testResultPage.getTotalElements()).isEqualTo(3L);
            assertThat(testResultPage.getContent()).hasSize(1).first().isEqualTo(testResults.get(1));
        }

        static Stream<Arguments> testSinglePropertySort() {
            return Stream.of(
                arguments(
                    ASC,
                    new String[]{TestResult_.ID},
                    new int[]{0, 1, 2}),
                arguments(
                    DESC,
                    new String[]{TestResult_.ID},
                    new int[]{2, 1, 0}),
                arguments(
                    ASC,
                    new String[]{TestResult_.TEST_NAME},
                    new int[]{1, 0, 2}),
                arguments(
                    DESC,
                    new String[]{TestResult_.TEST_NAME},
                    new int[]{2, 0, 1})
            );
        }

        @MethodSource
        @ParameterizedTest
        void testSinglePropertySort(Sort.Direction direction, String[] propertyNames, int[] indexOrder) {
            testSort(direction, propertyNames, indexOrder);
        }

        static Stream<Arguments> testMultiplePropertySort() {
            return Stream.of(
                arguments(
                    ASC,
                    new String[]{TestResult_.ERROR_MESSAGE, TestResult_.ID},
                    new int[]{0, 2, 1}),
                arguments(
                    DESC,
                    new String[]{TestResult_.ERROR_MESSAGE, TestResult_.ID},
                    new int[]{1, 2, 0})
            );
        }

        @MethodSource
        @ParameterizedTest
        void testMultiplePropertySort(Sort.Direction direction, String[] propertyNames, int[] indexOrder) {
            testSort(direction, propertyNames, indexOrder);
        }

        private void testSort(Sort.Direction direction, String[] propertyNames, int[] indexOrder) {
            Page<TestResult> testResultPage = fixture.findByCriteria(
                new TestResultCriteria(),
                PageRequest.of(0, 3, Sort.by(direction, propertyNames))
            );

            assertThat(testResultPage)
                .hasSize(3)
                .containsExactly(
                    testResults.get(indexOrder[0]),
                    testResults.get(indexOrder[1]),
                    testResults.get(indexOrder[2])
                );
        }
    }

    private void assertThatTestResultAtIndexSelectedByCriteria(TestResultCriteria testResultCriteria, int index) {
        Page<TestResult> testResultPage = fixture.findByCriteria(testResultCriteria, PageRequest.of(0, 1, Sort.by(ASC, TestResult_.ID)));

        assertThat(testResultPage.getTotalPages()).isEqualTo(1);
        assertThat(testResultPage.getTotalElements()).isEqualTo(1L);
        assertThat(testResultPage.getContent()).hasSize(1).first().isEqualTo(testResults.get(index));
    }

    @AfterEach
    void afterEachTeardown() {
        testResultRepository.deleteAll(testResults);
    }
}
