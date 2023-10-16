package org.citrusframework.simulator.repository;

import org.citrusframework.simulator.model.TestResult;
import org.citrusframework.simulator.service.dto.TestResultByStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TestResult entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long>, JpaSpecificationExecutor<TestResult> {

    @Query("select new org.citrusframework.simulator.service.dto.TestResultByStatus(" +
        "sum(case when t.status = 1 then 1 else 0 end), " +
        "sum(case when t.status = 2 then 1 else 0 end)) " +
        "from TestResult t")
    TestResultByStatus countByStatus();
}
