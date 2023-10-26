package org.citrusframework.simulator.web.rest;

import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.model.ScenarioAction;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.repository.ScenarioActionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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
 * Integration tests for the {@link ScenarioActionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class ScenarioActionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/scenario-actions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ScenarioActionRepository scenarioActionRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc restScenarioActionMockMvc;

    private ScenarioAction scenarioAction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScenarioAction createEntity(EntityManager entityManager) {
        return ScenarioAction.builder()
            .name(DEFAULT_NAME)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .build();
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScenarioAction createUpdatedEntity(EntityManager entityManager) {
        return ScenarioAction.builder()
            .name(UPDATED_NAME)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .build();
    }

    @BeforeEach
    public void initTest() {
        scenarioAction = createEntity(entityManager);
    }

    @Test
    @Transactional
    void getAllScenarioActions() throws Exception {
        // Initialize the database
        scenarioActionRepository.saveAndFlush(scenarioAction);

        // Get all the scenarioActionList
        restScenarioActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=actionId,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].actionId").value(hasItem(scenarioAction.getActionId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }

    @Test
    @Transactional
    void getScenarioAction() throws Exception {
        // Initialize the database
        scenarioActionRepository.saveAndFlush(scenarioAction);

        // Get the scenarioAction
        restScenarioActionMockMvc
            .perform(get(ENTITY_API_URL_ID, scenarioAction.getActionId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.actionId").value(scenarioAction.getActionId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()));
    }

    @Test
    @Transactional
    void getScenarioActionsByIdFiltering() throws Exception {
        // Initialize the database
        scenarioActionRepository.saveAndFlush(scenarioAction);

        Long actionId = scenarioAction.getActionId();

        defaultScenarioActionShouldBeFound("actionId.equals=" + actionId);
        defaultScenarioActionShouldNotBeFound("actionId.notEquals=" + actionId);

        defaultScenarioActionShouldBeFound("actionId.greaterThanOrEqual=" + actionId);
        defaultScenarioActionShouldNotBeFound("actionId.greaterThan=" + actionId);

        defaultScenarioActionShouldBeFound("actionId.lessThanOrEqual=" + actionId);
        defaultScenarioActionShouldNotBeFound("actionId.lessThan=" + actionId);
    }

    @Test
    @Transactional
    void getAllScenarioActionsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioActionRepository.saveAndFlush(scenarioAction);

        // Get all the scenarioActionList where name equals to DEFAULT_NAME
        defaultScenarioActionShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the scenarioActionList where name equals to UPDATED_NAME
        defaultScenarioActionShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllScenarioActionsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioActionRepository.saveAndFlush(scenarioAction);

        // Get all the scenarioActionList where name in DEFAULT_NAME or UPDATED_NAME
        defaultScenarioActionShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the scenarioActionList where name equals to UPDATED_NAME
        defaultScenarioActionShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllScenarioActionsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioActionRepository.saveAndFlush(scenarioAction);

        // Get all the scenarioActionList where name is not null
        defaultScenarioActionShouldBeFound("name.specified=true");

        // Get all the scenarioActionList where name is null
        defaultScenarioActionShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllScenarioActionsByNameContainsSomething() throws Exception {
        // Initialize the database
        scenarioActionRepository.saveAndFlush(scenarioAction);

        // Get all the scenarioActionList where name contains DEFAULT_NAME
        defaultScenarioActionShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the scenarioActionList where name contains UPDATED_NAME
        defaultScenarioActionShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllScenarioActionsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        scenarioActionRepository.saveAndFlush(scenarioAction);

        // Get all the scenarioActionList where name does not contain DEFAULT_NAME
        defaultScenarioActionShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the scenarioActionList where name does not contain UPDATED_NAME
        defaultScenarioActionShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllScenarioActionsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioActionRepository.saveAndFlush(scenarioAction);

        // Get all the scenarioActionList where startDate equals to DEFAULT_START_DATE
        defaultScenarioActionShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the scenarioActionList where startDate equals to UPDATED_START_DATE
        defaultScenarioActionShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioActionsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioActionRepository.saveAndFlush(scenarioAction);

        // Get all the scenarioActionList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultScenarioActionShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the scenarioActionList where startDate equals to UPDATED_START_DATE
        defaultScenarioActionShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioActionsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioActionRepository.saveAndFlush(scenarioAction);

        // Get all the scenarioActionList where startDate is not null
        defaultScenarioActionShouldBeFound("startDate.specified=true");

        // Get all the scenarioActionList where startDate is null
        defaultScenarioActionShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllScenarioActionsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioActionRepository.saveAndFlush(scenarioAction);

        // Get all the scenarioActionList where endDate equals to DEFAULT_END_DATE
        defaultScenarioActionShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the scenarioActionList where endDate equals to UPDATED_END_DATE
        defaultScenarioActionShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioActionsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioActionRepository.saveAndFlush(scenarioAction);

        // Get all the scenarioActionList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultScenarioActionShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the scenarioActionList where endDate equals to UPDATED_END_DATE
        defaultScenarioActionShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioActionsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioActionRepository.saveAndFlush(scenarioAction);

        // Get all the scenarioActionList where endDate is not null
        defaultScenarioActionShouldBeFound("endDate.specified=true");

        // Get all the scenarioActionList where endDate is null
        defaultScenarioActionShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllScenarioActionsByScenarioExecutionIsEqualToSomething() throws Exception {
        ScenarioExecution scenarioExecution;
        if (TestUtil.findAll(entityManager, ScenarioExecution.class).isEmpty()) {
            scenarioActionRepository.saveAndFlush(scenarioAction);
            scenarioExecution = ScenarioExecutionResourceIT.createEntity(entityManager);
        } else {
            scenarioExecution = TestUtil.findAll(entityManager, ScenarioExecution.class).get(0);
        }
        entityManager.persist(scenarioExecution);
        entityManager.flush();
        scenarioAction.setScenarioExecution(scenarioExecution);
        scenarioActionRepository.saveAndFlush(scenarioAction);
        Long scenarioExecutionId = scenarioExecution.getExecutionId();
        // Get all the scenarioActionList where scenarioExecution equals to scenarioExecutionId
        defaultScenarioActionShouldBeFound("scenarioExecutionId.equals=" + scenarioExecutionId);

        // Get all the scenarioActionList where scenarioExecution equals to (scenarioExecutionId + 1)
        defaultScenarioActionShouldNotBeFound("scenarioExecutionId.equals=" + (scenarioExecutionId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultScenarioActionShouldBeFound(String filter) throws Exception {
        restScenarioActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=actionId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].actionId").value(hasItem(scenarioAction.getActionId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));

        // Check, that the count call also returns 1
        restScenarioActionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=actionId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultScenarioActionShouldNotBeFound(String filter) throws Exception {
        restScenarioActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=actionId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restScenarioActionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=actionId,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingScenarioAction() throws Exception {
        // Get the scenarioAction
        restScenarioActionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }
}
