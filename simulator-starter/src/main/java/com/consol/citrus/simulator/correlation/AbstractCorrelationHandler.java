package com.consol.citrus.simulator.correlation;

import com.consol.citrus.simulator.scenario.ScenarioEndpoint;

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractCorrelationHandler implements CorrelationHandler {

    private final ScenarioEndpoint scenarioEndpoint;

    /**
     * Default constructor using scenario.
     * @param scenarioEndpoint
     */
    public AbstractCorrelationHandler(ScenarioEndpoint scenarioEndpoint) {
        this.scenarioEndpoint = scenarioEndpoint;
    }

    @Override
    public ScenarioEndpoint getScenarioEndpoint() {
        return scenarioEndpoint;
    }
}
