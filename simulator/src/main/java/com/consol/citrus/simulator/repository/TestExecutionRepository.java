package com.consol.citrus.simulator.repository;

import com.consol.citrus.simulator.model.TestExecution;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * JPA repository for {@link TestExecution}
 */
@Repository
public interface TestExecutionRepository extends CrudRepository<TestExecution, Long> {
    List<TestExecution> findByTestNameOrderByStartDateDesc(String testName);

    List<TestExecution> findByStatusOrderByStartDateDesc(TestExecution.Status status);

    List<TestExecution> findByStartDateBetweenOrderByStartDateDesc(Date fromDate, Date toDate, Pageable pageable);
}
