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
@ConfigurationProperties(prefix = "citrus.simulator.jms")
public class SimulatorJmsConfigurationProperties implements EnvironmentAware {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(SimulatorJmsConfigurationProperties.class);

    /**
     * The JMS inbound destination name. The simulator receives asynchronous messages from this destination.
     */
    private static final String SIMULATOR_INBOUND_DESTINATION_PROPERTY = "citrus.simulator.jms.inbound.destination";
    private static final String SIMULATOR_INBOUND_DESTINATION_ENV = "CITRUS_SIMULATOR_JMS_INBOUND_DESTINATION";
    private String inboundDestination = "Citrus.Simulator.Inbound";

    /**
     * The JMS outbound destination name. The simulator sends asynchronous messages to this destination.
     */
    private static final String SIMULATOR_OUTBOUND_DESTINATION_PROPERTY = "citrus.simulator.jms.outbound.destination";
    private static final String SIMULATOR_OUTBOUND_DESTINATION_ENV = "CITRUS_SIMULATOR_JMS_OUTBOUND_DESTINATION";
    private String outboundDestination = "Citrus.Simulator.Outbound";

    /**
     * En-/Disable JMS synchronous communication.
     */
    private static final String SIMULATOR_SYNC_PROPERTY = "citrus.simulator.jms.synchronous";
    private static final String SIMULATOR_SYNC_ENV = "CITRUS_SIMULATOR_JMS_SYNCHRONOUS";
    private boolean synchronous = false;

    /**
     * En-/Disable JMS synchronous communication.
     */
    private static final String SIMULATOR_SOAP_ENVELOPE_PROPERTY = "citrus.simulator.jms.soap";
    private static final String SIMULATOR_SOAP_ENVELOPE_ENV = "CITRUS_SIMULATOR_JMS_SOAP";
    private boolean useSoap = false;

    /**
     * The Spring application context environment
     */
    private Environment env;

    @PostConstruct
    private void loadProperties() {
        inboundDestination = env.getProperty(SIMULATOR_INBOUND_DESTINATION_PROPERTY, env.getProperty(SIMULATOR_INBOUND_DESTINATION_ENV, inboundDestination));
        outboundDestination = env.getProperty(SIMULATOR_OUTBOUND_DESTINATION_PROPERTY, env.getProperty(SIMULATOR_OUTBOUND_DESTINATION_ENV, outboundDestination));
        synchronous = Boolean.valueOf(env.getProperty(SIMULATOR_SYNC_PROPERTY, env.getProperty(SIMULATOR_SYNC_ENV, String.valueOf(synchronous))));
        useSoap = Boolean.valueOf(env.getProperty(SIMULATOR_SOAP_ENVELOPE_PROPERTY, env.getProperty(SIMULATOR_SOAP_ENVELOPE_ENV, String.valueOf(useSoap))));

        log.info("Using the simulator configuration: {}", this.toString());
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
     * Gets the outboundDestination.
     *
     * @return
     */
    public String getOutboundDestination() {
        return outboundDestination;
    }

    /**
     * Sets the outboundDestination.
     *
     * @param outboundDestination
     */
    public void setOutboundDestination(String outboundDestination) {
        this.outboundDestination = outboundDestination;
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
                "inboundDestination='" + inboundDestination + '\'' +
                ", outboundDestination='" + outboundDestination + '\'' +
                ", synchronous='" + synchronous + '\'' +
                ", useSoap='" + useSoap + '\'' +
                '}';
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
