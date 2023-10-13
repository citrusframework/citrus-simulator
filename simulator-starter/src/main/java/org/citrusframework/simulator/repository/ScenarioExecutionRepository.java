package org.citrusframework.simulator.repository;

import org.citrusframework.simulator.model.ScenarioExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link ScenarioExecution} entity.
 */
@Repository
public interface ScenarioExecutionRepository extends JpaRepository<ScenarioExecution, Long>, JpaSpecificationExecutor<ScenarioExecution> {}
