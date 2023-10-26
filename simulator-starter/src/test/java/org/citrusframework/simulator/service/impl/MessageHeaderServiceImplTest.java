package org.citrusframework.simulator.service.impl;

import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.MessageHeader;
import org.citrusframework.simulator.repository.MessageHeaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageHeaderServiceImplTest {

    @Mock
    private MessageHeaderRepository messageHeaderRepositoryMock;

    private MessageHeader messageHeaderWithMessage;

    private MessageHeaderServiceImpl fixture;

    @BeforeEach
    void beforeEachSetup() {
        MessageHeader messageHeader = new MessageHeader();
        Message message = Message.builder()
            .messageId(1234L)
            .citrusMessageId("citrus-message-id")
            .build();
        messageHeader.setMessage(message);
        messageHeaderWithMessage = spy(messageHeader);

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
        Page<MessageHeader> page = new PageImpl<>(List.of(messageHeaderWithMessage));

        when(messageHeaderRepositoryMock.findAllWithEagerRelationships(pageable)).thenReturn(page);

        Page<MessageHeader> result = fixture.findAll(pageable);

        assertEquals(page, result);

        verifyDtoPreparations();
    }

    @Test
    void testFindOne() {
        Long messageId = 1L;

        when(messageHeaderRepositoryMock.findOneWithEagerRelationships(messageId)).thenReturn(Optional.of(messageHeaderWithMessage));

        Optional<MessageHeader> maybeMessageHeader = fixture.findOne(messageId);

        assertTrue(maybeMessageHeader.isPresent());
        assertEquals(messageHeaderWithMessage, maybeMessageHeader.get());

        verifyDtoPreparations();
    }

    private void verifyDtoPreparations() {
        ArgumentCaptor<Message> messageHeaderArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageHeaderWithMessage).setMessage(messageHeaderArgumentCaptor.capture());

        Message expectedMessage = messageHeaderWithMessage.getMessage();
        Message capturedMessage = messageHeaderArgumentCaptor.getValue();

        assertEquals(expectedMessage.getMessageId(), capturedMessage.getMessageId());
        assertEquals(expectedMessage.getCitrusMessageId(), capturedMessage.getCitrusMessageId());
    }
}
