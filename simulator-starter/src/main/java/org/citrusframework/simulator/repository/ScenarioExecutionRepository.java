package org.citrusframework.simulator.repository;

import org.citrusframework.simulator.model.ScenarioExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link ScenarioExecution} entity.
 */
@Repository
public interface ScenarioExecutionRepository extends JpaRepository<ScenarioExecution, Long>, JpaSpecificationExecutor<ScenarioExecution> {

    @Override
    @EntityGraph(attributePaths = {"scenarioParameters", "scenarioActions", "scenarioMessages"})
    Page<ScenarioExecution> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"scenarioParameters", "scenarioActions", "scenarioMessages"})
    Page<ScenarioExecution> findAll(Specification<ScenarioExecution> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"scenarioParameters", "scenarioActions", "scenarioMessages"})
    Optional<ScenarioExecution> findById(Long id);
}
