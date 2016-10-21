package com.consol.citrus.simulator.annotation;

import org.springframework.ws.server.EndpointInterceptor;

/**
 * @author Christoph Deppisch
 */
public interface SimulatorWebServiceConfigurer extends SimulatorConfigurer {

    /**
     * Gets the message dispatcher servlet mapping.
     * @return
     */
    String servletMapping();

    /**
     * Gets the list of endpoint interceptors.
     * @return
     */
    EndpointInterceptor[] interceptors();
}
