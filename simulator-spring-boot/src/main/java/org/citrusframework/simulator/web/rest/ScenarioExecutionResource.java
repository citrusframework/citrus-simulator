/*
 * Copyright 2023-2024 the original author or authors.
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

import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.service.ScenarioExecutionQueryService;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.citrusframework.simulator.service.criteria.ScenarioExecutionCriteria;
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
 * REST controller for managing {@link ScenarioExecution}.
 */
@RestController
@RequestMapping("/api")
public class ScenarioExecutionResource {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioExecutionResource.class);

    private final ScenarioExecutionService scenarioExecutionService;

    private final ScenarioExecutionQueryService scenarioExecutionQueryService;

    public ScenarioExecutionResource(
        ScenarioExecutionService scenarioExecutionService,
        ScenarioExecutionQueryService scenarioExecutionQueryService
    ) {
        this.scenarioExecutionService = scenarioExecutionService;
        this.scenarioExecutionQueryService = scenarioExecutionQueryService;
    }

    /**
     * {@code GET  /scenario-executions} : get all the scenarioExecutions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of scenarioExecutions in body.
     */
    @GetMapping("/scenario-executions")
    public ResponseEntity<List<ScenarioExecution>> getAllScenarioExecutions(
        ScenarioExecutionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        logger.debug("REST request to get ScenarioExecutions by criteria: {}", criteria);

        Page<ScenarioExecution> page = scenarioExecutionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /scenario-executions/count} : count all the scenarioExecutions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/scenario-executions/count")
    public ResponseEntity<Long> countScenarioExecutions(ScenarioExecutionCriteria criteria) {
        logger.debug("REST request to count ScenarioExecutions by criteria: {}", criteria);
        return ResponseEntity.ok().body(scenarioExecutionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /scenario-executions/:id} : get the "id" scenarioExecution.
     *
     * @param id the id of the scenarioExecution to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scenarioExecution, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/scenario-executions/{id}")
    public ResponseEntity<ScenarioExecution> getScenarioExecution(@PathVariable("id") Long id) {
        logger.debug("REST request to get ScenarioExecution : {}", id);
        Optional<ScenarioExecution> scenarioExecution = scenarioExecutionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(scenarioExecution);
    }
}
