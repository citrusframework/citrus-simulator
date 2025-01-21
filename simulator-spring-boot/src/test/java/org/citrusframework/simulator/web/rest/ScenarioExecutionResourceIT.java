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

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.assertj.core.data.TemporalUnitLessThanOffset;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.ScenarioAction;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioExecution.ScenarioExecutionBuilder;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.model.TestResult;
import org.citrusframework.simulator.repository.ScenarioExecutionRepository;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.citrusframework.simulator.model.TestResult.Status.FAILURE;
import static org.citrusframework.simulator.model.TestResult.Status.SUCCESS;
import static org.citrusframework.simulator.web.rest.MessageResourceIT.DEFAULT_PAYLOAD;
import static org.citrusframework.simulator.web.rest.MessageResourceIT.UPDATED_PAYLOAD;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpStatus.OK;
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

    private static final TestResult.Status DEFAULT_STATUS = SUCCESS; // Integer value: 1
    private static final TestResult.Status UPDATED_STATUS = FAILURE; // Integer value: 2

    private static final String ENTITY_API_URL = "/api/scenario-executions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ScenarioExecutionRepository scenarioExecutionRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc mockMvc;

    private ScenarioExecution scenarioExecution;

    public static ScenarioExecutionBuilder createEntityBuilder(EntityManager entityManager) {
        return ScenarioExecution.builder()
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .scenarioName(DEFAULT_SCENARIO_NAME);
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScenarioExecution createEntity(EntityManager entityManager) {
        return createEntityBuilder(entityManager)
            .build();
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScenarioExecution createUpdatedEntity(EntityManager entityManager) {
        return ScenarioExecution.builder()
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .scenarioName(UPDATED_SCENARIO_NAME)
            .build();
    }

    @BeforeEach
    void beforeEachSetup() {
        var scenarioParameter = ScenarioParameterResourceIT.createEntity(entityManager);
        var testResult = TestResultResourceIT.createEntity(entityManager);
        var scenarioAction = ScenarioActionResourceIT.createEntity(entityManager);
        var message = MessageHeaderResourceIT.createEntity(entityManager).getMessage();

        scenarioExecution = createEntity(entityManager)
            .withTestResult(testResult)
            .addScenarioParameter(scenarioParameter)
            .addScenarioAction(scenarioAction)
            .addScenarioMessage(message);

        scenarioExecutionRepository.saveAndFlush(scenarioExecution);
    }

    @Test
    @Transactional
    void getAllScenarioExecutions_withAllDetails() throws Exception {
        // Get all the scenarioExecutionList
        mockMvc
            .perform(get(ENTITY_API_URL + "?sort=executionId,desc&includeActions=true&includeMessages=true&includeMessageHeaders=true&includeParameters=true"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").value(hasSize(1)))
            .andExpect(jsonPath("$.[0].executionId").value(equalTo(scenarioExecution.getExecutionId().intValue())))
            .andExpect(jsonPath("$.[0].startDate").value(equalTo(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[0].endDate").value(equalTo(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[0].scenarioName").value(equalTo(DEFAULT_SCENARIO_NAME)))
            .andExpect(jsonPath("$.[0].testResult.status").value(equalTo(SUCCESS.toString())))
            .andExpect(jsonPath("$.[0].scenarioParameters").value(hasSize(1)))
            .andExpect(jsonPath("$.[0].scenarioActions").value(hasSize(1)))
            .andExpect(jsonPath("$.[0].scenarioMessages").value(hasSize(1)))
            .andExpect(jsonPath("$.[0].scenarioMessages[0].headers").value(hasSize(1)));
    }

    @Test
    @Transactional
    void getScenarioExecution() throws Exception {
        // Get the scenarioExecution
        mockMvc
            .perform(get(ENTITY_API_URL_ID, scenarioExecution.getExecutionId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.executionId").value(scenarioExecution.getExecutionId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.scenarioName").value(DEFAULT_SCENARIO_NAME))
            .andExpect(jsonPath("$.testResult.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getScenarioExecutionsByIdFiltering() throws Exception {
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
        // Get all the scenarioExecutionList where startDate equals to DEFAULT_START_DATE
        defaultScenarioExecutionShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the scenarioExecutionList where startDate equals to UPDATED_START_DATE
        defaultScenarioExecutionShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByStartDateIsInShouldWork() throws Exception {
        // Get all the scenarioExecutionList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultScenarioExecutionShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the scenarioExecutionList where startDate equals to UPDATED_START_DATE
        defaultScenarioExecutionShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByStartDateIsNullOrNotNull() throws Exception {
        // Get all the scenarioExecutionList where startDate is not null
        defaultScenarioExecutionShouldBeFound("startDate.specified=true");

        // Get all the scenarioExecutionList where startDate is null
        defaultScenarioExecutionShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTestParametersByCreatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Get all the testParameterList where startDate is greater than or equal to DEFAULT_CREATED_DATE
        defaultScenarioExecutionShouldBeFound("startDate.greaterThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the testParameterList where startDate is greater than or equal to UPDATED_CREATED_DATE
        defaultScenarioExecutionShouldNotBeFound("startDate.greaterThanOrEqual=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllTestParametersByCreatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Get all the testParameterList where startDate is less than or equal to DEFAULT_CREATED_DATE
        defaultScenarioExecutionShouldBeFound("startDate.lessThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the testParameterList where startDate is less than or equal to SMALLER_CREATED_DATE
        defaultScenarioExecutionShouldNotBeFound("startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByEndDateIsEqualToSomething() throws Exception {
        // Get all the scenarioExecutionList where endDate equals to DEFAULT_END_DATE
        defaultScenarioExecutionShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the scenarioExecutionList where endDate equals to UPDATED_END_DATE
        defaultScenarioExecutionShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByEndDateIsInShouldWork() throws Exception {
        // Get all the scenarioExecutionList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultScenarioExecutionShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the scenarioExecutionList where endDate equals to UPDATED_END_DATE
        defaultScenarioExecutionShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByEndDateIsNullOrNotNull() throws Exception {
        // Get all the scenarioExecutionList where endDate is not null
        defaultScenarioExecutionShouldBeFound("endDate.specified=true");

        // Get all the scenarioExecutionList where endDate is null
        defaultScenarioExecutionShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTestParametersByLastModifiedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Get all the testParameterList where endDate is greater than or equal to DEFAULT_LAST_MODIFIED_DATE
        defaultScenarioExecutionShouldBeFound("endDate.greaterThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the testParameterList where endDate is greater than or equal to UPDATED_LAST_MODIFIED_DATE
        defaultScenarioExecutionShouldNotBeFound("endDate.greaterThanOrEqual=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllTestParametersByLastModifiedDateIsLessThanOrEqualToSomething() throws Exception {
        // Get all the testParameterList where endDate is less than or equal to DEFAULT_LAST_MODIFIED_DATE
        defaultScenarioExecutionShouldBeFound("endDate.lessThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the testParameterList where endDate is less than or equal to SMALLER_LAST_MODIFIED_DATE
        defaultScenarioExecutionShouldNotBeFound("endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioNameIsEqualToSomething() throws Exception {
        // Get all the scenarioExecutionList where scenarioName equals to DEFAULT_SCENARIO_NAME
        defaultScenarioExecutionShouldBeFound("scenarioName.equals=" + DEFAULT_SCENARIO_NAME);

        // Get all the scenarioExecutionList where scenarioName equals to UPDATED_SCENARIO_NAME
        defaultScenarioExecutionShouldNotBeFound("scenarioName.equals=" + UPDATED_SCENARIO_NAME);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioNameIsInShouldWork() throws Exception {
        // Get all the scenarioExecutionList where scenarioName in DEFAULT_SCENARIO_NAME or UPDATED_SCENARIO_NAME
        defaultScenarioExecutionShouldBeFound("scenarioName.in=" + DEFAULT_SCENARIO_NAME + "," + UPDATED_SCENARIO_NAME);

        // Get all the scenarioExecutionList where scenarioName equals to UPDATED_SCENARIO_NAME
        defaultScenarioExecutionShouldNotBeFound("scenarioName.in=" + UPDATED_SCENARIO_NAME);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioNameIsNullOrNotNull() throws Exception {
        // Get all the scenarioExecutionList where scenarioName is not null
        defaultScenarioExecutionShouldBeFound("scenarioName.specified=true");

        // Get all the scenarioExecutionList where scenarioName is null
        defaultScenarioExecutionShouldNotBeFound("scenarioName.specified=false");
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioNameContainsSomething() throws Exception {
        // Get all the scenarioExecutionList where scenarioName contains DEFAULT_SCENARIO_NAME
        defaultScenarioExecutionShouldBeFound("scenarioName.contains=" + DEFAULT_SCENARIO_NAME);

        // Get all the scenarioExecutionList where scenarioName contains UPDATED_SCENARIO_NAME
        defaultScenarioExecutionShouldNotBeFound("scenarioName.contains=" + UPDATED_SCENARIO_NAME);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioNameNotContainsSomething() throws Exception {
        // Get all the scenarioExecutionList where scenarioName does not contain DEFAULT_SCENARIO_NAME
        defaultScenarioExecutionShouldNotBeFound("scenarioName.doesNotContain=" + DEFAULT_SCENARIO_NAME);

        // Get all the scenarioExecutionList where scenarioName does not contain UPDATED_SCENARIO_NAME
        defaultScenarioExecutionShouldBeFound("scenarioName.doesNotContain=" + UPDATED_SCENARIO_NAME);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByStatusIsEqualToSomething() throws Exception {
        // Get all the scenarioExecutionList where status equals to DEFAULT_STATUS
        defaultScenarioExecutionShouldBeFound("status.equals=" + DEFAULT_STATUS.getId());

        // Get all the scenarioExecutionList where status equals to UPDATED_STATUS
        defaultScenarioExecutionShouldNotBeFound("status.equals=" + UPDATED_STATUS.getId());
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByStatusIsInShouldWork() throws Exception {
        // Get all the scenarioExecutionList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultScenarioExecutionShouldBeFound("status.in=" + DEFAULT_STATUS.getId() + "," + UPDATED_STATUS.getId());

        // Get all the scenarioExecutionList where status equals to UPDATED_STATUS
        defaultScenarioExecutionShouldNotBeFound("status.in=" + UPDATED_STATUS.getId());
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByStatusIsNullOrNotNull() throws Exception {
        // Get all the scenarioExecutionList where status is not null
        defaultScenarioExecutionShouldBeFound("status.specified=true");

        // Get all the scenarioExecutionList where status is null
        defaultScenarioExecutionShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioActionIsEqualToSomething() throws Exception {
        Long scenarioActionId = TestUtil.findAll(entityManager, ScenarioAction.class).get(0).getActionId();

        // Get all the scenarioExecutionList where scenarioAction equals to scenarioActionId
        defaultScenarioExecutionShouldBeFound("scenarioActionsId.equals=" + scenarioActionId);

        // Get all the scenarioExecutionList where scenarioAction equals to (scenarioActionId + 1)
        defaultScenarioExecutionShouldNotBeFound("scenarioActionsId.equals=" + (scenarioActionId + 1));
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioMessagesIsEqualToSomething() throws Exception {
        Long scenarioMessagesId = TestUtil.findAll(entityManager, Message.class).get(0).getMessageId();

        // Get all the scenarioExecutionList where scenarioMessages equals to scenarioMessagesId
        defaultScenarioExecutionShouldBeFound("scenarioMessagesId.equals=" + scenarioMessagesId);

        // Get all the scenarioExecutionList where scenarioMessages equals to (scenarioMessagesId + 1)
        defaultScenarioExecutionShouldNotBeFound("scenarioMessagesId.equals=" + (scenarioMessagesId + 1));
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioMessageDirectionIsEqualToSomething() throws Exception {
        Message scenarioMessages;
        if (TestUtil.findAll(entityManager, Message.class).isEmpty()) {
            scenarioExecutionRepository.saveAndFlush(scenarioExecution);
            scenarioMessages = MessageResourceIT.createEntity(entityManager);
        } else {
            scenarioMessages = TestUtil.findAll(entityManager, Message.class).get(0);
        }
        scenarioMessages.setDirection(Message.Direction.INBOUND);
        entityManager.persist(scenarioMessages);
        entityManager.flush();
        scenarioExecution.addScenarioMessage(scenarioMessages);
        scenarioExecutionRepository.saveAndFlush(scenarioExecution);
        int scenarioMessageDirection = scenarioMessages.getDirection().getId();
        // Get all the scenarioExecutionList where scenarioMessagesDirection equals to scenarioMessagesDirection
        defaultScenarioExecutionShouldBeFound("scenarioMessagesDirection.equals=" + scenarioMessageDirection);

        // Get all the scenarioExecutionList where scenarioMessagesDirection equals to (scenarioMessagesDirection + 1)
        defaultScenarioExecutionShouldNotBeFound("scenarioMessagesDirection.equals=" + (Message.Direction.UNKNOWN.getId()));
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioMessagesPayloadIsEqualToSomething() throws Exception {
        // Get all the scenarioExecutionList where payload equals the default payload
        defaultScenarioExecutionShouldBeFound("scenarioMessagesPayload.equals=" + DEFAULT_PAYLOAD);

        // Get all the scenarioExecutionList where payload equals another payload
        defaultScenarioExecutionShouldNotBeFound("scenarioMessagesPayload.equals=" + UPDATED_PAYLOAD);
    }

    @Test
    @Transactional
    void getAllScenarioExecutionsByScenarioParametersIsEqualToSomething() throws Exception {
        Long scenarioParametersId = TestUtil.findAll(entityManager, ScenarioParameter.class).get(0).getParameterId();

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
            .perform(get(ENTITY_API_URL + "?sort=executionId,desc&" + filter + "&includeActions=true&includeMessages=true&includeParameters=true"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].executionId").value(hasItem(scenarioExecution.getExecutionId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].scenarioName").value(hasItem(DEFAULT_SCENARIO_NAME)))
            .andExpect(jsonPath("$.[*].testResult.status").value(DEFAULT_STATUS.toString()));

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

    @Nested
    class CorrectTimeOnScenarioExecution {

        public static final TemporalUnitLessThanOffset LESS_THAN_5_SECONDS = new TemporalUnitLessThanOffset(5, SECONDS);

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private MockMvc mockMvc;

        @Test
        @Transactional
        void shouldInvokeScenario() throws Exception {
            String mockEndpointResult = mockMvc
                .perform(get("/services/rest/api/v1/ZmNrqCkoGQ"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

            assertThat(mockEndpointResult).contains("E5a084sOZw7");

            String scenarioExecutionsResult = mockMvc
                .perform(get("/api/scenario-executions?includeActions=true&includeMessages=true&scenarioMessagesPayload.equals=E5a084sOZw7"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

            List<ScenarioExecution> scenarioExecutions = objectMapper.readValue(scenarioExecutionsResult, ScenarioExecutions.class);

            assertThat(scenarioExecutions)
                .hasSize(1)
                .anySatisfy(execution -> {
                    assertThat(execution.startDate()).isCloseTo(now(), LESS_THAN_5_SECONDS);
                    assertThat(execution.endDate()).isCloseTo(now(), LESS_THAN_5_SECONDS);
                    assertThat(execution.scenarioActions()).allSatisfy(action -> {
                        assertThat(action.startDate()).isCloseTo(now(), LESS_THAN_5_SECONDS);
                        assertThat(action.endDate()).isCloseTo(now(), LESS_THAN_5_SECONDS);
                    });
                    assertThat(execution.scenarioMessages()).anySatisfy(action -> {
                        assertThat(action.createdDate()).isCloseTo(now(), LESS_THAN_5_SECONDS);
                    });
                });
        }

        @Scenario("DEFAULT_SCENARIO")
        public static class HelloScenario extends AbstractSimulatorScenario {

            @Override
            public void run(ScenarioRunner scenario) {
                scenario.$(scenario.http()
                    .receive()
                    .get()
                    .path("/services/rest/api/v1/ZmNrqCkoGQ"));

                scenario.$(scenario.http()
                    .send()
                    .response(OK)
                    .message()
                    .body("E5a084sOZw7"));
            }
        }


        public static class ScenarioExecutions extends ArrayList<ScenarioExecution> {
        }

        public record ScenarioExecution(
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<ScenarioActions> scenarioActions,
            List<ScenarioMessages> scenarioMessages
        ) {

        }

        public record ScenarioActions(
            LocalDateTime startDate,
            LocalDateTime endDate
        ) {

        }

        public record ScenarioMessages(
            LocalDateTime createdDate
        ) {

        }
    }
}
