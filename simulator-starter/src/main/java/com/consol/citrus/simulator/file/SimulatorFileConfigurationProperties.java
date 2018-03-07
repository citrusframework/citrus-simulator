package com.consol.citrus.simulator.file;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

@ConfigurationProperties(prefix = "citrus.simulator.file")
public class SimulatorFileConfigurationProperties implements EnvironmentAware {

    /**
     * Logger
     */
    private static Logger log = LoggerFactory.getLogger(SimulatorFileConfigurationProperties.class);

    /**
     * System property constants and environment variable names. Post construct callback reads these values and overwrites
     * settings in this property class in order to add support for environment variables.
     */
    private static final String SIMULATOR_FILE_INBOUND_CHANNEL_PROPERTY = "citrus.simulator.file.inbound.channel";
    private static final String SIMULATOR_FILE_INBOUND_CHANNEL_ENV = "CITRUS_SIMULATOR_FILE_INBOUND_CHANNEL";

    /**
     * Global option to enable/disable file support, default is false.
     */
    private boolean enabled;

    /**
     * The file inbound channel name. The simulator receives asynchronous messages using this channel.
     */
    private String inboundChannel = "fileInboundChannel";

    /**
     * The Spring application context environment auto injected by environment aware mechanism.
     */
    private Environment env;

    @PostConstruct
    private void loadProperties() {
        inboundChannel =
            env.getProperty(SIMULATOR_FILE_INBOUND_CHANNEL_PROPERTY, env.getProperty(SIMULATOR_FILE_INBOUND_CHANNEL_ENV, inboundChannel));

        if (log.isInfoEnabled()) {
            log.info("Using the simulator configuration: {}", this.toString());
        }
    }

    /**
     * Gets the enabled.
     *
     * @return enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled.
     *
     * @param enabled if file support is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the inboundChannel.
     *
     * @return inboundChannel
     */
    public String getInboundChannel() {
        return inboundChannel;
    }

    /**
     * Sets the inboundChannel.
     *
     * @param inboundChannel inbound channel name
     */
    public void setInboundChannel(String inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
               "enabled='" + enabled + '\'' +
               ", inboundChannel ='" + inboundChannel + '\'' +
               '}';
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
