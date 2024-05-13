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

package org.citrusframework.simulator.repository;

import org.citrusframework.simulator.model.ScenarioAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link ScenarioAction} entity.
 */
@Repository
public interface ScenarioActionRepository extends JpaRepository<ScenarioAction, Long>, JpaSpecificationExecutor<ScenarioAction> {

    default Optional<ScenarioAction> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    @Query(
        value = "select scenarioAction from ScenarioAction scenarioAction left join fetch scenarioAction.scenarioExecution",
        countQuery = "select count(scenarioAction) from ScenarioAction scenarioAction"
    )
    Page<ScenarioAction> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select scenarioAction from ScenarioAction scenarioAction left join fetch scenarioAction.scenarioExecution where scenarioAction.actionId =:actionId"
    )
    Optional<ScenarioAction> findOneWithToOneRelationships(@Param("actionId") Long actionId);
}
