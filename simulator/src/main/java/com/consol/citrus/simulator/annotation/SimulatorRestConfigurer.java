package com.consol.citrus.simulator.annotation;

import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author Christoph Deppisch
 */
public interface SimulatorRestConfigurer extends SimulatorConfigurer {

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
