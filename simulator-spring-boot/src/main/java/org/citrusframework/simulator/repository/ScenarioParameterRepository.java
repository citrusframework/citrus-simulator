/*
 * Copyright 2024 the original author or authors.
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

package org.citrusframework.simulator.repository;

import org.citrusframework.simulator.model.ScenarioParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link ScenarioParameter} entity.
 */
@Repository
public interface ScenarioParameterRepository extends JpaRepository<ScenarioParameter, Long>, JpaSpecificationExecutor<ScenarioParameter> {

    default Optional<ScenarioParameter> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default Page<ScenarioParameter> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select scenarioParameter from ScenarioParameter scenarioParameter left join fetch scenarioParameter.scenarioExecution",
        countQuery = "select count(scenarioParameter) from ScenarioParameter scenarioParameter"
    )
    Page<ScenarioParameter> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select scenarioParameter from ScenarioParameter scenarioParameter left join fetch scenarioParameter.scenarioExecution where scenarioParameter.id =:id"
    )
    Optional<ScenarioParameter> findOneWithToOneRelationships(@Param("id") Long id);
}
