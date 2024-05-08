/*
 * Copyright 2023 the original author or authors.
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

package org.citrusframework.simulator.service;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.model.TestResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * Service Interface for managing {@link ScenarioExecution}.
 */
public interface ScenarioExecutionService {

    /**
     * Save a scenarioExecution.
     *
     * @param scenarioExecution the entity to save.
     * @return the persisted entity.
     */
    ScenarioExecution save(ScenarioExecution scenarioExecution);

    /**
     * Get all the scenarioExecutions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ScenarioExecution> findAll(Pageable pageable);

    /**
     * Get the "id" scenarioExecution.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ScenarioExecution> findOne(Long id);

    /**
     * Get the "id" scenarioExecution with explicit lazy fetched relationships.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ScenarioExecution> findOneLazy(Long id);

    /**
     * Creates a new {@link ScenarioExecution}, persisting it within the database.
     *
     * @param scenarioName       the name of the scenario.
     * @param scenarioParameters the scenario's start parameters.
     * @return the new entity.
     */
    ScenarioExecution createAndSaveExecutionScenario(String scenarioName, @Nullable List<ScenarioParameter> scenarioParameters);

    /**
     * Complete a {@link ScenarioExecution} with the given {@link TestResult}.
     *
     * @param scenarioExecutionId the id of the entity.
     * @param testResult          the result of the execution.
     * @return the updated entity.
     */
    ScenarioExecution completeScenarioExecution(long scenarioExecutionId, TestResult testResult);
}
