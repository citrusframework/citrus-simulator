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
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.citrusframework.simulator.web.rest.ScenarioActionResourceIT.createEntity;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        Page<ScenarioAction> scenarioActions = scenarioActionRepository.findAllWithToOneRelationships(Pageable.unpaged());

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
