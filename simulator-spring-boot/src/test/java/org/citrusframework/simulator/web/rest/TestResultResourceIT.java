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
import org.citrusframework.simulator.model.TestParameter;
import org.citrusframework.simulator.model.TestResult;
import org.citrusframework.simulator.repository.TestResultRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.citrusframework.simulator.web.rest.TestUtil.sameInstant;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link TestResultResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
public class TestResultResourceIT {

    private static final TestResult.Status DEFAULT_STATUS = TestResult.Status.SUCCESS; // Integer value: 1
    private static final TestResult.Status UPDATED_STATUS = TestResult.Status.FAILURE; // Integer value: 2

    private static final String DEFAULT_TEST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TEST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CLASS_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CLASS_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_STACK_TRACE = "AAAAAAAAAA";
    private static final String UPDATED_STACK_TRACE = "BBBBBBBBBB";

    private static final String DEFAULT_FAILURE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_FAILURE_TYPE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_LAST_MODIFIED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_MODIFIED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_LAST_MODIFIED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/test-results";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc mockMvc;

    private TestResult testResult;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TestResult createEntity(EntityManager entityManager) {
        return createEntity(entityManager, false);
    }

    public static TestResult createEntity(EntityManager entityManager, boolean includeScenarioExecution) {
        var testResult = TestResult.builder()
            .status(DEFAULT_STATUS)
            .testName(DEFAULT_TEST_NAME)
            .className(DEFAULT_CLASS_NAME)
            .errorMessage(DEFAULT_ERROR_MESSAGE)
            .stackTrace(DEFAULT_STACK_TRACE)
            .failureType(DEFAULT_FAILURE_TYPE)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE)
            .build();

        if (includeScenarioExecution) {
            var scenarioExecution = ScenarioExecutionResourceIT.createEntity(entityManager);
            entityManager.persist(scenarioExecution);
            testResult.setScenarioExecution(scenarioExecution);
        }

        return testResult;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TestResult createUpdatedEntity(EntityManager entityManager) {
        return TestResult.builder()
            .status(UPDATED_STATUS)
            .testName(UPDATED_TEST_NAME)
            .className(UPDATED_CLASS_NAME)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .stackTrace(UPDATED_STACK_TRACE)
            .failureType(UPDATED_FAILURE_TYPE)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .build();
    }

    @BeforeEach
    void beforeEachSetup() {
        testResult = createEntity(entityManager);
    }

