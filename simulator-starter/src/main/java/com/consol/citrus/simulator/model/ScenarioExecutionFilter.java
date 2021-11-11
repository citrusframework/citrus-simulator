package com.consol.citrus.simulator.model;

import lombok.Data;

/**
 * Filter for filtering {@link Message}s
 */
@Data
public class ScenarioExecutionFilter extends MessageFilter {
    private String scenarioName;
}
