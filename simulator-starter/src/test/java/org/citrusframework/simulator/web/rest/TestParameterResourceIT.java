package org.citrusframework.simulator.web.rest;

import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.model.TestParameter;
import org.citrusframework.simulator.model.TestResult;
import org.citrusframework.simulator.repository.TestParameterRepository;
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

import static org.citrusframework.simulator.web.rest.TestUtil.findAll;
import static org.citrusframework.simulator.web.rest.TestUtil.sameInstant;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link TestParameterResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
class TestParameterResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_LAST_MODIFIED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_MODIFIED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_LAST_MODIFIED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/test-parameters";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{testResultId}/{key}";

    @Autowired
    private TestParameterRepository testParameterRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc restTestParameterMockMvc;

    private TestParameter testParameter;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TestParameter createEntity(EntityManager entityManager) {
        TestParameter testParameter = TestParameter.builder()
            .key(DEFAULT_KEY)
            .value(DEFAULT_VALUE)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE)
            .build();

        TestResult testResult;
        if (findAll(entityManager, TestResult.class).isEmpty()) {
            testResult = TestResultResourceIT.createEntity(entityManager);
            entityManager.persist(testResult);
            entityManager.flush();
        } else {
            testResult = findAll(entityManager, TestResult.class).get(0);
        }
        testParameter.setTestResult(testResult);

        return testParameter;
    }

    @BeforeEach
    public void initTest() {
        testParameter = createEntity(entityManager);
    }

    @Test
    @Transactional
    void getAllTestParameters() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList
        restTestParameterMockMvc
            .perform(get(ENTITY_API_URL + "?createDate=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED_DATE))));
    }

    @Test
    @Transactional
    void getTestParameter() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get the testParameter
        restTestParameterMockMvc
            .perform(get(ENTITY_API_URL_ID, testParameter.getTestResult().getId(), testParameter.getKey()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.createdDate").value(sameInstant(DEFAULT_CREATED_DATE)))
            .andExpect(jsonPath("$.lastModifiedDate").value(sameInstant(DEFAULT_LAST_MODIFIED_DATE)));
    }

    @Test
    @Transactional
    void getAllTestParametersByKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where key equals to DEFAULT_KEY
        defaultTestParameterShouldBeFound("key.equals=" + DEFAULT_KEY);

        // Get all the testParameterList where key equals to UPDATED_KEY
        defaultTestParameterShouldNotBeFound("key.equals=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllTestParametersByKeyIsInShouldWork() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where key in DEFAULT_KEY or UPDATED_KEY
        defaultTestParameterShouldBeFound("key.in=" + DEFAULT_KEY + "," + UPDATED_KEY);

        // Get all the testParameterList where key equals to UPDATED_KEY
        defaultTestParameterShouldNotBeFound("key.in=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllTestParametersByKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where key is not null
        defaultTestParameterShouldBeFound("key.specified=true");

        // Get all the testParameterList where key is null
        defaultTestParameterShouldNotBeFound("key.specified=false");
    }

    @Test
    @Transactional
    void getAllTestParametersByKeyContainsSomething() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where key contains DEFAULT_KEY
        defaultTestParameterShouldBeFound("key.contains=" + DEFAULT_KEY);

        // Get all the testParameterList where key contains UPDATED_KEY
        defaultTestParameterShouldNotBeFound("key.contains=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllTestParametersByKeyNotContainsSomething() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where key does not contain DEFAULT_KEY
        defaultTestParameterShouldNotBeFound("key.doesNotContain=" + DEFAULT_KEY);

        // Get all the testParameterList where key does not contain UPDATED_KEY
        defaultTestParameterShouldBeFound("key.doesNotContain=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllTestParametersByValueIsEqualToSomething() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where value equals to DEFAULT_VALUE
        defaultTestParameterShouldBeFound("value.equals=" + DEFAULT_VALUE);

        // Get all the testParameterList where value equals to UPDATED_VALUE
        defaultTestParameterShouldNotBeFound("value.equals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllTestParametersByValueIsInShouldWork() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where value in DEFAULT_VALUE or UPDATED_VALUE
        defaultTestParameterShouldBeFound("value.in=" + DEFAULT_VALUE + "," + UPDATED_VALUE);

        // Get all the testParameterList where value equals to UPDATED_VALUE
        defaultTestParameterShouldNotBeFound("value.in=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllTestParametersByValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where value is not null
        defaultTestParameterShouldBeFound("value.specified=true");

        // Get all the testParameterList where value is null
        defaultTestParameterShouldNotBeFound("value.specified=false");
    }

    @Test
    @Transactional
    void getAllTestParametersByValueContainsSomething() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where value contains DEFAULT_VALUE
        defaultTestParameterShouldBeFound("value.contains=" + DEFAULT_VALUE);

        // Get all the testParameterList where value contains UPDATED_VALUE
        defaultTestParameterShouldNotBeFound("value.contains=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllTestParametersByValueNotContainsSomething() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where value does not contain DEFAULT_VALUE
        defaultTestParameterShouldNotBeFound("value.doesNotContain=" + DEFAULT_VALUE);

        // Get all the testParameterList where value does not contain UPDATED_VALUE
        defaultTestParameterShouldBeFound("value.doesNotContain=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllTestParametersByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where createdDate equals to DEFAULT_CREATED_DATE
        defaultTestParameterShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the testParameterList where createdDate equals to UPDATED_CREATED_DATE
        defaultTestParameterShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTestParametersByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultTestParameterShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the testParameterList where createdDate equals to UPDATED_CREATED_DATE
        defaultTestParameterShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTestParametersByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where createdDate is not null
        defaultTestParameterShouldBeFound("createdDate.specified=true");

        // Get all the testParameterList where createdDate is null
        defaultTestParameterShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTestParametersByCreatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where createdDate is greater than or equal to DEFAULT_CREATED_DATE
        defaultTestParameterShouldBeFound("createdDate.greaterThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the testParameterList where createdDate is greater than or equal to UPDATED_CREATED_DATE
        defaultTestParameterShouldNotBeFound("createdDate.greaterThanOrEqual=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTestParametersByCreatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where createdDate is less than or equal to DEFAULT_CREATED_DATE
        defaultTestParameterShouldBeFound("createdDate.lessThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the testParameterList where createdDate is less than or equal to SMALLER_CREATED_DATE
        defaultTestParameterShouldNotBeFound("createdDate.lessThanOrEqual=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTestParametersByCreatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where createdDate is less than DEFAULT_CREATED_DATE
        defaultTestParameterShouldNotBeFound("createdDate.lessThan=" + DEFAULT_CREATED_DATE);

        // Get all the testParameterList where createdDate is less than UPDATED_CREATED_DATE
        defaultTestParameterShouldBeFound("createdDate.lessThan=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTestParametersByCreatedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where createdDate is greater than DEFAULT_CREATED_DATE
        defaultTestParameterShouldNotBeFound("createdDate.greaterThan=" + DEFAULT_CREATED_DATE);

        // Get all the testParameterList where createdDate is greater than SMALLER_CREATED_DATE
        defaultTestParameterShouldBeFound("createdDate.greaterThan=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTestParametersByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultTestParameterShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the testParameterList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultTestParameterShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTestParametersByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultTestParameterShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the testParameterList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultTestParameterShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTestParametersByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where lastModifiedDate is not null
        defaultTestParameterShouldBeFound("lastModifiedDate.specified=true");

        // Get all the testParameterList where lastModifiedDate is null
        defaultTestParameterShouldNotBeFound("lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTestParametersByLastModifiedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where lastModifiedDate is greater than or equal to DEFAULT_LAST_MODIFIED_DATE
        defaultTestParameterShouldBeFound("lastModifiedDate.greaterThanOrEqual=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the testParameterList where lastModifiedDate is greater than or equal to UPDATED_LAST_MODIFIED_DATE
        defaultTestParameterShouldNotBeFound("lastModifiedDate.greaterThanOrEqual=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTestParametersByLastModifiedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where lastModifiedDate is less than or equal to DEFAULT_LAST_MODIFIED_DATE
        defaultTestParameterShouldBeFound("lastModifiedDate.lessThanOrEqual=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the testParameterList where lastModifiedDate is less than or equal to SMALLER_LAST_MODIFIED_DATE
        defaultTestParameterShouldNotBeFound("lastModifiedDate.lessThanOrEqual=" + SMALLER_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTestParametersByLastModifiedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where lastModifiedDate is less than DEFAULT_LAST_MODIFIED_DATE
        defaultTestParameterShouldNotBeFound("lastModifiedDate.lessThan=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the testParameterList where lastModifiedDate is less than UPDATED_LAST_MODIFIED_DATE
        defaultTestParameterShouldBeFound("lastModifiedDate.lessThan=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTestParametersByLastModifiedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        testParameterRepository.saveAndFlush(testParameter);

        // Get all the testParameterList where lastModifiedDate is greater than DEFAULT_LAST_MODIFIED_DATE
        defaultTestParameterShouldNotBeFound("lastModifiedDate.greaterThan=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the testParameterList where lastModifiedDate is greater than SMALLER_LAST_MODIFIED_DATE
        defaultTestParameterShouldBeFound("lastModifiedDate.greaterThan=" + SMALLER_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTestParametersByTestResultIsEqualToSomething() throws Exception {
        TestResult testResult;
        if (TestUtil.findAll(entityManager, TestResult.class).isEmpty()) {
            testParameterRepository.saveAndFlush(testParameter);
            testResult = TestResultResourceIT.createEntity(entityManager);
        } else {
            testResult = TestUtil.findAll(entityManager, TestResult.class).get(0);
        }
        entityManager.persist(testResult);
        entityManager.flush();
        testParameter.setTestResult(testResult);
        testParameterRepository.saveAndFlush(testParameter);
        Long testResultId = testResult.getId();
        // Get all the testParameterList where testResult equals to testResultId
        defaultTestParameterShouldBeFound("testResultId.equals=" + testResultId);

        // Get all the testParameterList where testResult equals to (testResultId + 1)
        defaultTestParameterShouldNotBeFound("testResultId.equals=" + (testResultId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTestParameterShouldBeFound(String filter) throws Exception {
        restTestParameterMockMvc
            .perform(get(ENTITY_API_URL + "?createDate=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED_DATE))));

        // Check, that the count call also returns 1
        restTestParameterMockMvc
            .perform(get(ENTITY_API_URL + "/count?createDate=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTestParameterShouldNotBeFound(String filter) throws Exception {
        restTestParameterMockMvc
            .perform(get(ENTITY_API_URL + "?createDate=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTestParameterMockMvc
            .perform(get(ENTITY_API_URL + "/count?createDate=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTestParameter() throws Exception {
        // Get the testParameter
        restTestParameterMockMvc.perform(get(ENTITY_API_URL_ID, testParameter.getTestResult().getId(), Long.MAX_VALUE)).andExpect(status().isNotFound());
    }
}
