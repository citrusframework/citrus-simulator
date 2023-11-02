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

class ScenarioExecutionCriteriaTest {

    private ScenarioExecutionCriteria fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new ScenarioExecutionCriteria();
    }

    @Test
    void testExecutionId() {
        assertNull(fixture.getExecutionId());

        LongFilter executionIdFilter = fixture.id();
        assertNotNull(executionIdFilter);
        assertSame(executionIdFilter, fixture.getExecutionId());

        LongFilter mockExecutionIdFilter = mock(LongFilter.class);
        fixture.setExecutionId(mockExecutionIdFilter);
        assertSame(mockExecutionIdFilter, fixture.id());
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

    // Add more test methods following the same pattern for all the other attributes...

    @Test
    void testScenarioName() {
        assertNull(fixture.getScenarioName());

        StringFilter scenarioNameFilter = fixture.scenarioName();
        assertNotNull(scenarioNameFilter);
        assertSame(scenarioNameFilter, fixture.getScenarioName());

        StringFilter mockScenarioNameFilter = mock(StringFilter.class);
        fixture.setScenarioName(mockScenarioNameFilter);
        assertSame(mockScenarioNameFilter, fixture.scenarioName());
    }

    @Test
    void testStatus() {
        assertNull(fixture.getStatus());

        IntegerFilter statusFilter = fixture.status();
        assertNotNull(statusFilter);
        assertSame(statusFilter, fixture.getStatus());

        IntegerFilter mockStatusFilter = mock(IntegerFilter.class);
        fixture.setStatus(mockStatusFilter);
        assertSame(mockStatusFilter, fixture.status());
    }

    @Test
    void testErrorMessage() {
        assertNull(fixture.getErrorMessage());

        StringFilter errorMessageFilter = fixture.errorMessage();
        assertNotNull(errorMessageFilter);
        assertSame(errorMessageFilter, fixture.getErrorMessage());

        StringFilter mockErrorMessageFilter = mock(StringFilter.class);
        fixture.setErrorMessage(mockErrorMessageFilter);
        assertSame(mockErrorMessageFilter, fixture.errorMessage());
    }

    @Test
    void testScenarioActionsId() {
        assertNull(fixture.getScenarioActionsId());

        LongFilter scenarioActionsIdFilter = fixture.scenarioActionsId();
        assertNotNull(scenarioActionsIdFilter);
        assertSame(scenarioActionsIdFilter, fixture.getScenarioActionsId());

        LongFilter mockScenarioActionsIdFilter = mock(LongFilter.class);
        fixture.setScenarioActionsId(mockScenarioActionsIdFilter);
        assertSame(mockScenarioActionsIdFilter, fixture.scenarioActionsId());
    }

    @Test
    void testScenarioMessagesId() {
        assertNull(fixture.getScenarioMessagesId());

        LongFilter scenarioMessagesIdFilter = fixture.scenarioMessagesId();
        assertNotNull(scenarioMessagesIdFilter);
        assertSame(scenarioMessagesIdFilter, fixture.getScenarioMessagesId());

        LongFilter mockScenarioMessagesIdFilter = mock(LongFilter.class);
        fixture.setScenarioMessagesId(mockScenarioMessagesIdFilter);
        assertSame(mockScenarioMessagesIdFilter, fixture.scenarioMessagesId());
    }

    @Test
    void testScenarioParametersId() {
        assertNull(fixture.getScenarioParametersId());

        LongFilter scenarioParametersIdFilter = fixture.scenarioParametersId();
        assertNotNull(scenarioParametersIdFilter);
        assertSame(scenarioParametersIdFilter, fixture.getScenarioParametersId());

        LongFilter mockScenarioParametersIdFilter = mock(LongFilter.class);
        fixture.setScenarioParametersId(mockScenarioParametersIdFilter);
        assertSame(mockScenarioParametersIdFilter, fixture.scenarioParametersId());
    }

    @Test
    void testDistinct() {
        assertNull(fixture.getDistinct());

        fixture.setDistinct(true);
        assertTrue(fixture.getDistinct());
    }

    @Test
    void testCopy() {
        ScenarioExecutionCriteria copiedCriteria = fixture.copy();
        assertNotSame(copiedCriteria, fixture);
        assertEquals(copiedCriteria, fixture);
    }
}
