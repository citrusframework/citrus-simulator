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

import org.citrusframework.simulator.model.ScenarioParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link ScenarioParameter}.
 */
public interface ScenarioParameterService {

    /**
     * Save a scenarioParameter.
     *
     * @param scenarioParameter the entity to save.
     * @return the persisted entity.
     */
    ScenarioParameter save(ScenarioParameter scenarioParameter);

    /**
     * Get all the scenarioParameters.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ScenarioParameter> findAll(Pageable pageable);

    /**
     * Get the "id" scenarioParameter.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ScenarioParameter> findOne(Long id);

    /**
     * Delete the "id" scenarioParameter.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
