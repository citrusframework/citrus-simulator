package com.consol.citrus.simulator.annotation;

import javax.jms.ConnectionFactory;

/**
 * Common JMS simulator configuration
 */
public interface SimulatorJmsConfigurer extends SimulatorConfigurer {
    /**
     * Gets the jms connection factory.
     *
     * @return
     */
    ConnectionFactory connectionFactory();

    /**
     * Gets the jms destination to receive messages from.
     *
     * @return
     */
    String receiveDestinationName();

    /**
     * Should operate with SOAP envelope. This automatically adds SOAP envelope
     * handling to the inbound and outbound messages.
     *
     * @return
     */
    boolean useSoapEnvelope();

}
