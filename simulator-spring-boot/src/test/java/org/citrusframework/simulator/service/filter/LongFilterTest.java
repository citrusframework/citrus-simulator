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
