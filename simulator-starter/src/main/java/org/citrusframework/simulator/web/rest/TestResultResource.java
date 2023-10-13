package org.citrusframework.simulator.web.rest;

import org.citrusframework.simulator.model.TestResult;
import org.citrusframework.simulator.service.TestResultQueryService;
import org.citrusframework.simulator.service.TestResultService;
import org.citrusframework.simulator.service.criteria.TestResultCriteria;
import org.citrusframework.simulator.service.dto.TestResultByStatus;
import org.citrusframework.simulator.web.util.PaginationUtil;
import org.citrusframework.simulator.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link TestResult}.
 */
@RestController
@RequestMapping("/api")
public class TestResultResource {

    private final Logger logger = LoggerFactory.getLogger(TestResultResource.class);

    private final TestResultService testResultService;

    private final TestResultQueryService testResultQueryService;

    public TestResultResource(TestResultService testResultService, TestResultQueryService testResultQueryService) {
        this.testResultService = testResultService;
        this.testResultQueryService = testResultQueryService;
    }

    /**
     * {@code GET  /test-results} : get all the testResults.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of testResults in body.
     */
    @GetMapping("/test-results")
    public ResponseEntity<List<TestResult>> getAllTestResults(
        TestResultCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        logger.debug("REST request to get TestResults by criteria: {}", criteria);

        Page<TestResult> page = testResultQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /test-results/count} : count all the testResults.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/test-results/count")
    public ResponseEntity<Long> countTestResults(TestResultCriteria criteria) {
        logger.debug("REST request to count TestResults by criteria: {}", criteria);
        return ResponseEntity.ok().body(testResultQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /test-results/count-by-status} : count all the testResults by their status.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/test-results/count-by-status")
    public ResponseEntity<TestResultByStatus> countTestResultsByStatus() {
        logger.debug("REST request to count total TestResults by status");
        return ResponseEntity.ok().body(testResultService.countByStatus());
    }

    /**
     * {@code GET  /test-results/:id} : get the "id" testResult.
     *
     * @param id the id of the testResult to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the testResult, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/test-results/{id}")
    public ResponseEntity<TestResult> getTestResult(@PathVariable Long id) {
        logger.debug("REST request to get TestResult : {}", id);
        Optional<TestResult> testResult = testResultService.findOne(id);
        return ResponseUtil.wrapOrNotFound(testResult);
    }
}
