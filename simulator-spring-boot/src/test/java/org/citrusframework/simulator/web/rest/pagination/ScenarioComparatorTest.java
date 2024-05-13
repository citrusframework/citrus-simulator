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

package org.citrusframework.simulator.web.rest.pagination;

import org.citrusframework.simulator.web.rest.ScenarioResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.citrusframework.simulator.web.rest.ScenarioResource.Scenario.ScenarioType.MESSAGE_TRIGGERED;
import static org.citrusframework.simulator.web.rest.ScenarioResource.Scenario.ScenarioType.STARTER;
import static org.citrusframework.simulator.web.rest.pagination.ScenarioComparator.fromProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class ScenarioComparatorTest {

    static Stream<String> existingComparators() {
        return Stream.of(
            "name", "type"
        );
    }

    @MethodSource
    @ParameterizedTest
    void existingComparators(String property) {
        var comparator = fromProperty(property);
        assertTrue(comparator.isPresent());
    }

    @Test
    void shouldReturnEmptyForInvalidProperty() {
        var comparator = fromProperty("invalid");
        assertFalse(comparator.isPresent());
    }

    @Test
    void shouldCompareScenariosByName() {
        var scenario1 = mock(ScenarioResource.Scenario.class);
        var scenario2 = mock(ScenarioResource.Scenario.class);

        doReturn("ScenarioA").when(scenario1).name();
        doReturn("ScenarioB").when(scenario2).name();

        var comparator = fromProperty("name").orElseThrow(IllegalArgumentException::new);

        assertTrue(comparator.compare(scenario1, scenario2) < 0);
    }

    @Test
    void shouldCompareScenariosByType() {
        var scenario1 = mock(ScenarioResource.Scenario.class);
        var scenario2 = mock(ScenarioResource.Scenario.class);

        doReturn(STARTER).when(scenario1).type();
        doReturn(MESSAGE_TRIGGERED).when(scenario2).type();

        var comparator = fromProperty("type").orElseThrow(IllegalArgumentException::new);

        assertTrue(comparator.compare(scenario1, scenario2) < 0);
    }

    @Test
    void shouldHandleNullValues() {
        var scenario1 = mock(ScenarioResource.Scenario.class);
        var scenario2 = mock(ScenarioResource.Scenario.class);

        doReturn(null).when(scenario1).name();
        doReturn("ScenarioB").when(scenario2).name();

        var comparator = fromProperty("name").orElseThrow(IllegalArgumentException::new);

        assertEquals(-1, comparator.compare(scenario1, scenario2));
        assertEquals(1, comparator.compare(scenario2, scenario1));
    }
}
