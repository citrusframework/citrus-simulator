/*
 * Copyright the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.citrusframework.simulator.web.rest;

import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.Message.MessageBuilder;
import org.citrusframework.simulator.model.MessageHeader;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.citrusframework.simulator.web.rest.TestUtil.sameInstant;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link MessageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
public class MessageResourceIT {

    private static final Message.Direction DEFAULT_DIRECTION = Message.Direction.INBOUND; // Integer value: 1
    private static final Message.Direction UPDATED_DIRECTION = Message.Direction.OUTBOUND; // Integer value: 2

    static final String DEFAULT_PAYLOAD = "AAAAAAAAAA";
    static final String UPDATED_PAYLOAD = "BBBBBBBBBB";

    private static final String DEFAULT_CITRUS_MESSAGE_ID = "AAAAAAAAAA";
    private static final String UPDATED_CITRUS_MESSAGE_ID = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_LAST_MODIFIED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_MODIFIED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_LAST_MODIFIED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc mockMvc;

    private Message message;

    public static MessageBuilder createEntityBuilder(EntityManager entityManager) {
        return Message.builder()
            .direction(DEFAULT_DIRECTION)
            .payload(DEFAULT_PAYLOAD)
            .citrusMessageId(DEFAULT_CITRUS_MESSAGE_ID)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Message createEntity(EntityManager entityManager) {
        return createEntityBuilder(entityManager)
            .build();
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Message createUpdatedEntity(EntityManager entityManager) {
        return Message.builder()
            .direction(UPDATED_DIRECTION)
            .payload(UPDATED_PAYLOAD)
            .citrusMessageId(UPDATED_CITRUS_MESSAGE_ID)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .build();
    }

    @BeforeEach
    void beforeEachSetup() {
        message = createEntity(entityManager);
    }

    @Test
    @Transactional
    void getAllMessages() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList
        mockMvc
            .perform(get(ENTITY_API_URL + "?sort=messageId,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].messageId").value(hasItem(message.getMessageId().intValue())))
            .andExpect(jsonPath("$.[*].direction").value(hasItem(DEFAULT_DIRECTION.toString())))
            .andExpect(jsonPath("$.[*].payload").value(hasItem(DEFAULT_PAYLOAD)))
            .andExpect(jsonPath("$.[*].citrusMessageId").value(hasItem(DEFAULT_CITRUS_MESSAGE_ID)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED_DATE))));
    }

    @Test
    @Transactional
    void getMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get the message
        mockMvc
            .perform(get(ENTITY_API_URL_ID, message.getMessageId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.messageId").value(message.getMessageId().intValue()))
            .andExpect(jsonPath("$.direction").value(DEFAULT_DIRECTION.toString()))
            .andExpect(jsonPath("$.payload").value(DEFAULT_PAYLOAD))
            .andExpect(jsonPath("$.citrusMessageId").value(DEFAULT_CITRUS_MESSAGE_ID))
            .andExpect(jsonPath("$.createdDate").value(sameInstant(DEFAULT_CREATED_DATE)))
            .andExpect(jsonPath("$.lastModifiedDate").value(sameInstant(DEFAULT_LAST_MODIFIED_DATE)));
    }

    @Test
    @Transactional
    void getMessagesByIdFiltering() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        Long messageId = message.getMessageId();

        defaultMessageShouldBeFound("messageId.equals=" + messageId);
        defaultMessageShouldNotBeFound("messageId.notEquals=" + messageId);

        defaultMessageShouldBeFound("messageId.greaterThanOrEqual=" + messageId);
        defaultMessageShouldNotBeFound("messageId.greaterThan=" + messageId);

        defaultMessageShouldBeFound("messageId.lessThanOrEqual=" + messageId);
        defaultMessageShouldNotBeFound("messageId.lessThan=" + messageId);
    }

    @Test
    @Transactional
    void getAllMessagesByDirectionIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where direction equals to DEFAULT_DIRECTION
        defaultMessageShouldBeFound("direction.equals=" + DEFAULT_DIRECTION.getId());

        // Get all the messageList where direction equals to UPDATED_DIRECTION
        defaultMessageShouldNotBeFound("direction.equals=" + UPDATED_DIRECTION.getId());
    }

    @Test
    @Transactional
    void getAllMessagesByDirectionIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where direction in DEFAULT_DIRECTION or UPDATED_DIRECTION
        defaultMessageShouldBeFound("direction.in=" + DEFAULT_DIRECTION.getId() + "," + UPDATED_DIRECTION.getId());

        // Get all the messageList where direction equals to UPDATED_DIRECTION
        defaultMessageShouldNotBeFound("direction.in=" + UPDATED_DIRECTION.getId());
    }

    @Test
    @Transactional
    void getAllMessagesByDirectionIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where direction is not null
        defaultMessageShouldBeFound("direction.specified=true");

        // Get all the messageList where direction is null
        defaultMessageShouldNotBeFound("direction.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByPayloadIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where payload equals to DEFAULT_PAYLOAD
        defaultMessageShouldBeFound("payload.equals=" + DEFAULT_PAYLOAD);

        // Get all the messageList where payload equals to UPDATED_PAYLOAD
        defaultMessageShouldNotBeFound("payload.equals=" + UPDATED_PAYLOAD);
    }

    @Test
    @Transactional
    void getAllMessagesByPayloadIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where payload in DEFAULT_PAYLOAD or UPDATED_PAYLOAD
        defaultMessageShouldBeFound("payload.in=" + DEFAULT_PAYLOAD + "," + UPDATED_PAYLOAD);

        // Get all the messageList where payload equals to UPDATED_PAYLOAD
        defaultMessageShouldNotBeFound("payload.in=" + UPDATED_PAYLOAD);
    }

    @Test
    @Transactional
    void getAllMessagesByPayloadIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where payload is not null
        defaultMessageShouldBeFound("payload.specified=true");

        // Get all the messageList where payload is null
        defaultMessageShouldNotBeFound("payload.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByPayloadContainsSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where payload contains DEFAULT_PAYLOAD
        defaultMessageShouldBeFound("payload.contains=" + DEFAULT_PAYLOAD);

        // Get all the messageList where payload contains UPDATED_PAYLOAD
        defaultMessageShouldNotBeFound("payload.contains=" + UPDATED_PAYLOAD);
    }

    @Test
    @Transactional
    void getAllMessagesByPayloadNotContainsSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where payload does not contain DEFAULT_PAYLOAD
        defaultMessageShouldNotBeFound("payload.doesNotContain=" + DEFAULT_PAYLOAD);

        // Get all the messageList where payload does not contain UPDATED_PAYLOAD
        defaultMessageShouldBeFound("payload.doesNotContain=" + UPDATED_PAYLOAD);
    }

    @Test
    @Transactional
    void getAllMessagesByCitrusMessageIdIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where citrusMessageId equals to DEFAULT_CITRUS_MESSAGE_ID
        defaultMessageShouldBeFound("citrusMessageId.equals=" + DEFAULT_CITRUS_MESSAGE_ID);

        // Get all the messageList where citrusMessageId equals to UPDATED_CITRUS_MESSAGE_ID
        defaultMessageShouldNotBeFound("citrusMessageId.equals=" + UPDATED_CITRUS_MESSAGE_ID);
    }

    @Test
    @Transactional
    void getAllMessagesByCitrusMessageIdIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where citrusMessageId in DEFAULT_CITRUS_MESSAGE_ID or UPDATED_CITRUS_MESSAGE_ID
        defaultMessageShouldBeFound("citrusMessageId.in=" + DEFAULT_CITRUS_MESSAGE_ID + "," + UPDATED_CITRUS_MESSAGE_ID);

        // Get all the messageList where citrusMessageId equals to UPDATED_CITRUS_MESSAGE_ID
        defaultMessageShouldNotBeFound("citrusMessageId.in=" + UPDATED_CITRUS_MESSAGE_ID);
    }

    @Test
    @Transactional
    void getAllMessagesByCitrusMessageIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where citrusMessageId is not null
        defaultMessageShouldBeFound("citrusMessageId.specified=true");

        // Get all the messageList where citrusMessageId is null
        defaultMessageShouldNotBeFound("citrusMessageId.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByCitrusMessageIdContainsSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where citrusMessageId contains DEFAULT_CITRUS_MESSAGE_ID
        defaultMessageShouldBeFound("citrusMessageId.contains=" + DEFAULT_CITRUS_MESSAGE_ID);

        // Get all the messageList where citrusMessageId contains UPDATED_CITRUS_MESSAGE_ID
        defaultMessageShouldNotBeFound("citrusMessageId.contains=" + UPDATED_CITRUS_MESSAGE_ID);
    }

    @Test
    @Transactional
    void getAllMessagesByCitrusMessageIdNotContainsSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where citrusMessageId does not contain DEFAULT_CITRUS_MESSAGE_ID
        defaultMessageShouldNotBeFound("citrusMessageId.doesNotContain=" + DEFAULT_CITRUS_MESSAGE_ID);

        // Get all the messageList where citrusMessageId does not contain UPDATED_CITRUS_MESSAGE_ID
        defaultMessageShouldBeFound("citrusMessageId.doesNotContain=" + UPDATED_CITRUS_MESSAGE_ID);
    }

    @Test
    @Transactional
    void getAllMessagesByHeadersIsEqualToSomething() throws Exception {
        MessageHeader messageHeader;
        if (TestUtil.findAll(entityManager, MessageHeader.class).isEmpty()) {
            messageRepository.saveAndFlush(message);
            messageHeader = MessageHeaderResourceIT.createEntity(entityManager);
        } else {
            messageHeader = TestUtil.findAll(entityManager, MessageHeader.class).get(0);
        }
        entityManager.persist(messageHeader);
        entityManager.flush();
        message.addHeader(messageHeader);
        messageRepository.saveAndFlush(message);
        Long headersId = messageHeader.getHeaderId();
        // Get all the messageList where headers equals to headersId
        defaultMessageShouldBeFound("headersId.equals=" + headersId);

        // Get all the messageList where headers equals to (headersId + 1)
        defaultMessageShouldNotBeFound("headersId.equals=" + (headersId + 1));
    }

    @Test
    @Transactional
    void getAllMessagesByScenarioExecutionIsEqualToSomething() throws Exception {
        ScenarioExecution scenarioExecution;
        if (TestUtil.findAll(entityManager, ScenarioExecution.class).isEmpty()) {
            messageRepository.saveAndFlush(message);
            scenarioExecution = ScenarioExecutionResourceIT.createEntity(entityManager);
        } else {
            scenarioExecution = TestUtil.findAll(entityManager, ScenarioExecution.class).get(0);
        }
        entityManager.persist(scenarioExecution);
        entityManager.flush();
        message.setScenarioExecution(scenarioExecution);
        messageRepository.saveAndFlush(message);
        Long scenarioExecutionId = scenarioExecution.getExecutionId();
        // Get all the messageList where scenarioExecution equals to scenarioExecutionId
        defaultMessageShouldBeFound("scenarioExecutionId.equals=" + scenarioExecutionId);

        // Get all the messageList where scenarioExecution equals to (scenarioExecutionId + 1)
        defaultMessageShouldNotBeFound("scenarioExecutionId.equals=" + (scenarioExecutionId + 1));
    }

    @Test
    @Transactional
    void getAllMessagesByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where createdDate equals to DEFAULT_CREATED_DATE
        defaultMessageShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the messageList where createdDate equals to UPDATED_CREATED_DATE
        defaultMessageShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllMessagesByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultMessageShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the messageList where createdDate equals to UPDATED_CREATED_DATE
        defaultMessageShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllMessagesByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where createdDate is not null
        defaultMessageShouldBeFound("createdDate.specified=true");

        // Get all the messageList where createdDate is null
        defaultMessageShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByCreatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where createdDate is greater than or equal to DEFAULT_CREATED_DATE
        defaultMessageShouldBeFound("createdDate.greaterThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the messageList where createdDate is greater than or equal to UPDATED_CREATED_DATE
        defaultMessageShouldNotBeFound("createdDate.greaterThanOrEqual=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllMessagesByCreatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where createdDate is less than or equal to DEFAULT_CREATED_DATE
        defaultMessageShouldBeFound("createdDate.lessThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the messageList where createdDate is less than or equal to SMALLER_CREATED_DATE
        defaultMessageShouldNotBeFound("createdDate.lessThanOrEqual=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllMessagesByCreatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where createdDate is less than DEFAULT_CREATED_DATE
        defaultMessageShouldNotBeFound("createdDate.lessThan=" + DEFAULT_CREATED_DATE);

        // Get all the messageList where createdDate is less than UPDATED_CREATED_DATE
        defaultMessageShouldBeFound("createdDate.lessThan=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllMessagesByCreatedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where createdDate is greater than DEFAULT_CREATED_DATE
        defaultMessageShouldNotBeFound("createdDate.greaterThan=" + DEFAULT_CREATED_DATE);

        // Get all the messageList where createdDate is greater than SMALLER_CREATED_DATE
        defaultMessageShouldBeFound("createdDate.greaterThan=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllMessagesByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultMessageShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the messageList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultMessageShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllMessagesByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultMessageShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the messageList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultMessageShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllMessagesByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where lastModifiedDate is not null
        defaultMessageShouldBeFound("lastModifiedDate.specified=true");

        // Get all the messageList where lastModifiedDate is null
        defaultMessageShouldNotBeFound("lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByLastModifiedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where lastModifiedDate is greater than or equal to DEFAULT_LAST_MODIFIED_DATE
        defaultMessageShouldBeFound("lastModifiedDate.greaterThanOrEqual=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the messageList where lastModifiedDate is greater than or equal to UPDATED_LAST_MODIFIED_DATE
        defaultMessageShouldNotBeFound("lastModifiedDate.greaterThanOrEqual=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllMessagesByLastModifiedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where lastModifiedDate is less than or equal to DEFAULT_LAST_MODIFIED_DATE
        defaultMessageShouldBeFound("lastModifiedDate.lessThanOrEqual=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the messageList where lastModifiedDate is less than or equal to SMALLER_LAST_MODIFIED_DATE
        defaultMessageShouldNotBeFound("lastModifiedDate.lessThanOrEqual=" + SMALLER_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllMessagesByLastModifiedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where lastModifiedDate is less than DEFAULT_LAST_MODIFIED_DATE
        defaultMessageShouldNotBeFound("lastModifiedDate.lessThan=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the messageList where lastModifiedDate is less than UPDATED_LAST_MODIFIED_DATE
        defaultMessageShouldBeFound("lastModifiedDate.lessThan=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllMessagesByLastModifiedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where lastModifiedDate is greater than DEFAULT_LAST_MODIFIED_DATE
        defaultMessageShouldNotBeFound("lastModifiedDate.greaterThan=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the messageList where lastModifiedDate is greater than SMALLER_LAST_MODIFIED_DATE
        defaultMessageShouldBeFound("lastModifiedDate.greaterThan=" + SMALLER_LAST_MODIFIED_DATE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMessageShouldBeFound(String filter) throws Exception {
        mockMvc
            .perform(get(ENTITY_API_URL + "?sort=messageId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].messageId").value(hasItem(message.getMessageId().intValue())))
            .andExpect(jsonPath("$.[*].direction").value(hasItem(DEFAULT_DIRECTION.toString())))
            .andExpect(jsonPath("$.[*].payload").value(hasItem(DEFAULT_PAYLOAD)))
            .andExpect(jsonPath("$.[*].citrusMessageId").value(hasItem(DEFAULT_CITRUS_MESSAGE_ID)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED_DATE))));

        // Check, that the count call also returns 1
        mockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=messageId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMessageShouldNotBeFound(String filter) throws Exception {
        mockMvc
            .perform(get(ENTITY_API_URL + "?sort=messageId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        mockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=messageId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMessage() throws Exception {
        // Get the message
        mockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }
}
