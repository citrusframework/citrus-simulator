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

import com.consol.citrus.simulator.model.TestParameter;
import com.consol.citrus.simulator.service.ScenarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ScenarioController {
    @Autowired
    ScenarioService scenarioService;

    public static class Scenario {
        public enum ScenarioType {
            STARTER,
            MESSAGE_TRIGGERED;
        }

        private final String name;
        private final ScenarioType type;

        public Scenario(String name, ScenarioType type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public ScenarioType getType() {
            return type;
        }
    }

    /**
     * Get a list of scenario names
     *
     * @param filter
     * @return
     */
    // TODO MM rename test to scenario
    @RequestMapping(method = RequestMethod.GET, value = "/test")
    public Collection<Scenario> getTestNames(@RequestParam(value = "filter", required = false) String filter) {
        List<Scenario> scenarios = new ArrayList<>();
        scenarioService.getScenarioNames().forEach(name -> scenarios.add(new Scenario(name, Scenario.ScenarioType.MESSAGE_TRIGGERED)));
        scenarioService.getStarterNames().forEach(name -> scenarios.add(new Scenario(name, Scenario.ScenarioType.STARTER)));
        scenarios.sort(Comparator.comparing(Scenario::getName));
        return scenarios;
    }

    /**
     * Get the test parameters for the test matching the supplied name
     *
     * @param scenarioName the name of the scenario
     * @return the scenario parameters, if any are defined, or an empty list
     */
    @RequestMapping(method = RequestMethod.GET, value = "/test/{name}/parameters")
    public Collection<TestParameter> getTestParameters(@PathVariable("name") String scenarioName) {
        return scenarioService.lookupScenarioParameters(scenarioName);
    }

    /**
     * Launches a scenario using the collection of parameters as scenario variables. This rest service does not
     * block until the scenario has completed execution.
     *
     * @param name
     */
    @RequestMapping(method = RequestMethod.POST, value = "/test/{name}/launch")
    public Long launchTest(
            @PathVariable("name") String name,
            @RequestBody(required = false) List<TestParameter> testParameters
    ) {
        return scenarioService.run(name, testParameters);
    }

}
