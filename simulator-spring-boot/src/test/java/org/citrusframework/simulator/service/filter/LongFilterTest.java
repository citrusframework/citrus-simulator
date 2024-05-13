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

class LongFilterTest {

    @Test
    void testConstructor() {
        LongFilter filter = new LongFilter();
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
        LongFilter original = new LongFilter();
        original.setEquals(1L);
        original.setNotEquals(2L);
        original.setGreaterThan(3L);
        original.setLessThan(4L);
        original.setGreaterThanOrEqual(5L);
        original.setLessThanOrEqual(6L);
        original.setIn(Arrays.asList(7L, 8L));
        original.setNotIn(Arrays.asList(9L, 10L));

        LongFilter copy = new LongFilter(original);

        assertEquals(original, copy);
        assertNotSame(original.getIn(), copy.getIn());
        assertNotSame(original.getNotIn(), copy.getNotIn());
    }

    @Test
    void testCopy() {
        LongFilter original = new LongFilter();
        original.setEquals(1L);
        LongFilter copy = original.copy();

        assertEquals(original, copy);
        assertNotSame(original, copy);
    }
}
