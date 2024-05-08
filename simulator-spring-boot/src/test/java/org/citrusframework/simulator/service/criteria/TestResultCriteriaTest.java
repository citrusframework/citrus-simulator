package org.citrusframework.simulator.service.criteria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class TestResultCriteriaTest {

    private TestResultCriteria criteria;

    @BeforeEach
    void beforeEachSetup() {
        criteria = new TestResultCriteria();
    }

    @Test
    void copyShouldBeEqualObject() {
        TestResultCriteria copiedCriteria = criteria.copy();
        assertNotSame(copiedCriteria, criteria);
        assertEquals(copiedCriteria, criteria);
    }
}
