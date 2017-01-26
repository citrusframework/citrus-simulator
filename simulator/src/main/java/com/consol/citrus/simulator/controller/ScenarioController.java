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
     * Launches a test using the collection of test parameters as test variables. This rest service does not
     * block
     *
     * @param name
     */
    @RequestMapping(method = RequestMethod.POST, value = "/test/{name}/launch")
    public void launchTest(
            @PathVariable("name") String name,
            @RequestBody(required = false) List<TestParameter> testParameters
    ) {
        scenarioService.run2(name, testParameters);
    }

}
