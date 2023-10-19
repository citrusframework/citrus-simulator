package org.citrusframework.simulator.web.rest;

import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.MessageHeader;
import org.citrusframework.simulator.repository.MessageHeaderRepository;
import org.citrusframework.simulator.service.MessageHeaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link MessageHeaderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class MessageHeaderResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/message-headers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private MessageHeaderRepository messageHeaderRepository;

    @Mock
    private MessageHeaderRepository messageHeaderRepositoryMock;

    @Mock
    private MessageHeaderService messageHeaderServiceMock;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc restMessageHeaderMockMvc;

    private MessageHeader messageHeader;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MessageHeader createEntity(EntityManager entityManager) {
        MessageHeader messageHeader = MessageHeader.builder()
            .name(DEFAULT_NAME)
            .value(DEFAULT_VALUE)
            .build();
        // Add required entity
        Message message;
        if (TestUtil.findAll(entityManager, Message.class).isEmpty()) {
            message = MessageResourceIT.createEntity(entityManager);
            entityManager.persist(message);
            entityManager.flush();
        } else {
            message = TestUtil.findAll(entityManager, Message.class).get(0);
        }
        messageHeader.setMessage(message);
        return messageHeader;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MessageHeader createUpdatedEntity(EntityManager entityManager) {
        MessageHeader messageHeader = MessageHeader.builder()
            .name(UPDATED_NAME)
            .value(UPDATED_VALUE)
            .build();
        // Add required entity
        Message message;
        if (TestUtil.findAll(entityManager, Message.class).isEmpty()) {
            message = MessageResourceIT.createUpdatedEntity(entityManager);
            entityManager.persist(message);
            entityManager.flush();
        } else {
            message = TestUtil.findAll(entityManager, Message.class).get(0);
        }
        messageHeader.setMessage(message);
        return messageHeader;
    }

    @BeforeEach
    void beforeEachSetup() {
        messageHeader = createEntity(entityManager);
    }

    @Test
    @Transactional
    void getAllMessageHeaders() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList
        restMessageHeaderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=headerId,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].headerId").value(hasItem(messageHeader.getHeaderId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMessageHeadersWithEagerRelationshipsIsEnabled() throws Exception {
        when(messageHeaderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMessageHeaderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(messageHeaderServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMessageHeadersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(messageHeaderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMessageHeaderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(messageHeaderRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMessageHeader() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get the messageHeader
        restMessageHeaderMockMvc
            .perform(get(ENTITY_API_URL_ID, messageHeader.getHeaderId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.headerId").value(messageHeader.getHeaderId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    void getMessageHeadersByIdFiltering() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        Long headerId = messageHeader.getHeaderId();

        defaultMessageHeaderShouldBeFound("headerId.equals=" + headerId);
        defaultMessageHeaderShouldNotBeFound("headerId.notEquals=" + headerId);

        defaultMessageHeaderShouldBeFound("headerId.greaterThanOrEqual=" + headerId);
        defaultMessageHeaderShouldNotBeFound("headerId.greaterThan=" + headerId);

        defaultMessageHeaderShouldBeFound("headerId.lessThanOrEqual=" + headerId);
        defaultMessageHeaderShouldNotBeFound("headerId.lessThan=" + headerId);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where name equals to DEFAULT_NAME
        defaultMessageHeaderShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the messageHeaderList where name equals to UPDATED_NAME
        defaultMessageHeaderShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where name in DEFAULT_NAME or UPDATED_NAME
        defaultMessageHeaderShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the messageHeaderList where name equals to UPDATED_NAME
        defaultMessageHeaderShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where name is not null
        defaultMessageHeaderShouldBeFound("name.specified=true");

        // Get all the messageHeaderList where name is null
        defaultMessageHeaderShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllMessageHeadersByNameContainsSomething() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where name contains DEFAULT_NAME
        defaultMessageHeaderShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the messageHeaderList where name contains UPDATED_NAME
        defaultMessageHeaderShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where name does not contain DEFAULT_NAME
        defaultMessageHeaderShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the messageHeaderList where name does not contain UPDATED_NAME
        defaultMessageHeaderShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByValueIsEqualToSomething() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where value equals to DEFAULT_VALUE
        defaultMessageHeaderShouldBeFound("value.equals=" + DEFAULT_VALUE);

        // Get all the messageHeaderList where value equals to UPDATED_VALUE
        defaultMessageHeaderShouldNotBeFound("value.equals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByValueIsInShouldWork() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where value in DEFAULT_VALUE or UPDATED_VALUE
        defaultMessageHeaderShouldBeFound("value.in=" + DEFAULT_VALUE + "," + UPDATED_VALUE);

        // Get all the messageHeaderList where value equals to UPDATED_VALUE
        defaultMessageHeaderShouldNotBeFound("value.in=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where value is not null
        defaultMessageHeaderShouldBeFound("value.specified=true");

        // Get all the messageHeaderList where value is null
        defaultMessageHeaderShouldNotBeFound("value.specified=false");
    }

    @Test
    @Transactional
    void getAllMessageHeadersByValueContainsSomething() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where value contains DEFAULT_VALUE
        defaultMessageHeaderShouldBeFound("value.contains=" + DEFAULT_VALUE);

        // Get all the messageHeaderList where value contains UPDATED_VALUE
        defaultMessageHeaderShouldNotBeFound("value.contains=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByValueNotContainsSomething() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where value does not contain DEFAULT_VALUE
        defaultMessageHeaderShouldNotBeFound("value.doesNotContain=" + DEFAULT_VALUE);

        // Get all the messageHeaderList where value does not contain UPDATED_VALUE
        defaultMessageHeaderShouldBeFound("value.doesNotContain=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByMessageIsEqualToSomething() throws Exception {
        Message message;
        if (TestUtil.findAll(entityManager, Message.class).isEmpty()) {
            messageHeaderRepository.saveAndFlush(messageHeader);
            message = MessageResourceIT.createEntity(entityManager);
        } else {
            message = TestUtil.findAll(entityManager, Message.class).get(0);
        }
        entityManager.persist(message);
        entityManager.flush();
        messageHeader.setMessage(message);
        messageHeaderRepository.saveAndFlush(messageHeader);
        Long messageId = message.getMessageId();
        // Get all the messageHeaderList where message equals to messageId
        defaultMessageHeaderShouldBeFound("messageId.equals=" + messageId);

        // Get all the messageHeaderList where message equals to (messageId + 1)
        defaultMessageHeaderShouldNotBeFound("messageId.equals=" + (messageId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMessageHeaderShouldBeFound(String filter) throws Exception {
        restMessageHeaderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=headerId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].headerId").value(hasItem(messageHeader.getHeaderId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));

        // Check, that the count call also returns 1
        restMessageHeaderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=headerId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMessageHeaderShouldNotBeFound(String filter) throws Exception {
        restMessageHeaderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=headerId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMessageHeaderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=headerId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMessageHeader() throws Exception {
        // Get the messageHeader
        restMessageHeaderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }
}
