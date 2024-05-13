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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

class IntegerFilterTest {

    @Test
    void testConstructor() {
        IntegerFilter filter = new IntegerFilter();
        assertNull(filter.getEquals());
        assertNull(filter.getNotEquals());
        assertNull(filter.getGreaterThan());
        assertNull(filter.getLessThan());
        assertNull(filter.getGreaterThanOrEqual());
        assertNull(filter.getLessThanOrEqual());
        assertNull(filter.getIn());
        assertNull(filter.getNotIn());
    }

    @Test
    void testCopyConstructor() {
        IntegerFilter original = new IntegerFilter();
        original.setEquals(1);
        original.setNotEquals(2);
        original.setGreaterThan(3);
        original.setLessThan(4);
        original.setGreaterThanOrEqual(5);
        original.setLessThanOrEqual(6);
        original.setIn(Arrays.asList(7, 8));
        original.setNotIn(Arrays.asList(9, 10));

        IntegerFilter copy = new IntegerFilter(original);

        assertEquals(original, copy);
        assertNotSame(original.getIn(), copy.getIn());
        assertNotSame(original.getNotIn(), copy.getNotIn());
    }

    @Test
    void testCopy() {
        IntegerFilter original = new IntegerFilter();
        original.setEquals(1);
        IntegerFilter copy = original.copy();

        assertEquals(original, copy);
        assertNotSame(original, copy);
    }
}
