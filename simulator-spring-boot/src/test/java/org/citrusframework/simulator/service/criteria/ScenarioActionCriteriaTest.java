package org.citrusframework.simulator.service.criteria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class ScenarioActionCriteriaTest {

    private ScenarioActionCriteria fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new ScenarioActionCriteria();
    }

    @Test
    void copyShouldBeEqualObject() {
        ScenarioActionCriteria copiedCriteria = fixture.copy();
        assertNotSame(copiedCriteria, fixture);
        assertEquals(copiedCriteria, fixture);
    }
}
