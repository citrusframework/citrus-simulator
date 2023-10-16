package org.citrusframework.simulator.service.filter;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstantFilterTest {

    @Test
    void testConstructor() {
        InstantFilter filter = new InstantFilter();
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
        InstantFilter original = new InstantFilter();
        Instant now = Instant.now();
        original.setEquals(now);
        original.setNotEquals(now.plusSeconds(10));
        original.setGreaterThan(now.plusSeconds(20));
        original.setLessThan(now.plusSeconds(30));
        original.setGreaterThanOrEqual(now.plusSeconds(40));
        original.setLessThanOrEqual(now.plusSeconds(50));
        original.setIn(Arrays.asList(now, now.plusSeconds(60)));
        original.setNotIn(Arrays.asList(now.plusSeconds(70), now.plusSeconds(80)));

        InstantFilter copy = new InstantFilter(original);

        assertEquals(original, copy);
        assertNotSame(original.getIn(), copy.getIn());
        assertNotSame(original.getNotIn(), copy.getNotIn());
    }

    @Test
    void testCopy() {
        InstantFilter original = new InstantFilter();
        Instant now = Instant.now();
        original.setEquals(now);
        InstantFilter copy = original.copy();

        assertEquals(original, copy);
        assertNotSame(original, copy);
    }

    @Test
    void testSetters() {
        InstantFilter filter = new InstantFilter();
        Instant now = Instant.now();
        List<Instant> testList = Arrays.asList(now, now.plusSeconds(10));

        assertTrue(filter.setEquals(now) instanceof InstantFilter);
        assertTrue(filter.setNotEquals(now) instanceof InstantFilter);
        assertTrue(filter.setGreaterThan(now) instanceof InstantFilter);
        assertTrue(filter.setLessThan(now) instanceof InstantFilter);
        assertTrue(filter.setGreaterThanOrEqual(now) instanceof InstantFilter);
        assertTrue(filter.setLessThanOrEqual(now) instanceof InstantFilter);
        assertTrue(filter.setIn(testList) instanceof InstantFilter);
        assertTrue(filter.setNotIn(testList) instanceof InstantFilter);
    }
}
