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

class MessageCriteriaTest {

    private MessageCriteria fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new MessageCriteria();
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
    void testDirection() {
        assertNull(fixture.getDirection());

        IntegerFilter directionFilter = fixture.direction();
        assertNotNull(directionFilter);
        assertSame(directionFilter, fixture.getDirection());

        IntegerFilter mockDirectionFilter = mock(IntegerFilter.class);
        fixture.setDirection(mockDirectionFilter);
        assertSame(mockDirectionFilter, fixture.direction());
    }

    @Test
    void testPayload() {
        assertNull(fixture.getPayload());

        StringFilter payloadFilter = fixture.payload();
        assertNotNull(payloadFilter);
        assertSame(payloadFilter, fixture.getPayload());

        StringFilter mockPayloadFilter = mock(StringFilter.class);
        fixture.setPayload(mockPayloadFilter);
        assertSame(mockPayloadFilter, fixture.payload());
    }

    @Test
    void testCitrusMessageId() {
        assertNull(fixture.getCitrusMessageId());

        StringFilter citrusMessageIdFilter = fixture.citrusMessageId();
        assertNotNull(citrusMessageIdFilter);
        assertSame(citrusMessageIdFilter, fixture.getCitrusMessageId());

        StringFilter mockCitrusMessageIdFilter = mock(StringFilter.class);
        fixture.setCitrusMessageId(mockCitrusMessageIdFilter);
        assertSame(mockCitrusMessageIdFilter, fixture.citrusMessageId());
    }

    @Test
    void testHeadersId() {
        assertNull(fixture.getHeadersId());

        LongFilter headersIdFilter = fixture.headersId();
        assertNotNull(headersIdFilter);
        assertSame(headersIdFilter, fixture.getHeadersId());

        LongFilter mockHeadersIdFilter = mock(LongFilter.class);
        fixture.setHeadersId(mockHeadersIdFilter);
        assertSame(mockHeadersIdFilter, fixture.headersId());
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
    void testDistinct() {
        assertNull(fixture.getDistinct());

        fixture.setDistinct(true);
        assertTrue(fixture.getDistinct());
    }

    @Test
    void testCopy() {
        MessageCriteria copiedCriteria = fixture.copy();
        assertNotSame(copiedCriteria, fixture);
        assertEquals(copiedCriteria, fixture);
    }
}
