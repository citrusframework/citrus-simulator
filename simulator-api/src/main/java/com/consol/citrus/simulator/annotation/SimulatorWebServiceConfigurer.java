package com.consol.citrus.simulator.annotation;

import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import org.springframework.ws.server.EndpointInterceptor;

/**
 * @author Christoph Deppisch
 */
public interface SimulatorWebServiceConfigurer {

    /**
     * Gets the message dispatcher servlet mapping.
     * @return
     */
    String servletMapping();

    /**
     * Gets the mapping key extractor.
     * @return
     */
    MappingKeyExtractor mappingKeyExtractor();

    /**
     * Gets the list of endpoint interceptors.
     * @return
     */
    EndpointInterceptor[] interceptors();
}
