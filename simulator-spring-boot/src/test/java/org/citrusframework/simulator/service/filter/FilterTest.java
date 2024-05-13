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

package org.citrusframework.simulator.service.filter;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilterTest {

    @Test
    void testConstructor() {
        Filter<String> filter = new Filter<>();
        assertNull(filter.getEquals());
        assertNull(filter.getNotEquals());
        assertNull(filter.getSpecified());
        assertNull(filter.getIn());
        assertNull(filter.getNotIn());
    }

    @Test
    void testCopyConstructor() {
        Filter<String> original = new Filter<>();
        original.setEquals("testEquals");
        original.setNotEquals("testNotEquals");
        original.setSpecified(true);
        original.setIn(Arrays.asList("test1", "test2"));
        original.setNotIn(Arrays.asList("test3", "test4"));

        Filter<String> copy = new Filter<>(original);

        assertEquals(original, copy);
        assertNotSame(original.getIn(), copy.getIn());
        assertNotSame(original.getNotIn(), copy.getNotIn());
    }

    @Test
    void testGettersAndSetters() {
        Filter<String> filter = new Filter<>();
        String testValue = "testValue";
        List<String> testList = Arrays.asList("test1", "test2");

        filter.setEquals(testValue);
        assertEquals(testValue, filter.getEquals());

        filter.setNotEquals(testValue);
        assertEquals(testValue, filter.getNotEquals());

        filter.setSpecified(true);
        assertTrue(filter.getSpecified());

        filter.setIn(testList);
        assertEquals(testList, filter.getIn());

        filter.setNotIn(testList);
        assertEquals(testList, filter.getNotIn());
    }

    @Test
    void testCopy() {
        Filter<String> original = new Filter<>();
        original.setEquals("testEquals");
        Filter<String> copy = original.copy();

        assertEquals(original, copy);
        assertNotSame(original, copy);
    }

    @Test
    void testEqualsAndHashCode() {
        Filter<String> filter1 = new Filter<>();
        Filter<String> filter2 = new Filter<>();

        assertEquals(filter1, filter2);
        assertEquals(filter1.hashCode(), filter2.hashCode());

        filter1.setEquals("test");
        assertNotEquals(filter1, filter2);

        filter2.setEquals("test");
        assertEquals(filter1, filter2);
        assertEquals(filter1.hashCode(), filter2.hashCode());
    }

    @Test
    void testToString() {
        Filter<String> filter = new Filter<>();
        filter.setEquals("testEquals");
        assertTrue(filter.toString().contains("testEquals"));
    }
}
