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

package org.citrusframework.simulator.web.rest;

import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties.SimulationResults;
import org.citrusframework.simulator.model.TestResult;
import org.citrusframework.simulator.service.TestResultQueryService;
import org.citrusframework.simulator.service.TestResultService;
import org.citrusframework.simulator.service.criteria.TestResultCriteria;
import org.citrusframework.simulator.service.dto.TestResultByStatus;
import org.citrusframework.simulator.web.rest.dto.TestResultDTO;
import org.citrusframework.simulator.web.rest.dto.mapper.TestResultMapper;
import org.citrusframework.simulator.web.util.PaginationUtil;
import org.citrusframework.simulator.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

/**
 * REST controller for managing {@link TestResult}.
 */
@RestController
@RequestMapping("/api")
public class TestResultResource {

    private static final Logger logger = LoggerFactory.getLogger(TestResultResource.class);

    private final SimulatorConfigurationProperties simulatorConfigurationProperties;
    private final TestResultService testResultService;
    private final TestResultQueryService testResultQueryService;

    private final TestResultMapper testResultMapper;

    public TestResultResource(SimulatorConfigurationProperties simulatorConfigurationProperties, TestResultService testResultService, TestResultQueryService testResultQueryService, TestResultMapper testResultMapper) {
        this.simulatorConfigurationProperties = simulatorConfigurationProperties;
        this.testResultService = testResultService;
        this.testResultQueryService = testResultQueryService;
        this.testResultMapper = testResultMapper;
    }

    /**
     * {@code GET  /test-results} : get all the testResults.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of testResults in body.
     */
    @GetMapping("/test-results")
    public ResponseEntity<List<TestResultDTO>> getAllTestResults(TestResultCriteria criteria, @ParameterObject Pageable pageable) {
        logger.debug("REST request to get TestResults by criteria: {}", criteria);

        Page<TestResult> page = testResultQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent().stream().map(testResultMapper::toDto).toList());
    }

    /**
     * {@code DELETE  /test-results} : delete all the testResults.
     * <p>
     * Functionality can be disabled using the
     * property {@link SimulationResults#isResetEnabled()}, in which case an HTTP 501 "Not
     * Implemented" code will be returned.
     *
     * @return the {@link ResponseEntity} with status {@code 201 (NO CONTENT)}.
     */
    @DeleteMapping("/test-results")
    public ResponseEntity<Void> deleteAllTestResults() {
        if (simulatorConfigurationProperties.getSimulationResults().isResetEnabled()) {
            logger.debug("REST request to delete all TestResults");
            testResultService.deleteAll();
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("REST request to delete all TestResults, but reset is disabled!");
            return ResponseEntity.status(NOT_IMPLEMENTED)
                .header(
                    "message",
                    "Resetting TestResults is disabled on this simulator, see property 'citrus.simulator.simulation-results.reset-enabled' for more information!")
                .build();
        }
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
    public ResponseEntity<TestResultDTO> getTestResult(@PathVariable("id") Long id) {
        logger.debug("REST request to get TestResult : {}", id);
        Optional<TestResult> testResult = testResultService.findOne(id);
        return ResponseUtil.wrapOrNotFound(testResult.map(testResultMapper::toDto));
    }
}
