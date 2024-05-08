package org.citrusframework.simulator.service;

import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.MessageHeader;
import org.citrusframework.simulator.model.ScenarioAction;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioExecution_;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.model.TestResult_;
import org.citrusframework.simulator.repository.ScenarioExecutionRepository;
import org.citrusframework.simulator.service.criteria.ScenarioExecutionCriteria;
import org.citrusframework.simulator.service.filter.IntegerFilter;
import org.citrusframework.simulator.service.filter.LongFilter;
import org.citrusframework.simulator.web.rest.MessageResourceIT;
import org.citrusframework.simulator.web.rest.ScenarioActionResourceIT;
import org.citrusframework.simulator.web.rest.ScenarioExecutionResourceIT;
import org.citrusframework.simulator.web.rest.ScenarioParameterResourceIT;
import org.citrusframework.simulator.web.rest.TestResultResourceIT;
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
import org.springframework.data.domain.Sort.Direction;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.citrusframework.simulator.model.TestResult.Status.FAILURE;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Isolated
@IntegrationTest
class ScenarioExecutionQueryServiceIT {

    public static final String TRACEPARENT = "traceparent";

    public static final String MESSAGE_1_TRACEPARENT = "00-7a2a602f2a6560c041f362f66f76387b-cb1b70d4070d84ef-01";

    public static final String SOURCE = "source";
    public static final String SOURCE_VALUE = ScenarioExecutionQueryServiceIT.class.getSimpleName();

    @Autowired
    private ScenarioExecutionRepository scenarioExecutionRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ScenarioExecutionQueryService fixture;

    private ScenarioAction scenarioAction;
    private Message message;
    private ScenarioParameter scenarioParameter;

    private List<ScenarioExecution> scenarioExecutions;

    @BeforeEach
    void beforeEachSetup() {
        var now = Instant.now();

        scenarioAction = ScenarioActionResourceIT.createEntity(entityManager);
        message = MessageResourceIT.createEntityBuilder(entityManager)
            .citrusMessageId("e4d560c9-720f-4283-ac89-8f28b1d0f277")
            .build()
            .addHeader(MessageHeader.builder()
                .name(TRACEPARENT)
                .value(MESSAGE_1_TRACEPARENT)
                .build())
            .addHeader(MessageHeader.builder()
                .name(SOURCE)
                .value(SOURCE_VALUE)
                .build());
        scenarioParameter = ScenarioParameterResourceIT.createEntityBuilder(entityManager)
            .name("John")
            .value("Snow")
            .build();

        scenarioExecutions = new LinkedList<>();
        scenarioExecutions.add(
            ScenarioExecutionResourceIT.createEntityBuilder(entityManager)
                .startDate(now.minus(2, MINUTES))
                .endDate(now.minus(1, MINUTES))
                .build()
                .withTestResult(TestResultResourceIT.createEntity(entityManager))
                .addScenarioAction(ScenarioActionResourceIT.createEntity(entityManager))
                .addScenarioMessage(
                    MessageResourceIT.createEntityBuilder(entityManager)
                        .citrusMessageId("6eda9f2b-3a7a-423f-b19c-329ed3dd5ecc")
                        .build()
                        .addHeader(MessageHeader.builder()
                            .name(TRACEPARENT)
                            .value("00-83def191b1dda4c79c00ae4c443f0ca2-86535f4085bc9bc5-01")
                            .build())
                        .addHeader(MessageHeader.builder()
                            .name(SOURCE)
                            .value(SOURCE_VALUE)
                            .build()))
                .addScenarioParameter(scenarioParameter));
        scenarioExecutions.add(
            ScenarioExecutionResourceIT.createEntityBuilder(entityManager)
                .startDate(now.minus(4, MINUTES))
                .endDate(now)
                .build()
                .withTestResult(TestResultResourceIT.createEntity(entityManager))
                .addScenarioAction(ScenarioActionResourceIT.createEntity(entityManager))
                .addScenarioMessage(message)
                .addScenarioParameter(
                    ScenarioParameterResourceIT.createEntityBuilder(entityManager)
                        .name("another")
                        .value("parameter")
                        .build()));
        scenarioExecutions.add(
            ScenarioExecutionResourceIT.createEntityBuilder(entityManager)
                .startDate(now)
                .endDate(now.minus(1, MINUTES))
                .build()
                .withTestResult(TestResultResourceIT.createUpdatedEntity(entityManager))
                .addScenarioAction(scenarioAction)
                .addScenarioMessage(
                    MessageResourceIT.createEntityBuilder(entityManager)
                        .citrusMessageId("66666a09-7099-461b-aebb-260e776cd07f")
                        .build()
                        .addHeader(MessageHeader.builder()
                            .name("numeric")
                            .value("1234")
                            .build())
                        .addHeader(MessageHeader.builder()
                            .name(TRACEPARENT)
                            .value("00-1344094d192deb39a02025c6f9a67e3d-706d68f04da75c89-01")
                            .build())
                        .addHeader(MessageHeader.builder()
                            .name(SOURCE)
                            .value(SOURCE_VALUE)
                            .build()))
                .addScenarioParameter(
                    ScenarioParameterResourceIT.createEntityBuilder(entityManager)
                        .name("foo")
                        .value("bar")
                        .build()));

        scenarioExecutionRepository.saveAll(scenarioExecutions);
    }

