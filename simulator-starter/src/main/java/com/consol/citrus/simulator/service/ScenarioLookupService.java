package com.consol.citrus.simulator.service;

import com.consol.citrus.simulator.model.ScenarioParameter;
import com.consol.citrus.simulator.scenario.ScenarioStarter;
import com.consol.citrus.simulator.scenario.SimulatorScenario;
import com.consol.citrus.simulator.scenario.Starter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for looking-up and accessing {@link com.consol.citrus.simulator.scenario.Scenario}s and
 * {@link com.consol.citrus.simulator.scenario.Starter}s
 */
@Service
public class ScenarioLookupService {
    private static final Logger LOG = LoggerFactory.getLogger(ScenarioLookupService.class);

    private final ApplicationContext applicationContext;

    /**
     * List of available scenario starters
     */
    private Map<String, ScenarioStarter> scenarioStarters;

    /**
     * List of available scenarios
     */
    private Map<String, SimulatorScenario> scenarios;

    public ScenarioLookupService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    private void init() {
        scenarios = getSimulatorScenarios(applicationContext);
        LOG.info(String.format("Scenarios found: %n%s", Arrays.toString(scenarios.keySet().toArray())));

        scenarioStarters = getScenarioStarters(applicationContext);
        LOG.info(String.format("Starters found: %n%s", Arrays.toString(scenarioStarters.keySet().toArray())));
    }

    /**
     * Returns the list of parameters that the scenario can be passed when started
     *
     * @param scenarioName
     * @return
     */
    public Collection<ScenarioParameter> lookupScenarioParameters(String scenarioName) {
        if (scenarioStarters.containsKey(scenarioName)) {
            return scenarioStarters.get(scenarioName).getScenarioParameters();
        }
        return Collections.emptyList();
    }

    /**
     * Returns a list containing the names of all starters
     *
     * @return all starter names
     */
    public Collection<String> getStarterNames() {
        return scenarioStarters.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Returns a list containing the names of all scenarios.
     *
     * @return all scenario names
     */
    public Collection<String> getScenarioNames() {
        return scenarios.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    private static Map<String, SimulatorScenario> getSimulatorScenarios(ApplicationContext context) {
        return context.getBeansOfType(SimulatorScenario.class).entrySet().stream()
                .filter(map -> !map.getValue().getClass().isAnnotationPresent(Starter.class))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    private static Map<String, ScenarioStarter> getScenarioStarters(ApplicationContext context) {
        return context.getBeansOfType(ScenarioStarter.class).entrySet().stream()
                .filter(map -> map.getValue().getClass().isAnnotationPresent(Starter.class))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
