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
@ConfigurationProperties(prefix = "citrus.simulator.rest")
public class SimulatorRestConfigurationProperties implements EnvironmentAware {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(SimulatorRestConfigurationProperties.class);

    /**
     * The web service message dispatcher servlet mapping. Clients must use this
     * context path in order to access the web service support on the simulator.
     */
    private static final String SIMULATOR_URL_MAPPING_PROPERTY = "citrus.simulator.rest.url.mapping";
    private static final String SIMULATOR_URL_MAPPING_ENV = "CITRUS_SIMULATOR_REST_URL_MAPPING";
    private String urlMapping = "/services/rest/**";

    /**
     * The Spring application context environment
     */
    private Environment env;

    @PostConstruct
    private void loadProperties() {
        urlMapping = env.getProperty(SIMULATOR_URL_MAPPING_PROPERTY, env.getProperty(SIMULATOR_URL_MAPPING_ENV, urlMapping));

        log.info("Using the simulator configuration: {}", this.toString());
    }

    /**
     * Gets the urlMapping.
     *
     * @return
     */
    public String getUrlMapping() {
        return urlMapping;
    }

    /**
     * Sets the urlMapping.
     *
     * @param urlMapping
     */
    public void setUrlMapping(String urlMapping) {
        this.urlMapping = urlMapping;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "urlMapping='" + urlMapping + '\'' +
                '}';
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
