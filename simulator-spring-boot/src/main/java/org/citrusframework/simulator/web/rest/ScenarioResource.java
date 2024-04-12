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

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.citrusframework.simulator.common.FeatureFlagNotEnabledException;
import org.citrusframework.simulator.events.ScenariosReloadedEvent;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.scenario.ScenarioStarter;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.citrusframework.simulator.service.ScenarioLookupService;
import org.citrusframework.simulator.service.ScenarioRegistrationService;
import org.citrusframework.simulator.web.rest.dto.ScenarioParameterDTO;
import org.citrusframework.simulator.web.rest.dto.mapper.ScenarioParameterMapper;
import org.citrusframework.simulator.web.rest.pagination.ScenarioComparator;
import org.springdoc.core.annotations.ParameterObject;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.net.URLDecoder.decode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;
import static org.citrusframework.simulator.config.OpenFeatureConfig.EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED;
import static org.citrusframework.simulator.web.rest.ScenarioResource.Scenario.ScenarioType.MESSAGE_TRIGGERED;
import static org.citrusframework.simulator.web.rest.ScenarioResource.Scenario.ScenarioType.STARTER;
import static org.citrusframework.simulator.web.util.PaginationUtil.createPage;
import static org.citrusframework.simulator.web.util.PaginationUtil.generatePaginationHttpHeaders;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@Slf4j
@RestController
@RequestMapping("api")
public class ScenarioResource {

    private final ScenarioExecutorService scenarioExecutorService;
    private final ScenarioLookupService scenarioLookupService;
    private final ScenarioRegistrationService scenarioRegistrationService;

    private final ScenarioParameterMapper scenarioParameterMapper;

    private final List<Scenario> scenarioCache = new ArrayList<>();

    public ScenarioResource(ScenarioExecutorService scenarioExecutorService, ScenarioLookupService scenarioLookupService, ScenarioRegistrationService scenarioRegistrationService, ScenarioParameterMapper scenarioParameterMapper) {
        this.scenarioExecutorService = scenarioExecutorService;
        this.scenarioLookupService = scenarioLookupService;
        this.scenarioRegistrationService = scenarioRegistrationService;
        this.scenarioParameterMapper = scenarioParameterMapper;

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

        scenarioNames.forEach(name -> scenarioCache.add(new Scenario(name, MESSAGE_TRIGGERED)));
        scenarioStarterNames.forEach(name -> scenarioCache.add(new Scenario(name, STARTER)));

        scenarioCache.sort(comparing(Scenario::name));
    }

    /**
     * Get a list of all registered {@link Scenario}'s in the current simulator
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of scenarios in body.
     */
    @GetMapping(value = "/scenarios", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Scenario>> getScenarios(@RequestParam(name = "nameContains", required = false) Optional<String> nameContains, @ParameterObject Pageable pageable) {
        var nameFilter = nameContains.map(contains -> decode(contains, UTF_8)).orElse("*");
        logger.debug("REST request get registered Scenarios, where name contains: {}", nameFilter);

        Page<Scenario> page = createPage(
            scenarioCache.stream()
                .filter(scenario -> nameContains.isEmpty() || scenario.name().contains(nameFilter))
                .toList(),
            pageable,
            ScenarioComparator::fromProperty
        );

        HttpHeaders headers = generatePaginationHttpHeaders(fromCurrentRequest(), page);
        return ok().headers(headers).body(page.getContent());
    }

    @PostMapping(value = "/scenarios/{scenarioName}", consumes = {TEXT_PLAIN_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<?> uploadScenario(@PathVariable("scenarioName") String scenarioName, @RequestBody String javaSourceCode) {
        try {
            var simulatorScenario = scenarioRegistrationService.registerScenarioFromJavaSourceCode(scenarioName, javaSourceCode);
            return created(URI.create("/api/scenarios/" + scenarioName)).body(new Scenario(simulatorScenario.getName(), simulatorScenario instanceof ScenarioStarter ? STARTER : MESSAGE_TRIGGERED));
        } catch (FeatureFlagNotEnabledException e) {
            var responseNode = new ObjectMapper().createObjectNode();
            responseNode.put("message", "Feature flag not enabled.");
            responseNode.put("flag", EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED);
            return ResponseEntity.status(NOT_IMPLEMENTED)
                .body(responseNode);
        }
    }

    /**
     * Get the {@link ScenarioParameter}'s for the {@link Scenario} matching the supplied name
     *
     * @param scenarioName the name of the scenario
     * @return the {@link ScenarioParameter}'s, if any are defined, or an empty list
     */
    @GetMapping("/scenarios/{scenarioName}/parameters")
    public Collection<ScenarioParameterDTO> getScenarioParameters(@PathVariable("scenarioName") String scenarioName) {
        logger.debug("REST request get Parameters of Scenario: {}", scenarioName);
        return scenarioLookupService.lookupScenarioParameters(scenarioName).stream().map(scenarioParameterMapper::toDto).toList();
    }

    /**
     * Launches a {@link Scenario} using the collection of {@link ScenarioParameter} as scenario variables.
     * <p>
     * Note that this rest endpoint does not block until the scenario has completed execution.
     *
     * @param scenarioName the name of the launched {@link Scenario}
     */
    @PostMapping("scenarios/{scenarioName}/launch")
    public Long launchScenario(@NotEmpty @PathVariable("scenarioName") String scenarioName, @RequestBody(required = false) List<ScenarioParameterDTO> scenarioParameters) {
        logger.debug("REST request to launch Scenario '{}' with Parameters: {}", scenarioName, scenarioParameters);
        return scenarioExecutorService.run(scenarioName, scenarioParameters.stream().map(scenarioParameterMapper::toEntity).toList());
    }

    public record Scenario(String name, ScenarioResource.Scenario.ScenarioType type) {

        public enum ScenarioType {
            STARTER,
            MESSAGE_TRIGGERED
        }
    }
}
