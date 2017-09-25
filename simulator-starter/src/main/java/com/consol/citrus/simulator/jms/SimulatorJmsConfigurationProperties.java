package com.consol.citrus.simulator.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

/**
 * @author Christoph Deppisch
 */
@ConfigurationProperties(prefix = "citrus.simulator.jms")
public class SimulatorJmsConfigurationProperties implements EnvironmentAware {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(SimulatorJmsConfigurationProperties.class);

    /**
     * System property constants and environment variable names. Post construct callback reads these values and overwrites
     * settings in this property class in order to add support for environment variables.
     */
    private static final String SIMULATOR_INBOUND_DESTINATION_PROPERTY = "citrus.simulator.jms.inbound.destination";
    private static final String SIMULATOR_INBOUND_DESTINATION_ENV = "CITRUS_SIMULATOR_JMS_INBOUND_DESTINATION";
    private static final String SIMULATOR_REPLY_DESTINATION_PROPERTY = "citrus.simulator.jms.reply.destination";
    private static final String SIMULATOR_REPLY_DESTINATION_ENV = "CITRUS_SIMULATOR_JMS_REPLY_DESTINATION";
    private static final String SIMULATOR_SYNC_PROPERTY = "citrus.simulator.jms.synchronous";
    private static final String SIMULATOR_SYNC_ENV = "CITRUS_SIMULATOR_JMS_SYNCHRONOUS";
    private static final String SIMULATOR_SOAP_ENVELOPE_PROPERTY = "citrus.simulator.jms.soap";
    private static final String SIMULATOR_SOAP_ENVELOPE_ENV = "CITRUS_SIMULATOR_JMS_SOAP";

    /**
     * Global option to enable/disable JMS support, default is false.
     */
    private boolean enabled;

    /**
     * The JMS inbound destination name. The simulator receives asynchronous messages using this destination.
     */
    private String inboundDestination = "Citrus.Simulator.Inbound";

    /**
     * The JMS reply destination name. The simulator sends asynchronous messages to this destination.
     */
    private String replyDestination = "";

    /**
     * En-/Disable JMS synchronous communication. By default this option is disabled.
     */
    private boolean synchronous = false;

    /**
     * En-/Disable JMS synchronous communication. By default this option is disabled.
     */
    private boolean useSoap = false;

    /**
     * The Spring application context environment auto injected by environment aware mechanism.
     */
    private Environment env;

    @PostConstruct
    private void loadProperties() {
        inboundDestination = env.getProperty(SIMULATOR_INBOUND_DESTINATION_PROPERTY, env.getProperty(SIMULATOR_INBOUND_DESTINATION_ENV, inboundDestination));
        replyDestination = env.getProperty(SIMULATOR_REPLY_DESTINATION_PROPERTY, env.getProperty(SIMULATOR_REPLY_DESTINATION_ENV, replyDestination));
        synchronous = Boolean.valueOf(env.getProperty(SIMULATOR_SYNC_PROPERTY, env.getProperty(SIMULATOR_SYNC_ENV, String.valueOf(synchronous))));
        useSoap = Boolean.valueOf(env.getProperty(SIMULATOR_SOAP_ENVELOPE_PROPERTY, env.getProperty(SIMULATOR_SOAP_ENVELOPE_ENV, String.valueOf(useSoap))));

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
     * Gets the inboundDestination.
     *
     * @return
     */
    public String getInboundDestination() {
        return inboundDestination;
    }

    /**
     * Sets the inboundDestination.
     *
     * @param inboundDestination
     */
    public void setInboundDestination(String inboundDestination) {
        this.inboundDestination = inboundDestination;
    }

    /**
     * Gets the replyDestination.
     *
     * @return
     */
    public String getReplyDestination() {
        return replyDestination;
    }

    /**
     * Sets the replyDestination.
     *
     * @param replyDestination
     */
    public void setReplyDestination(String replyDestination) {
        this.replyDestination = replyDestination;
    }

    /**
     * Gets the synchronous.
     *
     * @return
     */
    public boolean isSynchronous() {
        return synchronous;
    }

    /**
     * Sets the synchronous.
     *
     * @param synchronous
     */
    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    /**
     * Gets the useSoap.
     *
     * @return
     */
    public boolean isUseSoap() {
        return useSoap;
    }

    /**
     * Sets the useSoap.
     *
     * @param useSoap
     */
    public void setUseSoap(boolean useSoap) {
        this.useSoap = useSoap;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "enabled='" + enabled + '\'' +
                ", inboundDestination='" + inboundDestination + '\'' +
                ", replyDestination='" + replyDestination + '\'' +
                ", synchronous='" + synchronous + '\'' +
                ", useSoap='" + useSoap + '\'' +
                '}';
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
