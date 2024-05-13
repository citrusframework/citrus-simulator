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
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.Message_;
import org.citrusframework.simulator.model.TestResult_;
import org.citrusframework.simulator.repository.MessageRepository;
import org.citrusframework.simulator.service.criteria.MessageCriteria;
import org.citrusframework.simulator.service.filter.LongFilter;
import org.citrusframework.simulator.web.rest.MessageResourceIT;
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

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Isolated
@IntegrationTest
class MessageQueryServiceIT {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MessageQueryService fixture;

    private List<Message> messages;

    @BeforeEach
    void beforeEachSetup() {
        var now = Instant.now();

        messages = new LinkedList<>();
        messages.add(
            MessageResourceIT.createEntityBuilder(entityManager)
                .citrusMessageId("4c7964f8-c1c7-492a-856a-48b10fec5363")
                .createdDate(now.minus(1, MINUTES))
                .build());
        messages.add(
            MessageResourceIT.createEntityBuilder(entityManager)
                .citrusMessageId("30b418d2-c9e4-4be1-b6cf-5ab9ac9af6c3")
                .createdDate(now)
                .build());
        messages.add(
            MessageResourceIT.createEntityBuilder(entityManager)
                .citrusMessageId("4ff1c316-6ed3-4b5e-809b-4d76215346f1")
                .createdDate(now.minus(1, MINUTES))
                .build());

        messageRepository.saveAll(messages);
    }

    @Nested
    class FindByCriteria {

        @Test
        void selectMessageIdLessThan() {
            var messageCriteria = new MessageCriteria();
            messageCriteria.setMessageId((LongFilter) new LongFilter().setLessThan(messages.get(1).getMessageId()));

            assertThatMessageAtIndexSelectedByCriteria(messageCriteria, 0);
        }

        @Test
        void selectMessageIdLessThanOrEqualTo() {
            var messageCriteria = new MessageCriteria();
            messageCriteria.setMessageId((LongFilter) new LongFilter().setLessThanOrEqual(messages.get(1).getMessageId()));

            Page<Message> messagePage = fixture.findByCriteria(
                messageCriteria,
                PageRequest.of(0, 2, Sort.by(ASC, Message_.MESSAGE_ID))
            );

            assertThat(messagePage.getTotalPages()).isEqualTo(1);
            assertThat(messagePage.getTotalElements()).isEqualTo(2L);
            assertThat(messagePage.getContent())
                .hasSize(2)
                .containsExactly(
                    messages.get(0),
                    messages.get(1));
        }

        @Test
        void selectMessageIdGreaterThan() {
            var messageCriteria = new MessageCriteria();
            messageCriteria.setMessageId((LongFilter) new LongFilter().setGreaterThan(messages.get(1).getMessageId()));

            assertThatMessageAtIndexSelectedByCriteria(messageCriteria, 2);
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
            Page<Message> messagePage = fixture.findByCriteria(
                new MessageCriteria(),
                PageRequest.of(0, pageSize, Sort.by(ASC, Message_.MESSAGE_ID))
            );

            assertThat(messagePage.getTotalPages()).isEqualTo(expectedTotalPages);
            assertThat(messagePage.getTotalElements()).isEqualTo(3L);
            assertThat(messagePage.getContent()).hasSize(pageSize);
        }

        @Test
        void selectSecondPage() {
            Page<Message> testResultPage = fixture.findByCriteria(
                new MessageCriteria(),
                PageRequest.of(1, 1, Sort.by(ASC, TestResult_.ID))
            );

            assertThat(testResultPage.getTotalPages()).isEqualTo(3);
            assertThat(testResultPage.getTotalElements()).isEqualTo(3L);
            assertThat(testResultPage.getContent()).hasSize(1).first().isEqualTo(messages.get(1));
        }

        static Stream<Arguments> testSinglePropertySort() {
            return Stream.of(
                arguments(
                    ASC,
                    new String[]{Message_.MESSAGE_ID},
                    new int[]{0, 1, 2}),
                arguments(
                    DESC,
                    new String[]{Message_.MESSAGE_ID},
                    new int[]{2, 1, 0}),
                arguments(
                    ASC,
                    new String[]{Message_.CITRUS_MESSAGE_ID},
                    new int[]{1, 0, 2}),
                arguments(
                    DESC,
                    new String[]{Message_.CITRUS_MESSAGE_ID},
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
                    new String[]{Message_.CREATED_DATE, Message_.MESSAGE_ID},
                    new int[]{0, 2, 1}),
                arguments(
                    DESC,
                    new String[]{Message_.CREATED_DATE, Message_.MESSAGE_ID},
                    new int[]{1, 2, 0})
            );
        }

        @MethodSource
        @ParameterizedTest
        void testMultiplePropertySort(Sort.Direction direction, String[] propertyNames, int[] indexOrder) {
            testSort(direction, propertyNames, indexOrder);
        }

        private void testSort(Sort.Direction direction, String[] propertyNames, int[] indexOrder) {
            Page<Message> messagePage = fixture.findByCriteria(
                new MessageCriteria(),
                PageRequest.of(0, 3, Sort.by(direction, propertyNames))
            );

            assertThat(messagePage)
                .hasSize(3)
                .containsExactly(
                    messages.get(indexOrder[0]),
                    messages.get(indexOrder[1]),
                    messages.get(indexOrder[2]));
        }
    }

    @AfterEach
    void afterEachTeardown() {
        messageRepository.deleteAll(messages);
    }

    private void assertThatMessageAtIndexSelectedByCriteria(MessageCriteria messageCriteria, int index) {
        Page<Message> messagePage = fixture.findByCriteria(messageCriteria, PageRequest.of(0, 1, Sort.by(ASC, Message_.MESSAGE_ID)));

        assertThat(messagePage.getTotalPages()).isEqualTo(1);
        assertThat(messagePage.getTotalElements()).isEqualTo(1L);
        assertThat(messagePage.getContent()).hasSize(1).first().isEqualTo(messages.get(index));
    }
}
