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

import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.model.ScenarioAction;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.web.rest.ScenarioExecutionResourceIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.citrusframework.simulator.web.rest.ScenarioActionResourceIT.createEntity;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.data.domain.Pageable.unpaged;

@IntegrationTest
class ScenarioActionRepositoryIT {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ScenarioActionRepository scenarioActionRepository;

    private ScenarioAction scenarioAction;

    private static void verifyRelationships(ScenarioAction scenarioAction) {
        assertNotNull(scenarioAction.getScenarioExecution());
    }

    @BeforeEach
    void beforeEachSetup() {
        scenarioAction = createEntity(entityManager);

        ScenarioExecution scenarioExecution = ScenarioExecutionResourceIT.createEntity(entityManager);
        entityManager.persist(scenarioExecution);
        scenarioAction.setScenarioExecution(scenarioExecution);

        entityManager.persist(scenarioAction);
    }

    @Test
    @Transactional
    void testFindAllWithToOneRelationships() {
        Page<ScenarioAction> scenarioActions = scenarioActionRepository.findAllWithToOneRelationships(unpaged());

        assertTrue(scenarioActions.hasContent());
        verifyRelationships(scenarioActions.getContent().get(0));
    }

    @Test
    @Transactional
    void testFindOneWithToOneRelationships() {
        Optional<ScenarioAction> scenarioActions = scenarioActionRepository.findOneWithToOneRelationships(scenarioAction.getActionId());

        assertTrue(scenarioActions.isPresent());
        verifyRelationships(scenarioActions.get());

        assertFalse(scenarioActionRepository.findOneWithToOneRelationships(Long.MAX_VALUE).isPresent());
    }
}
