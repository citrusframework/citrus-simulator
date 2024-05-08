package org.citrusframework.simulator.service.criteria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class MessageCriteriaTest {

    private MessageCriteria fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new MessageCriteria();
    }

    @Test
    void copyShouldBeEqualObject() {
        MessageCriteria copiedCriteria = fixture.copy();
        assertNotSame(copiedCriteria, fixture);
        assertEquals(copiedCriteria, fixture);
    }
}
