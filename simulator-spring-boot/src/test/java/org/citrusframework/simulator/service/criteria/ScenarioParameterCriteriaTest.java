package org.citrusframework.simulator.service.criteria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class ScenarioParameterCriteriaTest {

    private ScenarioParameterCriteria fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new ScenarioParameterCriteria();
    }

    @Test
    void copyShouldBeEqualObject() {
        ScenarioParameterCriteria copiedCriteria = fixture.copy();
        assertNotSame(copiedCriteria, fixture);
        assertEquals(copiedCriteria, fixture);
    }
}
