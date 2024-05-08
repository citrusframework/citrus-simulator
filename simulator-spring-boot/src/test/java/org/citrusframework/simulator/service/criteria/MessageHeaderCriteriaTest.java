package org.citrusframework.simulator.service.criteria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class MessageHeaderCriteriaTest {

    private MessageHeaderCriteria fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new MessageHeaderCriteria();
    }

    @Test
    void copyShouldBeEqualObject() {
        MessageHeaderCriteria copiedCriteria = fixture.copy();
        assertNotSame(copiedCriteria, fixture);
        assertEquals(copiedCriteria, fixture);
    }
}
