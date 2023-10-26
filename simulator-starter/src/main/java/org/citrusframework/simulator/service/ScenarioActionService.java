package org.citrusframework.simulator.service;

import org.citrusframework.simulator.model.ScenarioAction;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Objects;
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
     * Function that converts the {@link ScenarioAction} to its "DTO-form": It may only contain the {@code scenarioName}
     * of the related {@link ScenarioExecution}, no further attributes. That is especially true for relationships,
     * because of a possible {@link org.hibernate.LazyInitializationException}).
     *
     * @param scenarioAction The entity, which should be returned
     * @return the entity with prepared {@link ScenarioExecution}
     */
    static ScenarioAction restrictToDtoProperties(ScenarioAction scenarioAction) {
        ScenarioExecution scenarioExecution = scenarioAction.getScenarioExecution();
        if (!Objects.isNull(scenarioExecution)) {
            scenarioAction.setScenarioExecution(ScenarioExecution.builder().scenarioName(scenarioExecution.getScenarioName()).build());
        }
        return scenarioAction;
    }
}
