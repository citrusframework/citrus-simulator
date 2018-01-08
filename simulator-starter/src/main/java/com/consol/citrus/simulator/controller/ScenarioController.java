/*
 * Copyright 2006-2017 the original author or authors.
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

package com.consol.citrus.simulator.controller;

import com.consol.citrus.simulator.model.ScenarioParameter;
import com.consol.citrus.simulator.service.ScenarioExecutionService;
import com.consol.citrus.simulator.service.ScenarioLookupService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/scenario")
public class ScenarioController {

    private final ScenarioExecutionService scenarioExecutionService;
    private final ScenarioLookupService scenarioLookupService;
    private final List<Scenario> scenarios;

    public ScenarioController(ScenarioExecutionService scenarioExecutionService, ScenarioLookupService scenarioLookupService) {
        this.scenarioExecutionService = scenarioExecutionService;
        this.scenarioLookupService = scenarioLookupService;
        this.scenarios = getScenarioList(scenarioLookupService);
    }

    @Data
    public static class Scenario {
        public enum ScenarioType {
            STARTER,
            MESSAGE_TRIGGERED;
        }

        private final String name;
        private final ScenarioType type;
    }

    private static List<Scenario> getScenarioList(ScenarioLookupService scenarioLookupService) {
        final List<Scenario> scenarios = new ArrayList<>();
        scenarioLookupService.getScenarioNames().forEach(name -> scenarios.add(new Scenario(name, Scenario.ScenarioType.MESSAGE_TRIGGERED)));
        scenarioLookupService.getStarterNames().forEach(name -> scenarios.add(new Scenario(name, Scenario.ScenarioType.STARTER)));
        return scenarios;
    }

    @Data
    @NoArgsConstructor
    public static class ScenarioFilter {
        private String name;
    }

    /**
     * Get a list of scenarios
     *
     * @param filter
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public Collection<Scenario> getScenarioNames(@RequestBody(required = false) ScenarioFilter filter) {
        return scenarios.stream()
                .filter(scenario -> {
                    if (filter != null && StringUtils.hasText(filter.getName())) {
                        return scenario.getName().contains(filter.getName());
                    }
                    return true;
                })
                .sorted(Comparator.comparing(Scenario::getName)).collect(Collectors.toList());
    }

    /**
     * Get the scenario parameters for the scenario matching the supplied name
     *
     * @param scenarioName the name of the scenario
     * @return the scenario parameters, if any are defined, or an empty list
     */
    @RequestMapping(method = RequestMethod.GET, value = "/parameters/{name}")
    public Collection<ScenarioParameter> getScenarioParameters(@PathVariable("name") String scenarioName) {
        return scenarioLookupService.lookupScenarioParameters(scenarioName);
    }

    /**
     * Launches a scenario using the collection of parameters as scenario variables. This rest service does not
     * block until the scenario has completed execution.
     *
     * @param name
     */
    @RequestMapping(method = RequestMethod.POST, value = "/launch/{name}")
    public Long launchScenario(
            @PathVariable("name") String name,
            @RequestBody(required = false) List<ScenarioParameter> scenarioParameters) {
        return scenarioExecutionService.run(name, scenarioParameters);
    }

}
