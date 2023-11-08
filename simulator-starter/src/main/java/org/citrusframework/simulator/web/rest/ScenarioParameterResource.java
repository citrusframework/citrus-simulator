/*
 * Copyright 2023 the original author or authors.
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

import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.service.ScenarioParameterQueryService;
import org.citrusframework.simulator.service.ScenarioParameterService;
import org.citrusframework.simulator.service.criteria.ScenarioParameterCriteria;
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
 * REST controller for managing {@link ScenarioParameter}.
 */
@RestController
@RequestMapping("/api")
public class ScenarioParameterResource {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioParameterResource.class);

    private final ScenarioParameterService scenarioParameterService;

    private final ScenarioParameterQueryService scenarioParameterQueryService;

    public ScenarioParameterResource(
        ScenarioParameterService scenarioParameterService,
        ScenarioParameterQueryService scenarioParameterQueryService
    ) {
        this.scenarioParameterService = scenarioParameterService;
        this.scenarioParameterQueryService = scenarioParameterQueryService;
    }

    /**
     * {@code GET  /scenario-parameters} : get all the scenarioParameters.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of scenarioParameters in body.
     */
    @GetMapping("/scenario-parameters")
    public ResponseEntity<List<ScenarioParameter>> getAllScenarioParameters(
        ScenarioParameterCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        logger.debug("REST request to get ScenarioParameters by criteria: {}", criteria);

        Page<ScenarioParameter> page = scenarioParameterQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /scenario-parameters/count} : count all the scenarioParameters.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/scenario-parameters/count")
    public ResponseEntity<Long> countScenarioParameters(ScenarioParameterCriteria criteria) {
        logger.debug("REST request to count ScenarioParameters by criteria: {}", criteria);
        return ResponseEntity.ok().body(scenarioParameterQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /scenario-parameters/:id} : get the "id" scenarioParameter.
     *
     * @param id the id of the scenarioParameter to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scenarioParameter, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/scenario-parameters/{id}")
    public ResponseEntity<ScenarioParameter> getScenarioParameter(@PathVariable Long id) {
        logger.debug("REST request to get ScenarioParameter : {}", id);
        Optional<ScenarioParameter> scenarioParameter = scenarioParameterService.findOne(id);
        return ResponseUtil.wrapOrNotFound(scenarioParameter);
    }
}
