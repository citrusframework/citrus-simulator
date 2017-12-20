package com.consol.citrus.simulator.scenario.mapper;

@FunctionalInterface
public interface ScenarioNameValidator {
    /**
     * Checks the supplied {@code scenarioName}
     *
     * @param scenarioName the scenario name to check
     * @return true, if a scenario exists with the supplied scenario name
     */
    boolean isValidScenarioName(String scenarioName);
}
