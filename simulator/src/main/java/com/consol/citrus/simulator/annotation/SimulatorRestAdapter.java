package com.consol.citrus.simulator.annotation;

import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.http.interceptor.LoggingHandlerInterceptor;
import com.consol.citrus.simulator.http.AnnotationRequestMappingKeyExtractor;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author Christoph Deppisch
 */
public abstract class SimulatorRestAdapter implements SimulatorRestConfigurer {

    @Override
    public MappingKeyExtractor mappingKeyExtractor() {
        return new AnnotationRequestMappingKeyExtractor();
    }

    @Override
    public HandlerInterceptor[] interceptors() {
        return new HandlerInterceptor[] { new LoggingHandlerInterceptor() };
    }

    @Override
    public String urlMapping() {
        return "/services/rest/**";
    }
}
