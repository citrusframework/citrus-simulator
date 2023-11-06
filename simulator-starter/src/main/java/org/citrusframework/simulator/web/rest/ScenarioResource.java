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

import static org.citrusframework.simulator.web.util.PaginationUtil.createPage;

import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.citrusframework.simulator.events.ScenariosReloadedEvent;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.citrusframework.simulator.service.ScenarioLookupService;
import org.citrusframework.simulator.web.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("api")
public class ScenarioResource {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioResource.class);

    private final ScenarioExecutorService scenarioExecutorService;
    private final ScenarioLookupService scenarioLookupService;

    private final List<Scenario> scenarioCache = new ArrayList<>();

    public ScenarioResource(ScenarioExecutorService scenarioExecutorService, ScenarioLookupService scenarioLookupService) {
        this.scenarioExecutorService = scenarioExecutorService;
        this.scenarioLookupService = scenarioLookupService;

        evictAndReloadScenarioCache(scenarioLookupService.getScenarioNames(), scenarioLookupService.getStarterNames());
    }

    @EventListener(ScenariosReloadedEvent.class)
    public void evictAndReloadScenarioCache(ScenariosReloadedEvent scenariosReloadedEvent) {
        logger.info("Registered change in scenario cache: {}", scenariosReloadedEvent);
        evictAndReloadScenarioCache(scenariosReloadedEvent.getScenarioNames(), scenariosReloadedEvent.getScenarioStarterNames());
    }

    private synchronized void evictAndReloadScenarioCache(Set<String> scenarioNames, Set<String> scenarioStarterNames) {
        logger.debug("Scenarios found: {}", scenarioNames);
        logger.debug("Starters found: {}", scenarioStarterNames);

        scenarioCache.clear();

        scenarioNames.forEach(name -> scenarioCache.add(new Scenario(name, Scenario.ScenarioType.MESSAGE_TRIGGERED)));
        scenarioStarterNames.forEach(name -> scenarioCache.add(new Scenario(name, Scenario.ScenarioType.STARTER)));

        scenarioCache.sort(Comparator.comparing(Scenario::name));
    }

    /**
     * Get a list of all registered {@link Scenario}'s in the current simulator
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of scenarios in body.
     */
    @GetMapping("/scenarios")
    public ResponseEntity<List<Scenario>> getScenarios(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        logger.debug("REST request get registered Scenarios");

        Page<Scenario> page = createPage(scenarioCache, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Get the {@link ScenarioParameter}'s for the {@link Scenario} matching the supplied name
     *
     * @param scenarioName the name of the scenario
     * @return the {@link ScenarioParameter}'s, if any are defined, or an empty list
     */
    @GetMapping("/scenarios/{scenarioName}/parameters")
    public Collection<ScenarioParameter> getScenarioParameters(@PathVariable("scenarioName") String scenarioName) {
        logger.debug("REST request get Parameters of Scenario: {}", scenarioName);
        return scenarioLookupService.lookupScenarioParameters(scenarioName);
    }

    /**
     * Launches a {@link Scenario} using the collection of {@link ScenarioParameter} as scenario variables.
     * <p>
     * Note that this rest endpoint does not block until the scenario has completed execution.
     *
     * @param scenarioName the name of the launched {@link Scenario}
     */
    @PostMapping("scenarios/{scenarioName}/launch")
    public Long launchScenario(@NotEmpty @PathVariable("scenarioName") String scenarioName, @RequestBody(required = false) List<ScenarioParameter> scenarioParameters) {
        logger.debug("REST request to launch Scenario '{}' with Parameters: {}", scenarioName, scenarioParameters);
        return scenarioExecutorService.run(scenarioName, scenarioParameters);
    }

    public record Scenario(String name, ScenarioResource.Scenario.ScenarioType type) {

        public enum ScenarioType {
            STARTER,
            MESSAGE_TRIGGERED
        }
    }
}
