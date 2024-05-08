/*
 * Copyright 2023-2024 the original author or authors.
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
import org.citrusframework.TestAction;
import org.citrusframework.TestCase;
import org.citrusframework.simulator.model.ScenarioAction;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link ScenarioAction}.
 */
public interface ScenarioActionService {

    /**
     * Save a scenarioAction.
     *
     * @param scenarioAction the entity to save.
     * @return the persisted entity.
     */
    ScenarioAction save(ScenarioAction scenarioAction);

    /**
     * Get all the scenarioActions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ScenarioAction> findAll(Pageable pageable);

    /**
     * Get the "id" scenarioAction.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ScenarioAction> findOne(Long id);

    /**
     * Create and save the new {@link ScenarioAction}, linked to the given {@link ScenarioExecution}. The latter is
     * being determined based on the given {@link TestCase}. Detail information is bein extracted from
     * the {@link TestAction}.
     * <p>
     * Beware that the method is allowed to skip persistence for <i>some</i> test actions, in which case it will
     * return {@code null}.
     *
     * @param testCase the testcase identifying the entity.
     * @param testAction the test action to attach to the scenario execution.
     * @return the persisted entity or null.
     */
    @Nullable ScenarioAction createForScenarioExecutionAndSave(TestCase testCase, TestAction testAction);

    /**
     * Complete the {@link ScenarioAction}, linked to the given {@link ScenarioExecution}. The latter is
     * being determined based on the given {@link TestCase}.
     *
     * @param testCase the testcase identifying the entity.
     * @param testAction the test action to attach to the scenario execution.
     */
    void completeTestAction(TestCase testCase, TestAction testAction);
}
