package org.citrusframework.simulator.service.criteria;

import org.citrusframework.simulator.service.filter.InstantFilter;
import org.citrusframework.simulator.service.filter.LongFilter;
import org.citrusframework.simulator.service.filter.StringFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

class TestParameterCriteriaTest {

    private TestParameterCriteria criteria;

    @BeforeEach
    void beforeEachSetup() {
        criteria = new TestParameterCriteria();
    }

    @Test
    void testKey() {
        assertNull(criteria.getKey());

        StringFilter keyFilter = criteria.key();
        assertNotNull(keyFilter);
        assertSame(keyFilter, criteria.getKey());

        StringFilter mockKeyFilter = mock(StringFilter.class);
        criteria.setKey(mockKeyFilter);
        assertSame(mockKeyFilter, criteria.key());
    }

    @Test
    void testValue() {
        assertNull(criteria.getValue());

        StringFilter valueFilter = criteria.value();
        assertNotNull(valueFilter);
        assertSame(valueFilter, criteria.getValue());

        StringFilter mockValueFilter = mock(StringFilter.class);
        criteria.setValue(mockValueFilter);
        assertSame(mockValueFilter, criteria.value());
    }

    @Test
    void testCreatedDate() {
        assertNull(criteria.getCreatedDate());

        InstantFilter createdDateFilter = criteria.createdDate();
        assertNotNull(createdDateFilter);
        assertSame(createdDateFilter, criteria.getCreatedDate());

        InstantFilter mockCreatedDateFilter = mock(InstantFilter.class);
        criteria.setCreatedDate(mockCreatedDateFilter);
        assertSame(mockCreatedDateFilter, criteria.createdDate());
    }

    @Test
    void testLastModifiedDate() {
        assertNull(criteria.getLastModifiedDate());

        InstantFilter lastModifiedDateFilter = criteria.lastModifiedDate();
        assertNotNull(lastModifiedDateFilter);
        assertSame(lastModifiedDateFilter, criteria.getLastModifiedDate());

        InstantFilter mockLastModifiedDateFilter = mock(InstantFilter.class);
        criteria.setLastModifiedDate(mockLastModifiedDateFilter);
        assertSame(mockLastModifiedDateFilter, criteria.lastModifiedDate());
    }

    @Test
    void testTestResultId() {
        assertNull(criteria.getTestResultId());

        LongFilter testResultIdFilter = criteria.testResultId();
        assertNotNull(testResultIdFilter);
        assertSame(testResultIdFilter, criteria.getTestResultId());

        LongFilter mockTestResultIdFilter = mock(LongFilter.class);
        criteria.setTestResultId(mockTestResultIdFilter);
        assertSame(mockTestResultIdFilter, criteria.testResultId());
    }

    @Test
    void testCopy() {
        TestParameterCriteria copiedCriteria = criteria.copy();
        assertNotSame(copiedCriteria, criteria);
        assertEquals(copiedCriteria, criteria);
    }
}
