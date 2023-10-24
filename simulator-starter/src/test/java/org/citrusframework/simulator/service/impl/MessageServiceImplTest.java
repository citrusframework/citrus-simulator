package org.citrusframework.simulator.service.impl;

import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.repository.MessageRepository;
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
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepositoryMock;

    private MessageServiceImpl fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new MessageServiceImpl(messageRepositoryMock);
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
}
