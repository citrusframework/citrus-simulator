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

class StringFilterTest {

    @Test
    void testConstructor() {
        StringFilter filter = new StringFilter();
        assertNull(filter.getEquals());
        assertNull(filter.getNotEquals());
        assertNull(filter.getIn());
        assertNull(filter.getNotIn());
        assertNull(filter.getContains());
        assertNull(filter.getDoesNotContain());
    }

    @Test
    void testCopyConstructor() {
        StringFilter original = new StringFilter();
        original.setEquals("testEquals");
        original.setNotEquals("testNotEquals");
        original.setIn(Arrays.asList("test1", "test2"));
        original.setNotIn(Arrays.asList("test3", "test4"));
        original.setContains("testContains");
        original.setDoesNotContain("testDoesNotContain");

        StringFilter copy = new StringFilter(original);

        assertEquals(original, copy);
        assertNotSame(original.getIn(), copy.getIn());
        assertNotSame(original.getNotIn(), copy.getNotIn());
    }

    @Test
    void testGettersAndSetters() {
        StringFilter filter = new StringFilter();
        String testValue = "testValue";
        List<String> testList = Arrays.asList("test1", "test2");

        filter.setEquals(testValue);
        assertEquals(testValue, filter.getEquals());

        filter.setNotEquals(testValue);
        assertEquals(testValue, filter.getNotEquals());

        filter.setIn(testList);
        assertEquals(testList, filter.getIn());

        filter.setNotIn(testList);
        assertEquals(testList, filter.getNotIn());

        filter.setContains(testValue);
        assertEquals(testValue, filter.getContains());

        filter.setDoesNotContain(testValue);
        assertEquals(testValue, filter.getDoesNotContain());
    }

    @Test
    void testCopy() {
        StringFilter original = new StringFilter();
        original.setEquals("testEquals");
        StringFilter copy = original.copy();

        assertEquals(original, copy);
        assertNotSame(original, copy);
    }

    @Test
    void testEqualsAndHashCode() {
        StringFilter filter1 = new StringFilter();
        StringFilter filter2 = new StringFilter();

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
        StringFilter filter = new StringFilter();
        filter.setEquals("testEquals");
        assertTrue(filter.toString().contains("testEquals"));
    }
}
