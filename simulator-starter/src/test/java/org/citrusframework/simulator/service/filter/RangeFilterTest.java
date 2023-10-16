package org.citrusframework.simulator.service.filter;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RangeFilterTest {

    @Test
    void testConstructor() {
        RangeFilter<String> filter = new RangeFilter<>();
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
        RangeFilter<String> original = new RangeFilter<>();
        original.setEquals("testEquals");
        original.setNotEquals("testNotEquals");
        original.setGreaterThan("testGreaterThan");
        original.setLessThan("testLessThan");
        original.setGreaterThanOrEqual("testGreaterThanOrEqual");
        original.setLessThanOrEqual("testLessThanOrEqual");
        original.setIn(Arrays.asList("test1", "test2"));
        original.setNotIn(Arrays.asList("test3", "test4"));

        RangeFilter<String> copy = new RangeFilter<>(original);

        assertEquals(original, copy);
        assertNotSame(original.getIn(), copy.getIn());
        assertNotSame(original.getNotIn(), copy.getNotIn());
    }

    @Test
    void testGettersAndSetters() {
        RangeFilter<String> filter = new RangeFilter<>();
        String testValue = "testValue";
        List<String> testList = Arrays.asList("test1", "test2");

        filter.setEquals(testValue);
        assertEquals(testValue, filter.getEquals());

        filter.setNotEquals(testValue);
        assertEquals(testValue, filter.getNotEquals());

        filter.setGreaterThan(testValue);
        assertEquals(testValue, filter.getGreaterThan());

        filter.setLessThan(testValue);
        assertEquals(testValue, filter.getLessThan());

        filter.setGreaterThanOrEqual(testValue);
        assertEquals(testValue, filter.getGreaterThanOrEqual());

        filter.setLessThanOrEqual(testValue);
        assertEquals(testValue, filter.getLessThanOrEqual());

        filter.setIn(testList);
        assertEquals(testList, filter.getIn());

        filter.setNotIn(testList);
        assertEquals(testList, filter.getNotIn());
    }

    @Test
    void testEqualsAndHashCode() {
        RangeFilter<String> filter1 = new RangeFilter<>();
        RangeFilter<String> filter2 = new RangeFilter<>();

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
        RangeFilter<String> filter = new RangeFilter<>();
        filter.setEquals("testEquals");
        assertTrue(filter.toString().contains("testEquals"));
    }

    @Test
    void testCopy() {
        RangeFilter<String> original = new RangeFilter<>();
        original.setEquals("testEquals");
        RangeFilter<String> copy = original.copy();

        assertEquals(original, copy);
        assertNotSame(original, copy);
    }
}
