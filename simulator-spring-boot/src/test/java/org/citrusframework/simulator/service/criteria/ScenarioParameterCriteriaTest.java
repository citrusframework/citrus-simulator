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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ScenarioParameterCriteriaTest {

    private ScenarioParameterCriteria fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new ScenarioParameterCriteria();
    }

    @Test
    void testParameterId() {
        assertNull(fixture.getParameterId());

        LongFilter parameterIdFilter = fixture.id();
        assertNotNull(parameterIdFilter);
        assertSame(parameterIdFilter, fixture.getParameterId());

        LongFilter mockParameterIdFilter = mock(LongFilter.class);
        fixture.setParameterId(mockParameterIdFilter);
        assertSame(mockParameterIdFilter, fixture.id());
    }

    @Test
    void testName() {
        assertNull(fixture.getName());

        StringFilter nameFilter = fixture.name();
        assertNotNull(nameFilter);
        assertSame(nameFilter, fixture.getName());

        StringFilter mockNameFilter = mock(StringFilter.class);
        fixture.setName(mockNameFilter);
        assertSame(mockNameFilter, fixture.name());
    }

    @Test
    void testControlType() {
        assertNull(fixture.getControlType());

        IntegerFilter controlTypeFilter = fixture.controlType();
        assertNotNull(controlTypeFilter);
        assertSame(controlTypeFilter, fixture.getControlType());

        IntegerFilter mockControlTypeFilter = mock(IntegerFilter.class);
        fixture.setControlType(mockControlTypeFilter);
        assertSame(mockControlTypeFilter, fixture.controlType());
    }

    @Test
    void testValue() {
        assertNull(fixture.getValue());

        StringFilter valueFilter = fixture.value();
        assertNotNull(valueFilter);
        assertSame(valueFilter, fixture.getValue());

        StringFilter mockValueFilter = mock(StringFilter.class);
        fixture.setValue(mockValueFilter);
        assertSame(mockValueFilter, fixture.value());
    }

    @Test
    void testCreatedDate() {
        assertNull(fixture.getCreatedDate());

        InstantFilter createdDateFilter = fixture.createdDate();
        assertNotNull(createdDateFilter);
        assertSame(createdDateFilter, fixture.getCreatedDate());

        InstantFilter mockCreatedDateFilter = mock(InstantFilter.class);
        fixture.setCreatedDate(mockCreatedDateFilter);
        assertSame(mockCreatedDateFilter, fixture.createdDate());
    }

    @Test
    void testLastModifiedDate() {
        assertNull(fixture.getLastModifiedDate());

        InstantFilter lastModifiedDateFilter = fixture.lastModifiedDate();
        assertNotNull(lastModifiedDateFilter);
        assertSame(lastModifiedDateFilter, fixture.getLastModifiedDate());

        InstantFilter mockLastModifiedDateFilter = mock(InstantFilter.class);
        fixture.setLastModifiedDate(mockLastModifiedDateFilter);
        assertSame(mockLastModifiedDateFilter, fixture.lastModifiedDate());
    }

    @Test
    void testScenarioExecutionId() {
        assertNull(fixture.getScenarioExecutionId());

        LongFilter scenarioExecutionIdFilter = fixture.scenarioExecutionId();
        assertNotNull(scenarioExecutionIdFilter);
        assertSame(scenarioExecutionIdFilter, fixture.getScenarioExecutionId());

        LongFilter mockScenarioExecutionIdFilter = mock(LongFilter.class);
        fixture.setScenarioExecutionId(mockScenarioExecutionIdFilter);
        assertSame(mockScenarioExecutionIdFilter, fixture.scenarioExecutionId());
    }

    @Test
    void testDistinct() {
        assertNull(fixture.getDistinct());

        fixture.setDistinct(true);
        assertTrue(fixture.getDistinct());
    }

    @Test
    void testCopy() {
        ScenarioParameterCriteria copiedCriteria = fixture.copy();
        assertNotSame(copiedCriteria, fixture);
        assertEquals(copiedCriteria, fixture);
    }
}