    @Test
    @Transactional
    void getAllTestResults() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList
        mockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(testResult.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].testName").value(hasItem(DEFAULT_TEST_NAME)))
            .andExpect(jsonPath("$.[*].className").value(hasItem(DEFAULT_CLASS_NAME)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].stackTrace").value(hasItem(DEFAULT_STACK_TRACE)))
            .andExpect(jsonPath("$.[*].failureType").value(hasItem(DEFAULT_FAILURE_TYPE)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED_DATE))));
    }

    @Test
    @Transactional
    void getTestResult() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get the testResult
        mockMvc
            .perform(get(ENTITY_API_URL_ID, testResult.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(testResult.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.testName").value(DEFAULT_TEST_NAME))
            .andExpect(jsonPath("$.className").value(DEFAULT_CLASS_NAME))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE))
            .andExpect(jsonPath("$.stackTrace").value(DEFAULT_STACK_TRACE))
            .andExpect(jsonPath("$.failureType").value(DEFAULT_FAILURE_TYPE))
            .andExpect(jsonPath("$.createdDate").value(sameInstant(DEFAULT_CREATED_DATE)))
            .andExpect(jsonPath("$.lastModifiedDate").value(sameInstant(DEFAULT_LAST_MODIFIED_DATE)));
    }

    @Test
    @Transactional
    void getTestResultsByIdFiltering() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        Long id = testResult.getId();

        defaultTestResultShouldBeFound("id.equals=" + id);
        defaultTestResultShouldNotBeFound("id.notEquals=" + id);

        defaultTestResultShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTestResultShouldNotBeFound("id.greaterThan=" + id);

        defaultTestResultShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTestResultShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTestResultsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where status equals to DEFAULT_STATUS
        defaultTestResultShouldBeFound("status.equals=" + DEFAULT_STATUS.getId());

        // Get all the testResultList where status equals to UPDATED_STATUS
        defaultTestResultShouldNotBeFound("status.equals=" + UPDATED_STATUS.getId());
    }

    @Test
    @Transactional
    void getAllTestResultsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultTestResultShouldBeFound("status.in=" + DEFAULT_STATUS.getId() + "," + UPDATED_STATUS.getId());

        // Get all the testResultList where status equals to UPDATED_STATUS
        defaultTestResultShouldNotBeFound("status.in=" + UPDATED_STATUS.getId());
    }

    @Test
    @Transactional
    void getAllTestResultsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where status is not null
        defaultTestResultShouldBeFound("status.specified=true");

        // Get all the testResultList where status is null
        defaultTestResultShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllTestResultsByTestNameIsEqualToSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where testName equals to DEFAULT_TEST_NAME
        defaultTestResultShouldBeFound("testName.equals=" + DEFAULT_TEST_NAME);

        // Get all the testResultList where testName equals to UPDATED_TEST_NAME
        defaultTestResultShouldNotBeFound("testName.equals=" + UPDATED_TEST_NAME);
    }

    @Test
    @Transactional
    void getAllTestResultsByTestNameIsInShouldWork() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where testName in DEFAULT_TEST_NAME or UPDATED_TEST_NAME
        defaultTestResultShouldBeFound("testName.in=" + DEFAULT_TEST_NAME + "," + UPDATED_TEST_NAME);

        // Get all the testResultList where testName equals to UPDATED_TEST_NAME
        defaultTestResultShouldNotBeFound("testName.in=" + UPDATED_TEST_NAME);
    }

    @Test
    @Transactional
    void getAllTestResultsByTestNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where testName is not null
        defaultTestResultShouldBeFound("testName.specified=true");

        // Get all the testResultList where testName is null
        defaultTestResultShouldNotBeFound("testName.specified=false");
    }

    @Test
    @Transactional
    void getAllTestResultsByTestNameContainsSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where testName contains DEFAULT_TEST_NAME
        defaultTestResultShouldBeFound("testName.contains=" + DEFAULT_TEST_NAME);

        // Get all the testResultList where testName contains UPDATED_TEST_NAME
        defaultTestResultShouldNotBeFound("testName.contains=" + UPDATED_TEST_NAME);
    }

    @Test
    @Transactional
    void getAllTestResultsByTestNameNotContainsSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where testName does not contain DEFAULT_TEST_NAME
        defaultTestResultShouldNotBeFound("testName.doesNotContain=" + DEFAULT_TEST_NAME);

        // Get all the testResultList where testName does not contain UPDATED_TEST_NAME
        defaultTestResultShouldBeFound("testName.doesNotContain=" + UPDATED_TEST_NAME);
    }

    @Test
    @Transactional
    void getAllTestResultsByClassNameIsEqualToSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where className equals to DEFAULT_CLASS_NAME
        defaultTestResultShouldBeFound("className.equals=" + DEFAULT_CLASS_NAME);

        // Get all the testResultList where className equals to UPDATED_CLASS_NAME
        defaultTestResultShouldNotBeFound("className.equals=" + UPDATED_CLASS_NAME);
    }

    @Test
    @Transactional
    void getAllTestResultsByClassNameIsInShouldWork() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where className in DEFAULT_CLASS_NAME or UPDATED_CLASS_NAME
        defaultTestResultShouldBeFound("className.in=" + DEFAULT_CLASS_NAME + "," + UPDATED_CLASS_NAME);

        // Get all the testResultList where className equals to UPDATED_CLASS_NAME
        defaultTestResultShouldNotBeFound("className.in=" + UPDATED_CLASS_NAME);
    }

    @Test
    @Transactional
    void getAllTestResultsByClassNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where className is not null
        defaultTestResultShouldBeFound("className.specified=true");

        // Get all the testResultList where className is null
        defaultTestResultShouldNotBeFound("className.specified=false");
    }

    @Test
    @Transactional
    void getAllTestResultsByClassNameContainsSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where className contains DEFAULT_CLASS_NAME
        defaultTestResultShouldBeFound("className.contains=" + DEFAULT_CLASS_NAME);

        // Get all the testResultList where className contains UPDATED_CLASS_NAME
        defaultTestResultShouldNotBeFound("className.contains=" + UPDATED_CLASS_NAME);
    }

    @Test
    @Transactional
    void getAllTestResultsByClassNameNotContainsSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where className does not contain DEFAULT_CLASS_NAME
        defaultTestResultShouldNotBeFound("className.doesNotContain=" + DEFAULT_CLASS_NAME);

        // Get all the testResultList where className does not contain UPDATED_CLASS_NAME
        defaultTestResultShouldBeFound("className.doesNotContain=" + UPDATED_CLASS_NAME);
    }

    @Test
    @Transactional
    void getAllTestResultsByErrorMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where errorMessage equals to DEFAULT_ERROR_MESSAGE
        defaultTestResultShouldBeFound("errorMessage.equals=" + DEFAULT_ERROR_MESSAGE);

        // Get all the testResultList where errorMessage equals to UPDATED_ERROR_MESSAGE
        defaultTestResultShouldNotBeFound("errorMessage.equals=" + UPDATED_ERROR_MESSAGE);
    }

    @Test
    @Transactional
    void getAllTestResultsByErrorMessageIsInShouldWork() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where errorMessage in DEFAULT_ERROR_MESSAGE or UPDATED_ERROR_MESSAGE
        defaultTestResultShouldBeFound("errorMessage.in=" + DEFAULT_ERROR_MESSAGE + "," + UPDATED_ERROR_MESSAGE);

        // Get all the testResultList where errorMessage equals to UPDATED_ERROR_MESSAGE
        defaultTestResultShouldNotBeFound("errorMessage.in=" + UPDATED_ERROR_MESSAGE);
    }

    @Test
    @Transactional
    void getAllTestResultsByErrorMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where errorMessage is not null
        defaultTestResultShouldBeFound("errorMessage.specified=true");

        // Get all the testResultList where errorMessage is null
        defaultTestResultShouldNotBeFound("errorMessage.specified=false");
    }

    @Test
    @Transactional
    void getAllTestResultsByErrorMessageContainsSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where errorMessage contains DEFAULT_ERROR_MESSAGE
        defaultTestResultShouldBeFound("errorMessage.contains=" + DEFAULT_ERROR_MESSAGE);

        // Get all the testResultList where errorMessage contains UPDATED_ERROR_MESSAGE
        defaultTestResultShouldNotBeFound("errorMessage.contains=" + UPDATED_ERROR_MESSAGE);
    }

    @Test
    @Transactional
    void getAllTestResultsByErrorMessageNotContainsSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where errorMessage does not contain DEFAULT_ERROR_MESSAGE
        defaultTestResultShouldNotBeFound("errorMessage.doesNotContain=" + DEFAULT_ERROR_MESSAGE);

        // Get all the testResultList where errorMessage does not contain UPDATED_ERROR_MESSAGE
        defaultTestResultShouldBeFound("errorMessage.doesNotContain=" + UPDATED_ERROR_MESSAGE);
    }

    @Test
    @Transactional
    void getAllTestResultsByStackTraceIsEqualToSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where stackTrace equals to DEFAULT_FAILURE_STACK
        defaultTestResultShouldBeFound("stackTrace.equals=" + DEFAULT_STACK_TRACE);

        // Get all the testResultList where stackTrace equals to UPDATED_FAILURE_STACK
        defaultTestResultShouldNotBeFound("stackTrace.equals=" + UPDATED_STACK_TRACE);
    }

    @Test
    @Transactional
    void getAllTestResultsByStackTraceIsInShouldWork() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where stackTrace in DEFAULT_FAILURE_STACK or UPDATED_FAILURE_STACK
        defaultTestResultShouldBeFound("stackTrace.in=" + DEFAULT_STACK_TRACE + "," + UPDATED_STACK_TRACE);

        // Get all the testResultList where stackTrace equals to UPDATED_FAILURE_STACK
        defaultTestResultShouldNotBeFound("stackTrace.in=" + UPDATED_STACK_TRACE);
    }

    @Test
    @Transactional
    void getAllTestResultsByStackTraceIsNullOrNotNull() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where stackTrace is not null
        defaultTestResultShouldBeFound("stackTrace.specified=true");

        // Get all the testResultList where stackTrace is null
        defaultTestResultShouldNotBeFound("stackTrace.specified=false");
    }

    @Test
    @Transactional
    void getAllTestResultsByStackTraceContainsSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where stackTrace contains DEFAULT_FAILURE_STACK
        defaultTestResultShouldBeFound("stackTrace.contains=" + DEFAULT_STACK_TRACE);

        // Get all the testResultList where stackTrace contains UPDATED_FAILURE_STACK
        defaultTestResultShouldNotBeFound("stackTrace.contains=" + UPDATED_STACK_TRACE);
    }

    @Test
    @Transactional
    void getAllTestResultsByStackTraceNotContainsSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where stackTrace does not contain DEFAULT_FAILURE_STACK
        defaultTestResultShouldNotBeFound("stackTrace.doesNotContain=" + DEFAULT_STACK_TRACE);

        // Get all the testResultList where stackTrace does not contain UPDATED_FAILURE_STACK
        defaultTestResultShouldBeFound("stackTrace.doesNotContain=" + UPDATED_STACK_TRACE);
    }

    @Test
    @Transactional
    void getAllTestResultsByFailureTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where failureType equals to DEFAULT_FAILURE_TYPE
        defaultTestResultShouldBeFound("failureType.equals=" + DEFAULT_FAILURE_TYPE);

        // Get all the testResultList where failureType equals to UPDATED_FAILURE_TYPE
        defaultTestResultShouldNotBeFound("failureType.equals=" + UPDATED_FAILURE_TYPE);
    }

    @Test
    @Transactional
    void getAllTestResultsByFailureTypeIsInShouldWork() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where failureType in DEFAULT_FAILURE_TYPE or UPDATED_FAILURE_TYPE
        defaultTestResultShouldBeFound("failureType.in=" + DEFAULT_FAILURE_TYPE + "," + UPDATED_FAILURE_TYPE);

        // Get all the testResultList where failureType equals to UPDATED_FAILURE_TYPE
        defaultTestResultShouldNotBeFound("failureType.in=" + UPDATED_FAILURE_TYPE);
    }

    @Test
    @Transactional
    void getAllTestResultsByFailureTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where failureType is not null
        defaultTestResultShouldBeFound("failureType.specified=true");

        // Get all the testResultList where failureType is null
        defaultTestResultShouldNotBeFound("failureType.specified=false");
    }

    @Test
    @Transactional
    void getAllTestResultsByFailureTypeContainsSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where failureType contains DEFAULT_FAILURE_TYPE
        defaultTestResultShouldBeFound("failureType.contains=" + DEFAULT_FAILURE_TYPE);

        // Get all the testResultList where failureType contains UPDATED_FAILURE_TYPE
        defaultTestResultShouldNotBeFound("failureType.contains=" + UPDATED_FAILURE_TYPE);
    }

    @Test
    @Transactional
    void getAllTestResultsByFailureTypeNotContainsSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where failureType does not contain DEFAULT_FAILURE_TYPE
        defaultTestResultShouldNotBeFound("failureType.doesNotContain=" + DEFAULT_FAILURE_TYPE);

        // Get all the testResultList where failureType does not contain UPDATED_FAILURE_TYPE
        defaultTestResultShouldBeFound("failureType.doesNotContain=" + UPDATED_FAILURE_TYPE);
    }

    @Test
    @Transactional
    void getAllTestResultsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where createdDate equals to DEFAULT_CREATED_DATE
        defaultTestResultShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the testResultList where createdDate equals to UPDATED_CREATED_DATE
        defaultTestResultShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTestResultsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultTestResultShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the testResultList where createdDate equals to UPDATED_CREATED_DATE
        defaultTestResultShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTestResultsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where createdDate is not null
        defaultTestResultShouldBeFound("createdDate.specified=true");

        // Get all the testResultList where createdDate is null
        defaultTestResultShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTestResultsByCreatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where createdDate is greater than or equal to DEFAULT_CREATED_DATE
        defaultTestResultShouldBeFound("createdDate.greaterThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the testResultList where createdDate is greater than or equal to UPDATED_CREATED_DATE
        defaultTestResultShouldNotBeFound("createdDate.greaterThanOrEqual=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTestResultsByCreatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where createdDate is less than or equal to DEFAULT_CREATED_DATE
        defaultTestResultShouldBeFound("createdDate.lessThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the testResultList where createdDate is less than or equal to SMALLER_CREATED_DATE
        defaultTestResultShouldNotBeFound("createdDate.lessThanOrEqual=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTestResultsByCreatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where createdDate is less than DEFAULT_CREATED_DATE
        defaultTestResultShouldNotBeFound("createdDate.lessThan=" + DEFAULT_CREATED_DATE);

        // Get all the testResultList where createdDate is less than UPDATED_CREATED_DATE
        defaultTestResultShouldBeFound("createdDate.lessThan=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTestResultsByCreatedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where createdDate is greater than DEFAULT_CREATED_DATE
        defaultTestResultShouldNotBeFound("createdDate.greaterThan=" + DEFAULT_CREATED_DATE);

        // Get all the testResultList where createdDate is greater than SMALLER_CREATED_DATE
        defaultTestResultShouldBeFound("createdDate.greaterThan=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTestResultsByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultTestResultShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the testResultList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultTestResultShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTestResultsByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultTestResultShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the testResultList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultTestResultShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTestResultsByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where lastModifiedDate is not null
        defaultTestResultShouldBeFound("lastModifiedDate.specified=true");

        // Get all the testResultList where lastModifiedDate is null
        defaultTestResultShouldNotBeFound("lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTestResultsByLastModifiedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where lastModifiedDate is greater than or equal to DEFAULT_LAST_MODIFIED_DATE
        defaultTestResultShouldBeFound("lastModifiedDate.greaterThanOrEqual=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the testResultList where lastModifiedDate is greater than or equal to UPDATED_LAST_MODIFIED_DATE
        defaultTestResultShouldNotBeFound("lastModifiedDate.greaterThanOrEqual=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTestResultsByLastModifiedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where lastModifiedDate is less than or equal to DEFAULT_LAST_MODIFIED_DATE
        defaultTestResultShouldBeFound("lastModifiedDate.lessThanOrEqual=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the testResultList where lastModifiedDate is less than or equal to SMALLER_LAST_MODIFIED_DATE
        defaultTestResultShouldNotBeFound("lastModifiedDate.lessThanOrEqual=" + SMALLER_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTestResultsByLastModifiedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where lastModifiedDate is less than DEFAULT_LAST_MODIFIED_DATE
        defaultTestResultShouldNotBeFound("lastModifiedDate.lessThan=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the testResultList where lastModifiedDate is less than UPDATED_LAST_MODIFIED_DATE
        defaultTestResultShouldBeFound("lastModifiedDate.lessThan=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTestResultsByLastModifiedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        testResultRepository.saveAndFlush(testResult);

        // Get all the testResultList where lastModifiedDate is greater than DEFAULT_LAST_MODIFIED_DATE
        defaultTestResultShouldNotBeFound("lastModifiedDate.greaterThan=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the testResultList where lastModifiedDate is greater than SMALLER_LAST_MODIFIED_DATE
        defaultTestResultShouldBeFound("lastModifiedDate.greaterThan=" + SMALLER_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTestResultsByTestParameterIsEqualToSomething() throws Exception {
        TestParameter testParameter = getOrCreateTestParameterWithTestResult();

        String testParameterKey = testParameter.getKey();
        // Get all the testResultList where testParameter equals to testParameterKey
        defaultTestResultShouldBeFound("testParameterKey.equals=" + testParameterKey);

        // Get all the testResultList where testParameter equals to (testParameterKey + 1)
        defaultTestResultShouldNotBeFound("testParameterKey.equals=" + (testParameterKey + 1));
    }

    @Test
    @Transactional
    void deleteAllTestResults() throws Exception {
        testResult = createEntity(entityManager, true);
        getOrCreateTestParameterWithTestResult();

        assertThat(testResultRepository.findAll())
            .isNotEmpty();

        mockMvc
            .perform(delete(ENTITY_API_URL).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        assertThat(testResultRepository.findAll())
            .isEmpty();
    }

    private TestParameter getOrCreateTestParameterWithTestResult() {
        TestParameter testParameter;
        if (TestUtil.findAll(entityManager, TestParameter.class).isEmpty()) {
            testResultRepository.saveAndFlush(testResult);
            testParameter = TestParameterResourceIT.createEntity(entityManager);
        } else {
            testParameter = TestUtil.findAll(entityManager, TestParameter.class).get(0);
        }
        entityManager.persist(testParameter);
        entityManager.flush();
        testResult.addTestParameter(testParameter);
        testResultRepository.saveAndFlush(testResult);
        return testParameter;
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTestResultShouldBeFound(String filter) throws Exception {
        mockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(testResult.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].testName").value(hasItem(DEFAULT_TEST_NAME)))
            .andExpect(jsonPath("$.[*].className").value(hasItem(DEFAULT_CLASS_NAME)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].stackTrace").value(hasItem(DEFAULT_STACK_TRACE)))
            .andExpect(jsonPath("$.[*].failureType").value(hasItem(DEFAULT_FAILURE_TYPE)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED_DATE))));

        // Check, that the count call also returns 1
        mockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTestResultShouldNotBeFound(String filter) throws Exception {
        mockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        mockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTestResult() throws Exception {
        // Get the testResult
        mockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }
}
