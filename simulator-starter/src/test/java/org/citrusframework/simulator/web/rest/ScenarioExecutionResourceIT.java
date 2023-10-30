package org.citrusframework.simulator.web.rest;

import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.ScenarioAction;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.repository.ScenarioExecutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link ScenarioExecutionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
public class ScenarioExecutionResourceIT {

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    private static final Instant SMALLER_START_DATE = Instant.ofEpochMilli(-1L);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    private static final Instant SMALLER_END_DATE = Instant.ofEpochMilli(-1L);

    private static final String DEFAULT_SCENARIO_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SCENARIO_NAME = "BBBBBBBBBB";

    private static final ScenarioExecution.Status DEFAULT_STATUS = ScenarioExecution.Status.RUNNING; // Integer value: 1
    private static final ScenarioExecution.Status UPDATED_STATUS = ScenarioExecution.Status.SUCCESS; // Integer value: 2

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/scenario-executions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ScenarioExecutionRepository scenarioExecutionRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc mockMvc;

    private ScenarioExecution scenarioExecution;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScenarioExecution createEntity(EntityManager entityManager) throws ScenarioExecution.ErrorMessageTruncationException {
        ScenarioExecution scenarioExecution = ScenarioExecution.builder()
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .scenarioName(DEFAULT_SCENARIO_NAME)
            .status(DEFAULT_STATUS)
            .errorMessage(DEFAULT_ERROR_MESSAGE)
            .build();
        return scenarioExecution;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScenarioExecution createUpdatedEntity(EntityManager entityManager) throws ScenarioExecution.ErrorMessageTruncationException {
        ScenarioExecution scenarioExecution = ScenarioExecution.builder()
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .scenarioName(UPDATED_SCENARIO_NAME)
            .status(UPDATED_STATUS)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .build();
        return scenarioExecution;
    }

    @BeforeEach
    void beforeEachSetup() throws ScenarioExecution.ErrorMessageTruncationException {
        scenarioExecution = createEntity(entityManager);
    }

    @Test
    @Transactional
    void getAllScenarioExecutions() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList
        mockMvc
            .perform(get(ENTITY_API_URL + "?sort=executionId,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].executionId").value(hasItem(scenarioExecution.getExecutionId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].scenarioName").value(hasItem(DEFAULT_SCENARIO_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)));
    }

    @Test
    @Transactional
    void getScenarioExecution() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get the scenarioExecution
        mockMvc
            .perform(get(ENTITY_API_URL_ID, scenarioExecution.getExecutionId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.executionId").value(scenarioExecution.getExecutionId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.scenarioName").value(DEFAULT_SCENARIO_NAME))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE));
    }

    @Test
    @Transactional
    void getScenarioExecutionsByIdFiltering() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        Long executionId = scenarioExecution.getExecutionId();

        defaultScenarioExecutionShouldBeFound("executionId.equals=" + executionId);
        defaultScenarioExecutionShouldNotBeFound("executionId.notEquals=" + executionId);

        defaultScenarioExecutionShouldBeFound("executionId.greaterThanOrEqual=" + executionId);
        defaultScenarioExecutionShouldNotBeFound("executionId.greaterThan=" + executionId);

        defaultScenarioExecutionShouldBeFound("executionId.lessThanOrEqual=" + executionId);
        defaultScenarioExecutionShouldNotBeFound("executionId.lessThan=" + executionId);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where startDate equals to DEFAULT_START_DATE
        defaultScenarioExecutionShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the scenarioExecutionList where startDate equals to UPDATED_START_DATE
        defaultScenarioExecutionShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultScenarioExecutionShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the scenarioExecutionList where startDate equals to UPDATED_START_DATE
        defaultScenarioExecutionShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where startDate is not null
        defaultScenarioExecutionShouldBeFound("startDate.specified=true");

        // Get all the scenarioExecutionList where startDate is null
        defaultScenarioExecutionShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTestParametersByCreatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the testParameterList where startDate is greater than or equal to DEFAULT_CREATED_DATE
        defaultScenarioExecutionShouldBeFound("startDate.greaterThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the testParameterList where startDate is greater than or equal to UPDATED_CREATED_DATE
        defaultScenarioExecutionShouldNotBeFound("startDate.greaterThanOrEqual=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllTestParametersByCreatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the testParameterList where startDate is less than or equal to DEFAULT_CREATED_DATE
        defaultScenarioExecutionShouldBeFound("startDate.lessThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the testParameterList where startDate is less than or equal to SMALLER_CREATED_DATE
        defaultScenarioExecutionShouldNotBeFound("startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where endDate equals to DEFAULT_END_DATE
        defaultScenarioExecutionShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the scenarioExecutionList where endDate equals to UPDATED_END_DATE
        defaultScenarioExecutionShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultScenarioExecutionShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the scenarioExecutionList where endDate equals to UPDATED_END_DATE
        defaultScenarioExecutionShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where endDate is not null
        defaultScenarioExecutionShouldBeFound("endDate.specified=true");

        // Get all the scenarioExecutionList where endDate is null
        defaultScenarioExecutionShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTestParametersByLastModifiedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the testParameterList where endDate is greater than or equal to DEFAULT_LAST_MODIFIED_DATE
        defaultScenarioExecutionShouldBeFound("endDate.greaterThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the testParameterList where endDate is greater than or equal to UPDATED_LAST_MODIFIED_DATE
        defaultScenarioExecutionShouldNotBeFound("endDate.greaterThanOrEqual=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllTestParametersByLastModifiedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the testParameterList where endDate is less than or equal to DEFAULT_LAST_MODIFIED_DATE
        defaultScenarioExecutionShouldBeFound("endDate.lessThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the testParameterList where endDate is less than or equal to SMALLER_LAST_MODIFIED_DATE
        defaultScenarioExecutionShouldNotBeFound("endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioNameIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where scenarioName equals to DEFAULT_SCENARIO_NAME
        defaultScenarioExecutionShouldBeFound("scenarioName.equals=" + DEFAULT_SCENARIO_NAME);

        // Get all the scenarioExecutionList where scenarioName equals to UPDATED_SCENARIO_NAME
        defaultScenarioExecutionShouldNotBeFound("scenarioName.equals=" + UPDATED_SCENARIO_NAME);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioNameIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where scenarioName in DEFAULT_SCENARIO_NAME or UPDATED_SCENARIO_NAME
        defaultScenarioExecutionShouldBeFound("scenarioName.in=" + DEFAULT_SCENARIO_NAME + "," + UPDATED_SCENARIO_NAME);

        // Get all the scenarioExecutionList where scenarioName equals to UPDATED_SCENARIO_NAME
        defaultScenarioExecutionShouldNotBeFound("scenarioName.in=" + UPDATED_SCENARIO_NAME);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where scenarioName is not null
        defaultScenarioExecutionShouldBeFound("scenarioName.specified=true");

        // Get all the scenarioExecutionList where scenarioName is null
        defaultScenarioExecutionShouldNotBeFound("scenarioName.specified=false");
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioNameContainsSomething() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where scenarioName contains DEFAULT_SCENARIO_NAME
        defaultScenarioExecutionShouldBeFound("scenarioName.contains=" + DEFAULT_SCENARIO_NAME);

        // Get all the scenarioExecutionList where scenarioName contains UPDATED_SCENARIO_NAME
        defaultScenarioExecutionShouldNotBeFound("scenarioName.contains=" + UPDATED_SCENARIO_NAME);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioNameNotContainsSomething() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where scenarioName does not contain DEFAULT_SCENARIO_NAME
        defaultScenarioExecutionShouldNotBeFound("scenarioName.doesNotContain=" + DEFAULT_SCENARIO_NAME);

        // Get all the scenarioExecutionList where scenarioName does not contain UPDATED_SCENARIO_NAME
        defaultScenarioExecutionShouldBeFound("scenarioName.doesNotContain=" + UPDATED_SCENARIO_NAME);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where status equals to DEFAULT_STATUS
        defaultScenarioExecutionShouldBeFound("status.equals=" + DEFAULT_STATUS.getId());

        // Get all the scenarioExecutionList where status equals to UPDATED_STATUS
        defaultScenarioExecutionShouldNotBeFound("status.equals=" + UPDATED_STATUS.getId());
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultScenarioExecutionShouldBeFound("status.in=" + DEFAULT_STATUS.getId() + "," + UPDATED_STATUS.getId());

        // Get all the scenarioExecutionList where status equals to UPDATED_STATUS
        defaultScenarioExecutionShouldNotBeFound("status.in=" + UPDATED_STATUS.getId());
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where status is not null
        defaultScenarioExecutionShouldBeFound("status.specified=true");

        // Get all the scenarioExecutionList where status is null
        defaultScenarioExecutionShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByErrorMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where errorMessage equals to DEFAULT_ERROR_MESSAGE
        defaultScenarioExecutionShouldBeFound("errorMessage.equals=" + DEFAULT_ERROR_MESSAGE);

        // Get all the scenarioExecutionList where errorMessage equals to UPDATED_ERROR_MESSAGE
        defaultScenarioExecutionShouldNotBeFound("errorMessage.equals=" + UPDATED_ERROR_MESSAGE);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByErrorMessageIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where errorMessage in DEFAULT_ERROR_MESSAGE or UPDATED_ERROR_MESSAGE
        defaultScenarioExecutionShouldBeFound("errorMessage.in=" + DEFAULT_ERROR_MESSAGE + "," + UPDATED_ERROR_MESSAGE);

        // Get all the scenarioExecutionList where errorMessage equals to UPDATED_ERROR_MESSAGE
        defaultScenarioExecutionShouldNotBeFound("errorMessage.in=" + UPDATED_ERROR_MESSAGE);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByErrorMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where errorMessage is not null
        defaultScenarioExecutionShouldBeFound("errorMessage.specified=true");

        // Get all the scenarioExecutionList where errorMessage is null
        defaultScenarioExecutionShouldNotBeFound("errorMessage.specified=false");
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByErrorMessageContainsSomething() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where errorMessage contains DEFAULT_ERROR_MESSAGE
        defaultScenarioExecutionShouldBeFound("errorMessage.contains=" + DEFAULT_ERROR_MESSAGE);

        // Get all the scenarioExecutionList where errorMessage contains UPDATED_ERROR_MESSAGE
        defaultScenarioExecutionShouldNotBeFound("errorMessage.contains=" + UPDATED_ERROR_MESSAGE);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByErrorMessageNotContainsSomething() throws Exception {
        // Initialize the database
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);

        // Get all the scenarioExecutionList where errorMessage does not contain DEFAULT_ERROR_MESSAGE
        defaultScenarioExecutionShouldNotBeFound("errorMessage.doesNotContain=" + DEFAULT_ERROR_MESSAGE);

        // Get all the scenarioExecutionList where errorMessage does not contain UPDATED_ERROR_MESSAGE
        defaultScenarioExecutionShouldBeFound("errorMessage.doesNotContain=" + UPDATED_ERROR_MESSAGE);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioActionIsEqualToSomething() throws Exception {
        ScenarioAction scenarioAction;
        if (TestUtil.findAll(entityManager, ScenarioAction.class).isEmpty()) {
            scenarioExecutionRepository.saveAndFlush(scenarioExecution);
            scenarioAction = ScenarioActionResourceIT.createEntity(entityManager);
        } else {
            scenarioAction = TestUtil.findAll(entityManager, ScenarioAction.class).get(0);
        }
        entityManager.persist(scenarioAction);
        entityManager.flush();
        scenarioExecution.addScenarioAction(scenarioAction);
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);
        Long scenarioActionId = scenarioAction.getActionId();
        // Get all the scenarioExecutionList where scenarioAction equals to scenarioActionId
        defaultScenarioExecutionShouldBeFound("scenarioActionsId.equals=" + scenarioActionId);

        // Get all the scenarioExecutionList where scenarioAction equals to (scenarioActionId + 1)
        defaultScenarioExecutionShouldNotBeFound("scenarioActionsId.equals=" + (scenarioActionId + 1));
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioMessagesIsEqualToSomething() throws Exception {
        Message scenarioMessages;
        if (TestUtil.findAll(entityManager, Message.class).isEmpty()) {
            scenarioExecutionRepository.saveAndFlush(scenarioExecution);
            scenarioMessages = MessageResourceIT.createEntity(entityManager);
        } else {
            scenarioMessages = TestUtil.findAll(entityManager, Message.class).get(0);
        }
        entityManager.persist(scenarioMessages);
        entityManager.flush();
        scenarioExecution.addScenarioMessage(scenarioMessages);
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);
        Long scenarioMessagesId = scenarioMessages.getMessageId();
        // Get all the scenarioExecutionList where scenarioMessages equals to scenarioMessagesId
        defaultScenarioExecutionShouldBeFound("scenarioMessagesId.equals=" + scenarioMessagesId);

        // Get all the scenarioExecutionList where scenarioMessages equals to (scenarioMessagesId + 1)
        defaultScenarioExecutionShouldNotBeFound("scenarioMessagesId.equals=" + (scenarioMessagesId + 1));
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioParametersIsEqualToSomething() throws Exception {
        ScenarioParameter scenarioParameters;
        if (TestUtil.findAll(entityManager, ScenarioParameter.class).isEmpty()) {
            scenarioExecutionRepository.saveAndFlush(scenarioExecution);
            scenarioParameters = ScenarioParameterResourceIT.createEntity(entityManager);
        } else {
            scenarioParameters = TestUtil.findAll(entityManager, ScenarioParameter.class).get(0);
        }
        entityManager.persist(scenarioParameters);
        entityManager.flush();
        scenarioExecution.addScenarioParameter(scenarioParameters);
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);
        Long scenarioParametersId = scenarioParameters.getParameterId();
        // Get all the scenarioExecutionList where scenarioParameters equals to scenarioParametersId
        defaultScenarioExecutionShouldBeFound("scenarioParametersId.equals=" + scenarioParametersId);

        // Get all the scenarioExecutionList where scenarioParameters equals to (scenarioParametersId + 1)
        defaultScenarioExecutionShouldNotBeFound("scenarioParametersId.equals=" + (scenarioParametersId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultScenarioExecutionShouldBeFound(String filter) throws Exception {
        mockMvc
            .perform(get(ENTITY_API_URL + "?sort=executionId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].executionId").value(hasItem(scenarioExecution.getExecutionId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].scenarioName").value(hasItem(DEFAULT_SCENARIO_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)));

        // Check, that the count call also returns 1
        mockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=executionId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultScenarioExecutionShouldNotBeFound(String filter) throws Exception {
        mockMvc
            .perform(get(ENTITY_API_URL + "?sort=executionId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        mockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=executionId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingScenarioExecution() throws Exception {
        // Get the scenarioExecution
        mockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }
}
