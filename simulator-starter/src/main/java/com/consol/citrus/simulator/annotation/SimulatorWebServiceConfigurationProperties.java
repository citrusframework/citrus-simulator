package com.consol.citrus.simulator.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

/**
 * @author Christoph Deppisch
 */
@ConfigurationProperties(prefix = "citrus.simulator.ws")
public class SimulatorWebServiceConfigurationProperties implements EnvironmentAware {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(SimulatorWebServiceConfigurationProperties.class);

    /**
     * The web service message dispatcher servlet mapping. Clients must use this
     * context path in order to access the web service support on the simulator.
     */
    private static final String SIMULATOR_SERVLET_MAPPING_PROPERTY = "citrus.simulator.ws.servlet.mapping";
    private static final String SIMULATOR_SERVLET_MAPPING_ENV = "CITRUS_SIMULATOR_WS_SERVLET_MAPPING";
    private String servletMapping = "/services/ws/*";

    /**
     * The Spring application context environment
     */
    private Environment env;

    @PostConstruct
    private void loadProperties() {
        servletMapping = env.getProperty(SIMULATOR_SERVLET_MAPPING_PROPERTY, env.getProperty(SIMULATOR_SERVLET_MAPPING_ENV, servletMapping));

        log.info("Using the simulator configuration: {}", this.toString());
    }

    /**
     * Gets the servletMapping.
     *
     * @return
     */
    public String getServletMapping() {
        return servletMapping;
    }

    /**
     * Sets the servletMapping.
     *
     * @param servletMapping
     */
    public void setServletMapping(String servletMapping) {
        this.servletMapping = servletMapping;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "servletMapping='" + servletMapping + '\'' +
                '}';
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
