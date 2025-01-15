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
import org.citrusframework.simulator.model.MessageHeader;
import org.citrusframework.simulator.repository.MessageHeaderRepository;
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
 * Integration tests for the {@link MessageHeaderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
public class MessageHeaderResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_LAST_MODIFIED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_MODIFIED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_LAST_MODIFIED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/message-headers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private MessageHeaderRepository messageHeaderRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc mockMvc;

    private MessageHeader messageHeader;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MessageHeader createEntity(EntityManager entityManager) {
        MessageHeader messageHeader = MessageHeader.builder()
            .name(DEFAULT_NAME)
            .value(DEFAULT_VALUE)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE)
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
        message.addHeader(messageHeader);
        return messageHeader;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MessageHeader createUpdatedEntity(EntityManager em) {
        MessageHeader messageHeader = MessageHeader.builder()
            .name(UPDATED_NAME)
            .value(UPDATED_VALUE)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .build();
        // Add required entity
        Message message;
        if (TestUtil.findAll(em, Message.class).isEmpty()) {
            message = MessageResourceIT.createUpdatedEntity(em);
            em.persist(message);
            em.flush();
        } else {
            message = TestUtil.findAll(em, Message.class).get(0);
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
        mockMvc
            .perform(get(ENTITY_API_URL + "?sort=headerId,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].headerId").value(hasItem(messageHeader.getHeaderId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED_DATE))));
    }

    @Test
    @Transactional
    void getMessageHeader() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get the messageHeader
        mockMvc
            .perform(get(ENTITY_API_URL_ID, messageHeader.getHeaderId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.headerId").value(messageHeader.getHeaderId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.createdDate").value(sameInstant(DEFAULT_CREATED_DATE)))
            .andExpect(jsonPath("$.lastModifiedDate").value(sameInstant(DEFAULT_LAST_MODIFIED_DATE)));
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
    void getAllMessageHeadersByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where createdDate equals to DEFAULT_CREATED_DATE
        defaultMessageHeaderShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the messageHeaderList where createdDate equals to UPDATED_CREATED_DATE
        defaultMessageHeaderShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultMessageHeaderShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the messageHeaderList where createdDate equals to UPDATED_CREATED_DATE
        defaultMessageHeaderShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where createdDate is not null
        defaultMessageHeaderShouldBeFound("createdDate.specified=true");

        // Get all the messageHeaderList where createdDate is null
        defaultMessageHeaderShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllMessageHeadersByCreatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where createdDate is greater than or equal to DEFAULT_CREATED_DATE
        defaultMessageHeaderShouldBeFound("createdDate.greaterThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the messageHeaderList where createdDate is greater than or equal to UPDATED_CREATED_DATE
        defaultMessageHeaderShouldNotBeFound("createdDate.greaterThanOrEqual=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByCreatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where createdDate is less than or equal to DEFAULT_CREATED_DATE
        defaultMessageHeaderShouldBeFound("createdDate.lessThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the messageHeaderList where createdDate is less than or equal to SMALLER_CREATED_DATE
        defaultMessageHeaderShouldNotBeFound("createdDate.lessThanOrEqual=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByCreatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where createdDate is less than DEFAULT_CREATED_DATE
        defaultMessageHeaderShouldNotBeFound("createdDate.lessThan=" + DEFAULT_CREATED_DATE);

        // Get all the messageHeaderList where createdDate is less than UPDATED_CREATED_DATE
        defaultMessageHeaderShouldBeFound("createdDate.lessThan=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByCreatedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where createdDate is greater than DEFAULT_CREATED_DATE
        defaultMessageHeaderShouldNotBeFound("createdDate.greaterThan=" + DEFAULT_CREATED_DATE);

        // Get all the messageHeaderList where createdDate is greater than SMALLER_CREATED_DATE
        defaultMessageHeaderShouldBeFound("createdDate.greaterThan=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultMessageHeaderShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the messageHeaderList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultMessageHeaderShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultMessageHeaderShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the messageHeaderList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultMessageHeaderShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where lastModifiedDate is not null
        defaultMessageHeaderShouldBeFound("lastModifiedDate.specified=true");

        // Get all the messageHeaderList where lastModifiedDate is null
        defaultMessageHeaderShouldNotBeFound("lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllMessageHeadersByLastModifiedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where lastModifiedDate is greater than or equal to DEFAULT_LAST_MODIFIED_DATE
        defaultMessageHeaderShouldBeFound("lastModifiedDate.greaterThanOrEqual=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the messageHeaderList where lastModifiedDate is greater than or equal to UPDATED_LAST_MODIFIED_DATE
        defaultMessageHeaderShouldNotBeFound("lastModifiedDate.greaterThanOrEqual=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByLastModifiedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where lastModifiedDate is less than or equal to DEFAULT_LAST_MODIFIED_DATE
        defaultMessageHeaderShouldBeFound("lastModifiedDate.lessThanOrEqual=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the messageHeaderList where lastModifiedDate is less than or equal to SMALLER_LAST_MODIFIED_DATE
        defaultMessageHeaderShouldNotBeFound("lastModifiedDate.lessThanOrEqual=" + SMALLER_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByLastModifiedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where lastModifiedDate is less than DEFAULT_LAST_MODIFIED_DATE
        defaultMessageHeaderShouldNotBeFound("lastModifiedDate.lessThan=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the messageHeaderList where lastModifiedDate is less than UPDATED_LAST_MODIFIED_DATE
        defaultMessageHeaderShouldBeFound("lastModifiedDate.lessThan=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByLastModifiedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        messageHeaderRepository.saveAndFlush(messageHeader);

        // Get all the messageHeaderList where lastModifiedDate is greater than DEFAULT_LAST_MODIFIED_DATE
        defaultMessageHeaderShouldNotBeFound("lastModifiedDate.greaterThan=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the messageHeaderList where lastModifiedDate is greater than SMALLER_LAST_MODIFIED_DATE
        defaultMessageHeaderShouldBeFound("lastModifiedDate.greaterThan=" + SMALLER_LAST_MODIFIED_DATE);
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
        mockMvc
            .perform(get(ENTITY_API_URL + "?sort=headerId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].headerId").value(hasItem(messageHeader.getHeaderId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED_DATE))));

        // Check, that the count call also returns 1
        mockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=headerId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMessageHeaderShouldNotBeFound(String filter) throws Exception {
        mockMvc
            .perform(get(ENTITY_API_URL + "?sort=headerId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        mockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=headerId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMessageHeader() throws Exception {
        // Get the messageHeader
        mockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }
}
