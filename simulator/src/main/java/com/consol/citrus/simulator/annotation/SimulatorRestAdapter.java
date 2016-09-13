package com.consol.citrus.simulator.annotation;

import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.http.interceptor.LoggingHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author Christoph Deppisch
 */
public abstract class SimulatorRestAdapter implements SimulatorRestConfigurer {

    @Override
    public MappingKeyExtractor mappingKeyExtractor() {
        return new XPathPayloadMappingKeyExtractor();
    }

    @Override
    public HandlerInterceptor[] interceptors() {
        return new HandlerInterceptor[] { new LoggingHandlerInterceptor() };
    }

    @Override
    public String urlMapping() {
        return "/services/rest/*";
    }
}
