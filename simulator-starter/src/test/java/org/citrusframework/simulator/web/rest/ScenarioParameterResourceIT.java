package org.citrusframework.simulator.web.rest;

import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.repository.ScenarioParameterRepository;
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
 * Integration tests for the {@link ScenarioParameterResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
public class ScenarioParameterResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final ScenarioParameter.ControlType DEFAULT_CONTROL_TYPE = ScenarioParameter.ControlType.TEXTBOX; // Integer value: 1
    private static final ScenarioParameter.ControlType UPDATED_CONTROL_TYPE = ScenarioParameter.ControlType.TEXTAREA; // Integer value: 2

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_LAST_MODIFIED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_MODIFIED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_LAST_MODIFIED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/scenario-parameters";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ScenarioParameterRepository scenarioParameterRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc restScenarioParameterMockMvc;

    private ScenarioParameter scenarioParameter;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScenarioParameter createEntity(EntityManager entityManager) {
        return ScenarioParameter.builder()
            .name(DEFAULT_NAME)
            .controlType(DEFAULT_CONTROL_TYPE)
            .value(DEFAULT_VALUE)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE)
            .build();
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScenarioParameter createUpdatedEntity(EntityManager entityManager) {
        return ScenarioParameter.builder()
            .name(UPDATED_NAME)
            .controlType(UPDATED_CONTROL_TYPE)
            .value(UPDATED_VALUE)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .build();
    }

    @BeforeEach
    public void initTest() {
        scenarioParameter = createEntity(entityManager);
    }

    @Test
    @Transactional
    void getAllScenarioParameters() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList
        restScenarioParameterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=parameterId,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].parameterId").value(hasItem(scenarioParameter.getParameterId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].controlType").value(hasItem(DEFAULT_CONTROL_TYPE.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED_DATE))));
    }

    @Test
    @Transactional
    void getScenarioParameter() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get the scenarioParameter
        restScenarioParameterMockMvc
            .perform(get(ENTITY_API_URL_ID, scenarioParameter.getParameterId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.parameterId").value(scenarioParameter.getParameterId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.controlType").value(DEFAULT_CONTROL_TYPE.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.createdDate").value(sameInstant(DEFAULT_CREATED_DATE)))
            .andExpect(jsonPath("$.lastModifiedDate").value(sameInstant(DEFAULT_LAST_MODIFIED_DATE)));
    }

    @Test
    @Transactional
    void getScenarioParametersByIdFiltering() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        Long parameterId = scenarioParameter.getParameterId();

        defaultScenarioParameterShouldBeFound("parameterId.equals=" + parameterId);
        defaultScenarioParameterShouldNotBeFound("parameterId.notEquals=" + parameterId);

        defaultScenarioParameterShouldBeFound("parameterId.greaterThanOrEqual=" + parameterId);
        defaultScenarioParameterShouldNotBeFound("parameterId.greaterThan=" + parameterId);

        defaultScenarioParameterShouldBeFound("parameterId.lessThanOrEqual=" + parameterId);
        defaultScenarioParameterShouldNotBeFound("parameterId.lessThan=" + parameterId);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where name equals to DEFAULT_NAME
        defaultScenarioParameterShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the scenarioParameterList where name equals to UPDATED_NAME
        defaultScenarioParameterShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where name in DEFAULT_NAME or UPDATED_NAME
        defaultScenarioParameterShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the scenarioParameterList where name equals to UPDATED_NAME
        defaultScenarioParameterShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where name is not null
        defaultScenarioParameterShouldBeFound("name.specified=true");

        // Get all the scenarioParameterList where name is null
        defaultScenarioParameterShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllScenarioParametersByNameContainsSomething() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where name contains DEFAULT_NAME
        defaultScenarioParameterShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the scenarioParameterList where name contains UPDATED_NAME
        defaultScenarioParameterShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where name does not contain DEFAULT_NAME
        defaultScenarioParameterShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the scenarioParameterList where name does not contain UPDATED_NAME
        defaultScenarioParameterShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByControlTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where controlType equals to DEFAULT_CONTROL_TYPE
        defaultScenarioParameterShouldBeFound("controlType.equals=" + DEFAULT_CONTROL_TYPE.getId());

        // Get all the scenarioParameterList where controlType equals to UPDATED_CONTROL_TYPE
        defaultScenarioParameterShouldNotBeFound("controlType.equals=" + UPDATED_CONTROL_TYPE.getId());
    }

    @Test
    @Transactional
    void getAllScenarioParametersByControlTypeIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where controlType in DEFAULT_CONTROL_TYPE or UPDATED_CONTROL_TYPE
        defaultScenarioParameterShouldBeFound("controlType.in=" + DEFAULT_CONTROL_TYPE.getId() + "," + UPDATED_CONTROL_TYPE.getId());

        // Get all the scenarioParameterList where controlType equals to UPDATED_CONTROL_TYPE
        defaultScenarioParameterShouldNotBeFound("controlType.in=" + UPDATED_CONTROL_TYPE.getId());
    }

    @Test
    @Transactional
    void getAllScenarioParametersByControlTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where controlType is not null
        defaultScenarioParameterShouldBeFound("controlType.specified=true");

        // Get all the scenarioParameterList where controlType is null
        defaultScenarioParameterShouldNotBeFound("controlType.specified=false");
    }

    @Test
    @Transactional
    void getAllScenarioParametersByControlTypeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where controlType is greater than or equal to DEFAULT_CONTROL_TYPE
        defaultScenarioParameterShouldBeFound("controlType.greaterThanOrEqual=" + DEFAULT_CONTROL_TYPE.getId());

        // Get all the scenarioParameterList where controlType is greater than or equal to UPDATED_CONTROL_TYPE
        defaultScenarioParameterShouldNotBeFound("controlType.greaterThanOrEqual=" + UPDATED_CONTROL_TYPE.getId());
    }

    @Test
    @Transactional
    void getAllScenarioParametersByValueIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where value equals to DEFAULT_VALUE
        defaultScenarioParameterShouldBeFound("value.equals=" + DEFAULT_VALUE);

        // Get all the scenarioParameterList where value equals to UPDATED_VALUE
        defaultScenarioParameterShouldNotBeFound("value.equals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByValueIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where value in DEFAULT_VALUE or UPDATED_VALUE
        defaultScenarioParameterShouldBeFound("value.in=" + DEFAULT_VALUE + "," + UPDATED_VALUE);

        // Get all the scenarioParameterList where value equals to UPDATED_VALUE
        defaultScenarioParameterShouldNotBeFound("value.in=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where value is not null
        defaultScenarioParameterShouldBeFound("value.specified=true");

        // Get all the scenarioParameterList where value is null
        defaultScenarioParameterShouldNotBeFound("value.specified=false");
    }

    @Test
    @Transactional
    void getAllScenarioParametersByValueContainsSomething() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where value contains DEFAULT_VALUE
        defaultScenarioParameterShouldBeFound("value.contains=" + DEFAULT_VALUE);

        // Get all the scenarioParameterList where value contains UPDATED_VALUE
        defaultScenarioParameterShouldNotBeFound("value.contains=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByValueNotContainsSomething() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where value does not contain DEFAULT_VALUE
        defaultScenarioParameterShouldNotBeFound("value.doesNotContain=" + DEFAULT_VALUE);

        // Get all the scenarioParameterList where value does not contain UPDATED_VALUE
        defaultScenarioParameterShouldBeFound("value.doesNotContain=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where createdDate equals to DEFAULT_CREATED_DATE
        defaultScenarioParameterShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the scenarioParameterList where createdDate equals to UPDATED_CREATED_DATE
        defaultScenarioParameterShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultScenarioParameterShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the scenarioParameterList where createdDate equals to UPDATED_CREATED_DATE
        defaultScenarioParameterShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where createdDate is not null
        defaultScenarioParameterShouldBeFound("createdDate.specified=true");

        // Get all the scenarioParameterList where createdDate is null
        defaultScenarioParameterShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllScenarioParametersByCreatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where createdDate is greater than or equal to DEFAULT_CREATED_DATE
        defaultScenarioParameterShouldBeFound("createdDate.greaterThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the scenarioParameterList where createdDate is greater than or equal to UPDATED_CREATED_DATE
        defaultScenarioParameterShouldNotBeFound("createdDate.greaterThanOrEqual=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByCreatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where createdDate is less than or equal to DEFAULT_CREATED_DATE
        defaultScenarioParameterShouldBeFound("createdDate.lessThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the scenarioParameterList where createdDate is less than or equal to SMALLER_CREATED_DATE
        defaultScenarioParameterShouldNotBeFound("createdDate.lessThanOrEqual=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByCreatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where createdDate is less than DEFAULT_CREATED_DATE
        defaultScenarioParameterShouldNotBeFound("createdDate.lessThan=" + DEFAULT_CREATED_DATE);

        // Get all the scenarioParameterList where createdDate is less than UPDATED_CREATED_DATE
        defaultScenarioParameterShouldBeFound("createdDate.lessThan=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByCreatedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where createdDate is greater than DEFAULT_CREATED_DATE
        defaultScenarioParameterShouldNotBeFound("createdDate.greaterThan=" + DEFAULT_CREATED_DATE);

        // Get all the scenarioParameterList where createdDate is greater than SMALLER_CREATED_DATE
        defaultScenarioParameterShouldBeFound("createdDate.greaterThan=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultScenarioParameterShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the scenarioParameterList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultScenarioParameterShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultScenarioParameterShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the scenarioParameterList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultScenarioParameterShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the scenarioParameterList where lastModifiedDate is not null
        defaultScenarioParameterShouldBeFound("lastModifiedDate.specified=true");

        // Get all the scenarioParameterList where lastModifiedDate is null
        defaultScenarioParameterShouldNotBeFound("lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllMessageHeadersByLastModifiedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the messageHeaderList where lastModifiedDate is greater than or equal to DEFAULT_LAST_MODIFIED_DATE
        defaultScenarioParameterShouldBeFound("lastModifiedDate.greaterThanOrEqual=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the messageHeaderList where lastModifiedDate is greater than or equal to UPDATED_LAST_MODIFIED_DATE
        defaultScenarioParameterShouldNotBeFound("lastModifiedDate.greaterThanOrEqual=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllMessageHeadersByLastModifiedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioParameterRepository.saveAndFlush(scenarioParameter);

        // Get all the messageHeaderList where lastModifiedDate is less than or equal to DEFAULT_LAST_MODIFIED_DATE
        defaultScenarioParameterShouldBeFound("lastModifiedDate.lessThanOrEqual=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the messageHeaderList where lastModifiedDate is less than or equal to SMALLER_LAST_MODIFIED_DATE
        defaultScenarioParameterShouldNotBeFound("lastModifiedDate.lessThanOrEqual=" + SMALLER_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioParametersByScenarioExecutionIsEqualToSomething() throws Exception {
        ScenarioExecution scenarioExecution;
        if (TestUtil.findAll(entityManager, ScenarioExecution.class).isEmpty()) {
            scenarioParameterRepository.saveAndFlush(scenarioParameter);
            scenarioExecution = ScenarioExecutionResourceIT.createEntity(entityManager);
        } else {
            scenarioExecution = TestUtil.findAll(entityManager, ScenarioExecution.class).get(0);
        }
        entityManager.persist(scenarioExecution);
        entityManager.flush();
        scenarioParameter.setScenarioExecution(scenarioExecution);
        scenarioParameterRepository.saveAndFlush(scenarioParameter);
        Long scenarioExecutionId = scenarioExecution.getExecutionId();
        // Get all the scenarioParameterList where scenarioExecution equals to scenarioExecutionId
        defaultScenarioParameterShouldBeFound("scenarioExecutionId.equals=" + scenarioExecutionId);

        // Get all the scenarioParameterList where scenarioExecution equals to (scenarioExecutionId + 1)
        defaultScenarioParameterShouldNotBeFound("scenarioExecutionId.equals=" + (scenarioExecutionId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultScenarioParameterShouldBeFound(String filter) throws Exception {
        restScenarioParameterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=parameterId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].parameterId").value(hasItem(scenarioParameter.getParameterId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].controlType").value(hasItem(DEFAULT_CONTROL_TYPE.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED_DATE))));

        // Check, that the count call also returns 1
        restScenarioParameterMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=parameterId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultScenarioParameterShouldNotBeFound(String filter) throws Exception {
        restScenarioParameterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=parameterId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restScenarioParameterMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=parameterId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingScenarioParameter() throws Exception {
        // Get the scenarioParameter
        restScenarioParameterMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }
}
