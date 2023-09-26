package org.citrusframework.simulator.service;

import org.citrusframework.simulator.model.TestParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link org.citrusframework.simulator.model.TestParameter}.
 */
public interface TestParameterService {
    /**
     * Save a testParameter.
     *
     * @param testParameter the entity to save.
     * @return the persisted entity.
     */
    TestParameter save(TestParameter testParameter);

    /**
     * Get all the testParameters.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TestParameter> findAll(Pageable pageable);

    /**
     * Get the "id" testParameter.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TestParameter> findOne(Long testResultId, String key);
}
