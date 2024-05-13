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

package org.citrusframework.simulator.service.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestResultByStatusTest {

    static Stream<Arguments> constructorCalculatesTotal() {
        return Stream.of(
            Arguments.of(0, 0, 0),
            Arguments.of(0, 1, 1),
            Arguments.of(1, 0, 1),
            Arguments.of(1, 2, 3),
            Arguments.of(2, 3, 5)
        );
    }

    @MethodSource
    @ParameterizedTest
    void constructorCalculatesTotal(int succeeded, int failed, int total) {
        assertEquals(total, new TestResultByStatus((long) succeeded, (long) failed).total());
    }

    @Test
    void constructorIsNullResistant() {
        TestResultByStatus testResultByStatus = new TestResultByStatus(null, null);
        assertEquals(0, testResultByStatus.total());
        assertEquals(0, testResultByStatus.successful());
        assertEquals(0, testResultByStatus.failed());
    }
}
