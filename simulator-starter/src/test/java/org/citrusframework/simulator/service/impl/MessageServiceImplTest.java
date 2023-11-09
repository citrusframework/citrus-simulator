package org.citrusframework.simulator.service.impl;

import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.repository.MessageRepository;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepositoryMock;

    @Mock
    private ScenarioExecutionService scenarioExecutionServiceMock;

    private MessageServiceImpl fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new MessageServiceImpl(messageRepositoryMock, scenarioExecutionServiceMock);
    }

    @Test
    void testSave() {
        Message message = new Message();

        when(messageRepositoryMock.save(message)).thenReturn(message);

        Message savedMessage = fixture.save(message);
        assertEquals(message, savedMessage);
    }

    @Test
    void testFindAll() {
        Pageable pageable = Pageable.unpaged();
        Page<Message> mockPage = mock(Page.class);

        when(messageRepositoryMock.findAllWithEagerRelationships(pageable)).thenReturn(mockPage);

        Page<Message> result = fixture.findAll(pageable);

        assertEquals(mockPage, result);
    }

    @Test
    void testFindOne() {
        Long messageId = 1L;
        Message message = new Message();

        when(messageRepositoryMock.findOneWithEagerRelationships(messageId)).thenReturn(Optional.of(message));

        Optional<Message> maybeMessage = fixture.findOne(messageId);

        assertTrue(maybeMessage.isPresent());
        assertEquals(message, maybeMessage.get());
    }

    @Nested
    class AttachMessageToScenarioExecutionAndSave {

        @Test
        void persistsMessageInCorrelationWithScenarioExecution() {
            Long scenarioExecutionId = 1234L;
            Message.Direction direction = Message.Direction.INBOUND;
            String citrusMessageId = "citrusMessageId";

            doReturn(Collections.emptyList())
                .when(messageRepositoryMock)
                .findAllForScenarioExecution(scenarioExecutionId, citrusMessageId, direction);

            Long messageId = 1234L;
            AtomicReference<Message> newMessage = new AtomicReference<>(null);

            doAnswer(invocationOnMock -> {
                Message message = invocationOnMock.getArgument(0, Message.class);
                newMessage.set(message);
                ReflectionTestUtils.setField(message, "messageId", messageId, Long.class);
                return message;
            })
                .when(messageRepositoryMock)
                .save(any(Message.class));

            ScenarioExecution scenarioExecutionSpy = spy(new ScenarioExecution());
            doReturn(Optional.of(scenarioExecutionSpy))
                .when(scenarioExecutionServiceMock)
                .findOne(scenarioExecutionId);

            Message result = fixture.attachMessageToScenarioExecutionAndSave(scenarioExecutionId, direction, "payload", citrusMessageId, Collections.emptyMap());

            assertEquals(messageId, result.getMessageId());

            verify(scenarioExecutionSpy).addScenarioMessage(newMessage.get());
            verify(scenarioExecutionServiceMock).save(scenarioExecutionSpy);
        }

        @Test
        void persistsMessageForMissingScenarioExecution() {
            Long scenarioExecutionId = 1234L;
            Message.Direction direction = Message.Direction.INBOUND;
            String citrusMessageId = "citrusMessageId";

            doReturn(Collections.emptyList())
                .when(messageRepositoryMock)
                .findAllForScenarioExecution(scenarioExecutionId, citrusMessageId, direction);
            doAnswer(returnsFirstArg()).when(messageRepositoryMock).save(any(Message.class));

            // Explicit declaration of invocation, although this is not strictly necessary
            doReturn(Optional.empty())
                .when(scenarioExecutionServiceMock)
                .findOne(scenarioExecutionId);

            Map<String, Object> headers = Collections.emptyMap();
            CitrusRuntimeException exception = assertThrows(
                CitrusRuntimeException.class,
                () -> fixture.attachMessageToScenarioExecutionAndSave(scenarioExecutionId, direction, "payload", citrusMessageId, headers)
            );

            assertEquals(
                String.format(
                    "Error while attaching Message to ScenarioExecution %s: Did not find corresponding ScenarioExecution!",
                    scenarioExecutionId),
                exception.getMessage()
            );
        }

        @Test
        void doesNothingIfMessageExists() {
            Long scenarioExecutionId = 1234L;
            Message.Direction direction = Message.Direction.INBOUND;
            String citrusMessageId = "citrusMessageId";

            Message existingMessage = new Message();
            doReturn(List.of(existingMessage))
                .when(messageRepositoryMock)
                .findAllForScenarioExecution(scenarioExecutionId, citrusMessageId, direction);

            Message result = fixture.attachMessageToScenarioExecutionAndSave(scenarioExecutionId, direction, "payload", citrusMessageId, Collections.emptyMap());
            assertEquals(existingMessage, result);
        }
    }
}