    @Nested
    class FindByCriteria {

        @Test
        void selectExecutionIdLessThan() {
            var scenarioExecutionCriteria = new ScenarioExecutionCriteria();
            scenarioExecutionCriteria.setExecutionId((LongFilter) new LongFilter().setLessThan(scenarioExecutions.get(1).getExecutionId()));

            assertThatScenarioExecutionAtIndexSelectedByCriteria(scenarioExecutionCriteria, 0);
        }

        @Test
        void selectExecutionIdLessThanOrEqualTo() {
            var scenarioExecutionCriteria = new ScenarioExecutionCriteria();
            scenarioExecutionCriteria.setExecutionId((LongFilter) new LongFilter().setLessThanOrEqual(scenarioExecutions.get(1).getExecutionId()));

            Page<ScenarioExecution> scenarioExecutionPage = fixture.findByCriteria(
                scenarioExecutionCriteria,
                PageRequest.of(0, 2, Sort.by(ASC, ScenarioExecution_.EXECUTION_ID))
            );

            assertThat(scenarioExecutionPage.getTotalPages()).isEqualTo(1);
            assertThat(scenarioExecutionPage.getTotalElements()).isEqualTo(2L);
            assertThat(scenarioExecutionPage.getContent())
                .hasSize(2)
                .containsExactly(
                    scenarioExecutions.get(0),
                    scenarioExecutions.get(1));
        }

        @Test
        void selectExecutionIdGreaterThan() {
            var scenarioExecutionCriteria = new ScenarioExecutionCriteria();
            scenarioExecutionCriteria.setExecutionId((LongFilter) new LongFilter().setGreaterThan(scenarioExecutions.get(1).getExecutionId()));

            assertThatScenarioExecutionAtIndexSelectedByCriteria(scenarioExecutionCriteria, 2);
        }

        @Test
        void selectWithJoinToScenarioActions() {
            var scenarioExecutionCriteria = new ScenarioExecutionCriteria();
            scenarioExecutionCriteria.setScenarioActionsId((LongFilter) new LongFilter().setEquals(scenarioAction.getActionId()));

            assertThatScenarioExecutionAtIndexSelectedByCriteria(scenarioExecutionCriteria, 2);
        }

        @Test
        void selectWithJoinToMessages() {
            var scenarioExecutionCriteria = new ScenarioExecutionCriteria();
            scenarioExecutionCriteria.setScenarioMessagesId((LongFilter) new LongFilter().setEquals(message.getMessageId()));

            assertThatScenarioExecutionAtIndexSelectedByCriteria(scenarioExecutionCriteria, 1);
        }

        @Test
        void selectWithJoinToScenarioParameters() {
            var scenarioExecutionCriteria = new ScenarioExecutionCriteria();
            scenarioExecutionCriteria.setScenarioParametersId((LongFilter) new LongFilter().setEquals(scenarioParameter.getParameterId()));

            assertThatScenarioExecutionAtIndexSelectedByCriteria(scenarioExecutionCriteria, 0);
        }

        @Test
        void selectWithJoinToTestResult() {
            var scenarioExecutionCriteria = new ScenarioExecutionCriteria();
            scenarioExecutionCriteria.setStatus((IntegerFilter) new IntegerFilter().setEquals(FAILURE.getId()));

            assertThatScenarioExecutionAtIndexSelectedByCriteria(scenarioExecutionCriteria, 2);
        }

        public static Stream<Arguments> selectWithJoinToMessageHeader() {
            return Stream.of(
                arguments("83def191b1dda4c79c00ae4c443f0ca2", 0),
                arguments(TRACEPARENT + "=" + MESSAGE_1_TRACEPARENT, 1),
                arguments(TRACEPARENT + "~1344094d192deb39a02025c6f9a67e3d", 2)
            );
        }

        @MethodSource
        @ParameterizedTest
        void selectWithJoinToMessageHeader(String headerFilterPattern, int expectedIndex) {
            var scenarioExecutionCriteria = new ScenarioExecutionCriteria();
            scenarioExecutionCriteria.setHeaders(headerFilterPattern);

            assertThatScenarioExecutionAtIndexSelectedByCriteria(scenarioExecutionCriteria, expectedIndex);
        }

