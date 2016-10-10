package com.consol.citrus.simulator.annotation;

import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import org.springframework.context.ApplicationContext;

/**
 * @author Christoph Deppisch
 */
public interface SimulatorConfigurer {

    /**
     * Gets the target endpoint.
     * @param applicationContext
     * @return
     */
    Endpoint endpoint(ApplicationContext applicationContext);

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
