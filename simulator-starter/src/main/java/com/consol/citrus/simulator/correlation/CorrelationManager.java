package com.consol.citrus.simulator.correlation;

/**
 * @author Christoph Deppisch
 */
public interface CorrelationManager {

    /**
     * Starts new correlation for messages with given handler.
     *
     * @return
     */
    CorrelationHandlerBuilder start();
}
