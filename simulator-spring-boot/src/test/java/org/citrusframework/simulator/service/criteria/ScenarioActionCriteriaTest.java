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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ScenarioActionCriteriaTest {

    private ScenarioActionCriteria fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new ScenarioActionCriteria();
    }

    @Test
    void testActionId() {
        assertNull(fixture.getActionId());

        LongFilter actionIdFilter = fixture.id();
        assertNotNull(actionIdFilter);
        assertSame(actionIdFilter, fixture.getActionId());

        LongFilter mockActionIdFilter = mock(LongFilter.class);
        fixture.setActionId(mockActionIdFilter);
        assertSame(mockActionIdFilter, fixture.id());
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
    void testStartDate() {
        assertNull(fixture.getStartDate());

        InstantFilter startDateFilter = fixture.startDate();
        assertNotNull(startDateFilter);
        assertSame(startDateFilter, fixture.getStartDate());

        InstantFilter mockStartDateFilter = mock(InstantFilter.class);
        fixture.setStartDate(mockStartDateFilter);
        assertSame(mockStartDateFilter, fixture.startDate());
    }

    @Test
    void testEndDate() {
        assertNull(fixture.getEndDate());

        InstantFilter endDateFilter = fixture.endDate();
        assertNotNull(endDateFilter);
        assertSame(endDateFilter, fixture.getEndDate());

        InstantFilter mockEndDateFilter = mock(InstantFilter.class);
        fixture.setEndDate(mockEndDateFilter);
        assertSame(mockEndDateFilter, fixture.endDate());
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
        ScenarioActionCriteria copiedCriteria = fixture.copy();
        assertNotSame(copiedCriteria, fixture);
        assertEquals(copiedCriteria, fixture);
    }
}
