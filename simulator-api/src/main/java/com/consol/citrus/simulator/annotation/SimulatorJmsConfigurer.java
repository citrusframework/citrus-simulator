package com.consol.citrus.simulator.annotation;

/**
 * @author Christoph Deppisch
 */
public interface SimulatorJmsConfigurer {

    /**
     * Gets the jms destination to read messages from.
     * @return
     */
    String destinationName();

    /**
     * Should operate with SOAP envelope. This automatically adds SOAP envelope
     * handling to the inbound and outbound messages.
     * @return
     */
    boolean useSoapEnvelope();
}
