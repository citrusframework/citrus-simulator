package com.consol.citrus.simulator.annotation;

import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;

import javax.jms.ConnectionFactory;

/**
 * @author Christoph Deppisch
 */
public interface SimulatorJmsConfigurer {

    /**
     * Gets the jms connection factory.
     * @return
     */
    ConnectionFactory connectionFactory();

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

    /**
     * Gets the mapping key extractor.
     * @return
     */
    MappingKeyExtractor mappingKeyExtractor();
}
