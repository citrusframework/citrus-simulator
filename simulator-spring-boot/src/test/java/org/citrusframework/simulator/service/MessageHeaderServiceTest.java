package org.citrusframework.simulator.service;

import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.MessageHeader;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith({MockitoExtension.class})
class MessageHeaderServiceTest {

    @Mock
    private EntityManager entityManagerMock;

    @Nested
    class RestrictToDtoProperties {

        @Test
        void shouldFilterMessageDetails() {
            var message = mock(Message.class);
            doReturn(1234L).when(message).getMessageId();
            doReturn("citrus-message-id").when(message).getCitrusMessageId();

            var messageHeader = new MessageHeader();
            messageHeader.setMessage(message);

            var restrictedMessageHeader = MessageHeaderService.restrictToDtoProperties(messageHeader, entityManagerMock);

            var restrictedMessage = restrictedMessageHeader.getMessage();
            assertEquals(message.getMessageId(), restrictedMessage.getMessageId());
            assertEquals(message.getCitrusMessageId(), restrictedMessage.getCitrusMessageId());

            verify(message, never()).getHeaders();
            verify(message, never()).getScenarioExecution();

            verify(entityManagerMock).detach(messageHeader);
        }

        @Test
        void shouldHandleNullMessage() {
            var messageHeader = new MessageHeader();

            var restrictedMessageHeader = MessageHeaderService.restrictToDtoProperties(messageHeader, entityManagerMock);

            assertNull(restrictedMessageHeader.getMessage());

            verifyNoInteractions(entityManagerMock);
        }
    }
}
