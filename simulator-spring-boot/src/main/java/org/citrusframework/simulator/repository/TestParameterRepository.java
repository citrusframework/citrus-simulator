/*
 * Copyright the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
