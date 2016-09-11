package com.consol.citrus.simulator.annotation;

/**
 * @author Christoph Deppisch
 */
public abstract class SimulatorJmsAdapter implements SimulatorJmsConfigurer {

    @Override
    public String destinationName() {
        return System.getProperty("citrus.simulator.jms.destination", "Citrus.Simulator.Inbound");
    }

    @Override
    public boolean useSoapEnvelope() {
        return false;
    }
}
