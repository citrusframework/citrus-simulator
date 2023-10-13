package org.citrusframework.simulator.repository;

import org.citrusframework.simulator.model.TestParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link TestParameter} entity.
 */
@Repository
public interface TestParameterRepository extends JpaRepository<TestParameter, TestParameter.TestParameterId>, JpaSpecificationExecutor<TestParameter> {}
