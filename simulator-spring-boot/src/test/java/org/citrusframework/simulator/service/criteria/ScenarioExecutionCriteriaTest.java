package org.citrusframework.simulator.service.criteria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class ScenarioExecutionCriteriaTest {

    private ScenarioExecutionCriteria fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new ScenarioExecutionCriteria();
    }

    @Test
    void copyShouldBeEqualObject() {
        ScenarioExecutionCriteria copiedCriteria = fixture.copy();
        assertNotSame(copiedCriteria, fixture);
        assertEquals(copiedCriteria, fixture);
    }
}
