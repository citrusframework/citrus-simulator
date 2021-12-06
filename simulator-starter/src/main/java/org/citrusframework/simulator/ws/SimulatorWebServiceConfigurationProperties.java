package org.citrusframework.simulator.ws;

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
     * System property constants and environment variable names. Post construct callback reads these values and overwrites
     * settings in this property class in order to add support for environment variables.
     */
    private static final String SIMULATOR_SERVLET_MAPPING_PROPERTY = "citrus.simulator.ws.servlet.mapping";
    private static final String SIMULATOR_SERVLET_MAPPING_ENV = "CITRUS_SIMULATOR_WS_SERVLET_MAPPING";

    /**
     * Global option to enable/disable SOAP web service support, default is false.
     */
    private boolean enabled;

    /**
     * The web service message dispatcher servlet mapping. Clients must use this
     * context path in order to access the web service support on the simulator.
     */
    private String servletMapping = "/services/ws/*";

    /**
     * The Spring application context environment auto injected by environment aware mechanism.
     */
    private Environment env;

    @PostConstruct
    private void loadProperties() {
        servletMapping = env.getProperty(SIMULATOR_SERVLET_MAPPING_PROPERTY, env.getProperty(SIMULATOR_SERVLET_MAPPING_ENV, servletMapping));

        log.info("Using the simulator configuration: {}", this.toString());
    }

    /**
     * Gets the enabled.
     *
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled.
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
                "enabled='" + enabled + '\'' +
                ", servletMapping='" + servletMapping + '\'' +
                '}';
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
