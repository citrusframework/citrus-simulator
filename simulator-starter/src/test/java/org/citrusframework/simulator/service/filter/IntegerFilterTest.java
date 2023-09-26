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
