package org.citrusframework.simulator.service.criteria;

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

class MessageHeaderCriteriaTest {

    private MessageHeaderCriteria fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new MessageHeaderCriteria();
    }

    @Test
    void testHeaderId() {
        assertNull(fixture.getHeaderId());

        LongFilter headerIdFilter = fixture.headerId();
        assertNotNull(headerIdFilter);
        assertSame(headerIdFilter, fixture.getHeaderId());

        LongFilter mockHeaderIdFilter = mock(LongFilter.class);
        fixture.setHeaderId(mockHeaderIdFilter);
        assertSame(mockHeaderIdFilter, fixture.headerId());
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
    void testMessageId() {
        assertNull(fixture.getMessageId());

        LongFilter messageIdFilter = fixture.messageId();
        assertNotNull(messageIdFilter);
        assertSame(messageIdFilter, fixture.getMessageId());

        LongFilter mockMessageIdFilter = mock(LongFilter.class);
        fixture.setMessageId(mockMessageIdFilter);
        assertSame(mockMessageIdFilter, fixture.messageId());
    }

    @Test
    void testDistinct() {
        assertNull(fixture.getDistinct());

        fixture.setDistinct(true);
        assertTrue(fixture.getDistinct());
    }

    @Test
    void testCopy() {
        MessageHeaderCriteria copiedCriteria = fixture.copy();
        assertNotSame(copiedCriteria, fixture);
        assertEquals(copiedCriteria, fixture);
    }
}
