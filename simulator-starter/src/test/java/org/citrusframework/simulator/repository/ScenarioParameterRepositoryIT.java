package org.citrusframework.simulator.repository;

import jakarta.persistence.EntityManager;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.web.rest.ScenarioExecutionResourceIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.citrusframework.simulator.web.rest.ScenarioParameterResourceIT.createEntity;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
class ScenarioParameterRepositoryIT {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ScenarioParameterRepository scenarioParameterRepository;

    private ScenarioParameter scenarioParameter;

    private static void verifyRelationships(ScenarioParameter scenarioParameter) {
        assertNotNull(scenarioParameter.getScenarioExecution());
    }

    @BeforeEach
    void beforeEachSetup() {
        scenarioParameter = createEntity(entityManager);

        ScenarioExecution scenarioExecution = ScenarioExecutionResourceIT.createEntity(entityManager);
        entityManager.persist(scenarioExecution);
        scenarioParameter.setScenarioExecution(scenarioExecution);

        entityManager.persist(scenarioParameter);
    }

    @Test
    @Transactional
    void testFindAllWithToOneRelationships() {
        Page<ScenarioParameter> scenarioParameters = scenarioParameterRepository.findAllWithToOneRelationships(Pageable.unpaged());

        assertTrue(scenarioParameters.hasContent());
        verifyRelationships(scenarioParameters.getContent().get(0));
    }

    @Test
    @Transactional
    void testFindOneWithToOneRelationships() {
        Optional<ScenarioParameter> scenarioParameters = scenarioParameterRepository.findOneWithToOneRelationships(scenarioParameter.getParameterId());

        assertTrue(scenarioParameters.isPresent());
        verifyRelationships(scenarioParameters.get());

        assertFalse(scenarioParameterRepository.findOneWithToOneRelationships(Long.MAX_VALUE).isPresent());
    }
}
