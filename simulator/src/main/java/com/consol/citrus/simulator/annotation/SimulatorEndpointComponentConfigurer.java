package com.consol.citrus.simulator.annotation;

import com.consol.citrus.endpoint.Endpoint;
import org.springframework.context.ApplicationContext;

/**
 * @author Christoph Deppisch
 */
public interface SimulatorEndpointComponentConfigurer extends SimulatorConfigurer {

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

}
