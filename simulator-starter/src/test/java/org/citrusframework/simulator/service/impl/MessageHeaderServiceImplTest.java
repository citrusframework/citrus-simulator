package org.citrusframework.simulator.service.impl;

import org.citrusframework.simulator.model.MessageHeader;
import org.citrusframework.simulator.repository.MessageHeaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageHeaderServiceImplTest {

    @Mock
    private MessageHeaderRepository messageHeaderRepositoryMock;

    private MessageHeaderServiceImpl fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new MessageHeaderServiceImpl(messageHeaderRepositoryMock);
    }

    @Test
    void testSave() {
        MessageHeader messageHeader = new MessageHeader();

        when(messageHeaderRepositoryMock.save(messageHeader)).thenReturn(messageHeader);

        MessageHeader savedMessageHeader = fixture.save(messageHeader);
        assertEquals(messageHeader, savedMessageHeader);
    }

    @Test
    void testFindAll() {
        Pageable pageable = Pageable.unpaged();
        Page<MessageHeader> mockPage = mock(Page.class);

        when(messageHeaderRepositoryMock.findAll(pageable)).thenReturn(mockPage);

        Page<MessageHeader> result = fixture.findAll(pageable);

        assertEquals(mockPage, result);
    }

    @Test
    void testFindAllWithEagerRelationships() {
        Pageable pageable = Pageable.unpaged();
        Page<MessageHeader> mockPage = mock(Page.class);

        when(messageHeaderRepositoryMock.findAllWithEagerRelationships(pageable)).thenReturn(mockPage);

        Page<MessageHeader> result = fixture.findAllWithEagerRelationships(pageable);

        assertEquals(mockPage, result);
    }

    @Test
    void testFindOne() {
        Long messageId = 1L;
        MessageHeader messageHeader = new MessageHeader();

        when(messageHeaderRepositoryMock.findOneWithEagerRelationships(messageId)).thenReturn(Optional.of(messageHeader));

        Optional<MessageHeader> maybeMessageHeader = fixture.findOne(messageId);

        assertTrue(maybeMessageHeader.isPresent());
        assertEquals(messageHeader, maybeMessageHeader.get());
    }
}
