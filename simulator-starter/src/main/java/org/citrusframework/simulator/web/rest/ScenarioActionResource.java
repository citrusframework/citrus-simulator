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

import org.citrusframework.simulator.model.ScenarioAction;
import org.citrusframework.simulator.service.ScenarioActionQueryService;
import org.citrusframework.simulator.service.ScenarioActionService;
import org.citrusframework.simulator.service.criteria.ScenarioActionCriteria;
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
 * REST controller for managing {@link ScenarioAction}.
 */
@RestController
@RequestMapping("/api")
public class ScenarioActionResource {

    private final Logger log = LoggerFactory.getLogger(ScenarioActionResource.class);

    private final ScenarioActionService scenarioActionService;

    private final ScenarioActionQueryService scenarioActionQueryService;

    public ScenarioActionResource(ScenarioActionService scenarioActionService, ScenarioActionQueryService scenarioActionQueryService) {
        this.scenarioActionService = scenarioActionService;
        this.scenarioActionQueryService = scenarioActionQueryService;
    }

    /**
     * {@code GET  /scenario-actions} : get all the scenarioActions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of scenarioActions in body.
     */
    @GetMapping("/scenario-actions")
    public ResponseEntity<List<ScenarioAction>> getAllScenarioActions(
        ScenarioActionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get ScenarioActions by criteria: {}", criteria);

        Page<ScenarioAction> page = scenarioActionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /scenario-actions/count} : count all the scenarioActions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/scenario-actions/count")
    public ResponseEntity<Long> countScenarioActions(ScenarioActionCriteria criteria) {
        log.debug("REST request to count ScenarioActions by criteria: {}", criteria);
        return ResponseEntity.ok().body(scenarioActionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /scenario-actions/:id} : get the "id" scenarioAction.
     *
     * @param id the id of the scenarioAction to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scenarioAction, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/scenario-actions/{id}")
    public ResponseEntity<ScenarioAction> getScenarioAction(@PathVariable Long id) {
        log.debug("REST request to get ScenarioAction : {}", id);
        Optional<ScenarioAction> scenarioAction = scenarioActionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(scenarioAction);
    }
}
