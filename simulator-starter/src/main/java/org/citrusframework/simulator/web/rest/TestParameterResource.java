package org.citrusframework.simulator.web.rest;

import org.citrusframework.simulator.model.TestParameter;
import org.citrusframework.simulator.service.TestParameterQueryService;
import org.citrusframework.simulator.service.TestParameterService;
import org.citrusframework.simulator.service.criteria.TestParameterCriteria;
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
 * REST controller for managing {@link org.citrusframework.simulator.model.TestParameter}.
 */
@RestController
@RequestMapping("/api")
public class TestParameterResource {

    private final Logger logger = LoggerFactory.getLogger(TestParameterResource.class);

    private final TestParameterService testParameterService;

    private final TestParameterQueryService testParameterQueryService;

    public TestParameterResource(TestParameterService testParameterService, TestParameterQueryService testParameterQueryService) {
        this.testParameterService = testParameterService;
        this.testParameterQueryService = testParameterQueryService;
    }

    /**
     * {@code GET  /test-parameters} : get all the testParameters.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of testParameters in body.
     */
    @GetMapping("/test-parameters")
    public ResponseEntity<List<TestParameter>> getAllTestParameters(
        TestParameterCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        logger.debug("REST request to get TestParameters by criteria: {}", criteria);

        Page<TestParameter> page = testParameterQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /test-parameters/count} : count all the testParameters.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/test-parameters/count")
    public ResponseEntity<Long> countTestParameters(TestParameterCriteria criteria) {
        logger.debug("REST request to count TestParameters by criteria: {}", criteria);
        return ResponseEntity.ok().body(testParameterQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /test-parameters/:id} : get the "id" testParameter.
     *
     * @param id the id of the testParameter to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the testParameter, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/test-parameters/{testResultId}/{key}")
    public ResponseEntity<TestParameter> getTestParameter(@PathVariable Long testResultId, @PathVariable String key) {
        logger.debug("REST request to get TestParameter '{}' of TestResult: {}", key, testResultId);
        Optional<TestParameter> testParameter = testParameterService.findOne(testResultId, key);
        return ResponseUtil.wrapOrNotFound(testParameter);
    }
}
