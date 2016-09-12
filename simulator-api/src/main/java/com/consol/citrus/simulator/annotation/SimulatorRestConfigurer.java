package com.consol.citrus.simulator.annotation;

import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author Christoph Deppisch
 */
public interface SimulatorRestConfigurer {

    /**
     * Gets the mapping key extractor.
     * @return
     */
    MappingKeyExtractor mappingKeyExtractor();

    /**
     * Gets list of handler interceptors that should be automatically added to Citrus
     * incoming request handling.
     * @return
     */
    HandlerInterceptor[] interceptors();

    /**
     * Url the Http REST support is mapped to on servlet container.
     * @return
     */
    String urlMapping();
}