        @Test
        void selectWithJoinToMessageHeaderMultipleCriteria() {
            var scenarioExecutionCriteria = new ScenarioExecutionCriteria();
            scenarioExecutionCriteria.setHeaders(SOURCE + "=" + SOURCE_VALUE);

            Page<ScenarioExecution> scenarioExecutionPage = fixture.findByCriteria(scenarioExecutionCriteria, PageRequest.of(0, 3, Sort.by(ASC, ScenarioExecution_.EXECUTION_ID)));

            assertThat(scenarioExecutionPage.getTotalPages()).isEqualTo(1);
            assertThat(scenarioExecutionPage.getTotalElements()).isEqualTo(3L);

            scenarioExecutionCriteria.setHeaders(scenarioExecutionCriteria.getHeaders() + "; " + TRACEPARENT + "=" + MESSAGE_1_TRACEPARENT);
            assertThatScenarioExecutionAtIndexSelectedByCriteria(scenarioExecutionCriteria, 1);
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
            Page<ScenarioExecution> scenarioExecutionPage = fixture.findByCriteria(
                new ScenarioExecutionCriteria(),
                PageRequest.of(0, pageSize, Sort.by(ASC, ScenarioExecution_.EXECUTION_ID))
            );

            assertThat(scenarioExecutionPage.getTotalPages()).isEqualTo(expectedTotalPages);
            assertThat(scenarioExecutionPage.getTotalElements()).isEqualTo(3L);
            assertThat(scenarioExecutionPage.getContent()).hasSize(pageSize);
        }

        @Test
        void selectSecondPage() {
            Page<ScenarioExecution> testResultPage = fixture.findByCriteria(
                new ScenarioExecutionCriteria(),
                PageRequest.of(1, 1, Sort.by(ASC, TestResult_.ID))
            );

            assertThat(testResultPage.getTotalPages()).isEqualTo(3);
            assertThat(testResultPage.getTotalElements()).isEqualTo(3L);
            assertThat(testResultPage.getContent()).hasSize(1).first().isEqualTo(scenarioExecutions.get(1));
        }

        static Stream<Arguments> testSinglePropertySort() {
            return Stream.of(
                arguments(
                    ASC,
                    new String[]{ScenarioExecution_.EXECUTION_ID},
                    new int[]{0, 1, 2}),
                arguments(
                    DESC,
                    new String[]{ScenarioExecution_.EXECUTION_ID},
                    new int[]{2, 1, 0}),
                arguments(
                    ASC,
                    new String[]{ScenarioExecution_.START_DATE},
                    new int[]{1, 0, 2}),
                arguments(
                    DESC,
                    new String[]{ScenarioExecution_.START_DATE},
                    new int[]{2, 0, 1})
            );
        }

        @MethodSource
        @ParameterizedTest
        void testSinglePropertySort(Direction direction, String[] propertyNames, int[] indexOrder) {
            testSort(direction, propertyNames, indexOrder);
        }

        static Stream<Arguments> testMultiplePropertySort() {
            return Stream.of(
                arguments(
                    ASC,
                    new String[]{ScenarioExecution_.END_DATE, ScenarioExecution_.EXECUTION_ID},
                    new int[]{0, 2, 1}),
                arguments(
                    DESC,
                    new String[]{ScenarioExecution_.END_DATE, ScenarioExecution_.EXECUTION_ID},
                    new int[]{1, 2, 0})
            );
        }

        @MethodSource
        @ParameterizedTest
        void testMultiplePropertySort(Direction direction, String[] propertyNames, int[] indexOrder) {
            testSort(direction, propertyNames, indexOrder);
        }

        private void testSort(Direction direction, String[] propertyNames, int[] indexOrder) {
            Page<ScenarioExecution> scenarioExecutionPage = fixture.findByCriteria(
                new ScenarioExecutionCriteria(),
                PageRequest.of(0, 3, Sort.by(direction, propertyNames))
            );

            assertThat(scenarioExecutionPage)
                .hasSize(3)
                .containsExactly(
                    scenarioExecutions.get(indexOrder[0]),
                    scenarioExecutions.get(indexOrder[1]),
                    scenarioExecutions.get(indexOrder[2])
                );
        }
    }

    @AfterEach
    void afterEachTeardown() {
        scenarioExecutionRepository.deleteAll(scenarioExecutions);
    }

    private void assertThatScenarioExecutionAtIndexSelectedByCriteria(ScenarioExecutionCriteria scenarioExecutionCriteria, int index) {
        Page<ScenarioExecution> scenarioExecutionPage = fixture.findByCriteria(scenarioExecutionCriteria, PageRequest.of(0, 1, Sort.by(ASC, ScenarioExecution_.EXECUTION_ID)));

        assertThat(scenarioExecutionPage.getTotalPages()).isEqualTo(1);
        assertThat(scenarioExecutionPage.getTotalElements()).isEqualTo(1L);
        assertThat(scenarioExecutionPage.getContent()).hasSize(1).first().isEqualTo(scenarioExecutions.get(index));
    }
}
