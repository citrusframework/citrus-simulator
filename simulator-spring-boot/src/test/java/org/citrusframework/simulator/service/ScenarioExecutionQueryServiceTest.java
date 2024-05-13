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

import org.assertj.core.api.InstanceOfAssertFactories;
import org.citrusframework.simulator.service.ScenarioExecutionQueryService.MessageHeaderFilter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.citrusframework.simulator.service.ScenarioExecutionQueryService.Operator.CONTAINS;
import static org.citrusframework.simulator.service.ScenarioExecutionQueryService.Operator.EQUALS;
import static org.citrusframework.simulator.service.ScenarioExecutionQueryService.Operator.GREATER_THAN;
import static org.citrusframework.simulator.service.ScenarioExecutionQueryService.Operator.GREATER_THAN_OR_EQUAL_TO;
import static org.citrusframework.simulator.service.ScenarioExecutionQueryService.Operator.LESS_THAN;
import static org.citrusframework.simulator.service.ScenarioExecutionQueryService.Operator.LESS_THAN_OR_EQUAL_TO;
import static org.citrusframework.simulator.service.ScenarioExecutionQueryService.isValidFilterPattern;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ScenarioExecutionQueryServiceTest {

    @Nested
    class IsValidFilterPattern {

        public static Stream<String> isValidPattern() {
            return Stream.of(
                // Real word samples
                "accept-encoding=gzip",
                "citrus_endpoint_uri=/services/rest/simulator",
                "citrus_http_version=HTTP/1.1",
                "connection=keep-alive",
                "host=localhost:8080",
                "user-agent=Apache-HttpClient/5.2.3 (Java/17.0.11)",
                // Made-up key-value pairs
                "key=",
                "key=value",
                "kebab-case=value",
                "snake_case=value",
                "key=b1028a4e-df33-40b6-a5aa-abbec001606a",
                "key=snake_case",
                "key~value",
                "key<1234",
                "key>2345",
                "key>=3456",
                "key<=4567"
            );
        }

        @MethodSource
        @ParameterizedTest
        void isValidPattern(String pattern) {
            assertThat(isValidFilterPattern(pattern))
                .isTrue();
        }

        public static Stream<String> isInvalidPattern() {
            return Stream.of(
                "key<foo",
                "key>bar",
                "key>=baz",
                "key<=boom",
                " key-starting-with-space",
                "key with spaces=value",
                "contentType=application/xml;charset=UTF-8"
            );
        }

        @MethodSource
        @ParameterizedTest
        void isInvalidPattern(String pattern) {
            assertThat(isValidFilterPattern(pattern))
                .isFalse();
        }
    }

    @Nested
    class MessageHeaderFilterTest {

        @Nested
        class FromFilterPattern {

            public static Stream<String> fromValueOnlyPattern() {
                return Stream.of(
                    "header",
                    "header with spaces",
                    "snake_case",
                    "b1028a4e-df33-40b6-a5aa-abbec001606a"
                );
            }

            @MethodSource
            @ParameterizedTest
            void fromValueOnlyPattern(String valuePattern) throws ScenarioExecutionQueryService.InvalidPatternException {
                assertThat(ScenarioExecutionQueryService.MessageHeaderFilter.fromFilterPattern(valuePattern))
                    .hasAllNullFieldsOrPropertiesExcept("value")
                    .hasFieldOrPropertyWithValue("value", valuePattern);
            }

            public static Stream<Arguments> fromKeyAndValuePattern() {
                return Stream.of(
                    arguments("key=", "key", EQUALS, ""),
                    arguments("key=value", "key", EQUALS, "value"),
                    arguments("key= header starting with space", "key", EQUALS, " header starting with space"),
                    arguments("key~value", "key", CONTAINS, "value"),
                    arguments("key<1234", "key", LESS_THAN, "1234"),
                    arguments("key>2345", "key", GREATER_THAN, "2345"),
                    arguments("key<=4567", "key", LESS_THAN_OR_EQUAL_TO, "4567"),
                    arguments("key>=3456", "key", GREATER_THAN_OR_EQUAL_TO, "3456")
                );
            }

            @MethodSource
            @ParameterizedTest
            void fromKeyAndValuePattern(String filterPattern, String expectedKey, ScenarioExecutionQueryService.Operator expectedOperator, String expectedValue) throws ScenarioExecutionQueryService.InvalidPatternException {
                assertThat(ScenarioExecutionQueryService.MessageHeaderFilter.fromFilterPattern(filterPattern))
                    .hasNoNullFieldsOrProperties()
                    .hasFieldOrPropertyWithValue("key", expectedKey)
                    .hasFieldOrPropertyWithValue("operator", expectedOperator)
                    .hasFieldOrPropertyWithValue("value", expectedValue);
            }

            public static Stream<String> fromInvalidPattern() {
                return Stream.of(
                    "key<foo", "key>bar", "key>=baz", "key<=boom", "key< 1234", "key> 1234", "key<= 1234", "key>= 1234", "contentType=application/xml;charset=UTF-8"
                );
            }

            @MethodSource
            @ParameterizedTest
            void fromInvalidPattern(String filterPattern) {
                assertThatThrownBy(() -> ScenarioExecutionQueryService.MessageHeaderFilter.fromFilterPattern(filterPattern))
                    .isInstanceOf(ScenarioExecutionQueryService.InvalidPatternException.class)
                    .hasMessage(format("The header filter pattern '%s' does not comply with the regex '^\\w?(([\\w-]+)[=~]?[ \\w,/.:()-]*|([\\w-]+)[<>]=?\\d+)$'!", filterPattern));
            }
        }

        @Nested
        class IsValueFilterOnly {

            @Test
            void withoutKeyAndOperatorIsValueOnly() {
                var fixture = new MessageHeaderFilter(null, null, "value");

                assertThat(fixture)
                    .extracting(MessageHeaderFilter::isValueFilterOnly)
                    .asInstanceOf(InstanceOfAssertFactories.BOOLEAN)
                    .isTrue();
            }

            @Test
            void withOperatorIsNotValueOnly() {
                var fixture = new MessageHeaderFilter(null, EQUALS, "value");

                assertThat(fixture)
                    .extracting(MessageHeaderFilter::isValueFilterOnly)
                    .asInstanceOf(InstanceOfAssertFactories.BOOLEAN)
                    .isFalse();
            }

            @Test
            void withKeyIsNotValueOnly() {
                var fixture = new MessageHeaderFilter("key", null, "value");

                assertThat(fixture)
                    .extracting(MessageHeaderFilter::isValueFilterOnly)
                    .asInstanceOf(InstanceOfAssertFactories.BOOLEAN)
                    .isFalse();
            }
        }
    }
}
