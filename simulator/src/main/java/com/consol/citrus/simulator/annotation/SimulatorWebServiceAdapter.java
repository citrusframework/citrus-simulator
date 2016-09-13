package com.consol.citrus.simulator.annotation;

import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.ws.interceptor.LoggingEndpointInterceptor;
import org.springframework.ws.server.EndpointInterceptor;

/**
 * @author Christoph Deppisch
 */
public abstract class SimulatorWebServiceAdapter implements SimulatorWebServiceConfigurer {

    @Override
    public String servletMapping() {
        return "/services/ws/*";
    }

    @Override
    public MappingKeyExtractor mappingKeyExtractor() {
        return new XPathPayloadMappingKeyExtractor();
    }

    @Override
    public EndpointInterceptor[] interceptors() {
        return new EndpointInterceptor[] { new LoggingEndpointInterceptor() };
    }
}
