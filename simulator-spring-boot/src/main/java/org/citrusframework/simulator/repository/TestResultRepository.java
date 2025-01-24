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

import org.citrusframework.simulator.model.TestResult;
import org.citrusframework.simulator.service.dto.TestResultByStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link TestResult} entity.
 */
@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long>, JpaSpecificationExecutor<TestResult> {

    @Query("select new org.citrusframework.simulator.service.dto.TestResultByStatus(" +
        "sum(case when t.status = 1 then 1 else 0 end), " +
        "sum(case when t.status = 2 then 1 else 0 end)) " +
        "from TestResult t")
    TestResultByStatus countByStatus();

    @Override
    @EntityGraph(attributePaths = {"testParameters"})
    Page<TestResult> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"testParameters"})
    Optional<TestResult> findById(Long id);

    @Query("FROM TestResult WHERE id IN :testResultIds")
    @EntityGraph(attributePaths = {"testParameters"})
    List<TestResult> findAllWhereIdIn(@Param("testResultIds") List<Long> testResultIds, Sort sort);
}
