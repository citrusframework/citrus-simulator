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
