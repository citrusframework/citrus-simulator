package org.citrusframework.simulator.correlation;

import org.citrusframework.simulator.scenario.ScenarioEndpoint;

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
