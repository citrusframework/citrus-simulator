package org.citrusframework.simulator.service;

import org.citrusframework.simulator.model.ScenarioExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

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
}
