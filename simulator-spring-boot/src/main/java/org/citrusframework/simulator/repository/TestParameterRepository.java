package org.citrusframework.simulator.repository;

import org.citrusframework.simulator.model.TestParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link TestParameter} entity.
 */
@Repository
public interface TestParameterRepository extends JpaRepository<TestParameter, TestParameter.TestParameterId>, JpaSpecificationExecutor<TestParameter> {

    default Optional<TestParameter> findByCompositeId(Long testResultId, String key) {
        return findOneByTestParameterIdTestResultIdEqualsAndTestParameterIdKeyEquals(testResultId, key);
    }

    Optional<TestParameter> findOneByTestParameterIdTestResultIdEqualsAndTestParameterIdKeyEquals(@Param("testResultId") Long testResultId, @Param("key") String key);
}
