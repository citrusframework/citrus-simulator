package org.citrusframework.simulator.service.criteria;

import org.citrusframework.simulator.service.filter.InstantFilter;
import org.citrusframework.simulator.service.filter.IntegerFilter;
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

class TestResultCriteriaTest {

    private TestResultCriteria criteria;

    @BeforeEach
    void beforeEachSetup() {
        criteria = new TestResultCriteria();
    }

    @Test
    void testId() {
        assertNull(criteria.getId());

        LongFilter idFilter = criteria.id();
        assertNotNull(idFilter);
        assertSame(idFilter, criteria.getId());

        LongFilter mockIdFilter = mock(LongFilter.class);
        criteria.setId(mockIdFilter);
        assertSame(mockIdFilter, criteria.id());
    }

    @Test
    void testStatus() {
        assertNull(criteria.getStatus());

        IntegerFilter statusFilter = criteria.status();
        assertNotNull(statusFilter);
        assertSame(statusFilter, criteria.getStatus());

        IntegerFilter mockStatusFilter = mock(IntegerFilter.class);
        criteria.setStatus(mockStatusFilter);
        assertSame(mockStatusFilter, criteria.status());
    }

    @Test
    void testTestName() {
        assertNull(criteria.getTestName());

        StringFilter testNameFilter = criteria.testName();
        assertNotNull(testNameFilter);
        assertSame(testNameFilter, criteria.getTestName());

        StringFilter mockTestNameFilter = mock(StringFilter.class);
        criteria.setTestName(mockTestNameFilter);
        assertSame(mockTestNameFilter, criteria.testName());
    }

    @Test
    void testClassName() {
        assertNull(criteria.getClassName());

        StringFilter classNameFilter = criteria.className();
        assertNotNull(classNameFilter);
        assertSame(classNameFilter, criteria.getClassName());

        StringFilter mockClassNameFilter = mock(StringFilter.class);
        criteria.setClassName(mockClassNameFilter);
        assertSame(mockClassNameFilter, criteria.className());
    }

    @Test
    void testErrorMessage() {
        assertNull(criteria.getErrorMessage());

        StringFilter errorMessageFilter = criteria.errorMessage();
        assertNotNull(errorMessageFilter);
        assertSame(errorMessageFilter, criteria.getErrorMessage());

        StringFilter mockErrorMessageFilter = mock(StringFilter.class);
        criteria.setErrorMessage(mockErrorMessageFilter);
        assertSame(mockErrorMessageFilter, criteria.errorMessage());
    }

    @Test
    void testStackTrace() {
        assertNull(criteria.getStackTrace());

        StringFilter stackTraceFilter = criteria.stackTrace();
        assertNotNull(stackTraceFilter);
        assertSame(stackTraceFilter, criteria.getStackTrace());

        StringFilter mockFailureStackFilter = mock(StringFilter.class);
        criteria.setStackTrace(mockFailureStackFilter);
        assertSame(mockFailureStackFilter, criteria.stackTrace());
    }

    @Test
    void testFailureType() {
        assertNull(criteria.getFailureType());

        StringFilter failureTypeFilter = criteria.failureType();
        assertNotNull(failureTypeFilter);
        assertSame(failureTypeFilter, criteria.getFailureType());

        StringFilter mockFailureTypeFilter = mock(StringFilter.class);
        criteria.setFailureType(mockFailureTypeFilter);
        assertSame(mockFailureTypeFilter, criteria.failureType());
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
    void testTestParameterKey() {
        assertNull(criteria.getTestParameterKey());

        StringFilter testParameterKeyFilter = criteria.testParameterId();
        assertNotNull(testParameterKeyFilter);
        assertSame(testParameterKeyFilter, criteria.getTestParameterKey());

        StringFilter mockTestParameterKeyFilter = mock(StringFilter.class);
        criteria.setTestParameterKey(mockTestParameterKeyFilter);
        assertSame(mockTestParameterKeyFilter, criteria.testParameterId());
    }

    @Test
    void testCopy() {
        TestResultCriteria copiedCriteria = criteria.copy();
        assertNotSame(copiedCriteria, criteria);
        assertEquals(copiedCriteria, criteria);
    }
}
