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
    private static final String SIMULATOR_RECEIVE_DESTINATION_PROPERTY = "citrus.simulator.jms.receive.destination";
    private static final String SIMULATOR_RECEIVE_DESTINATION_ENV = "CITRUS_SIMULATOR_JMS_RECEIVE_DESTINATION";
    private String receiveDestination = "Citrus.Simulator.Inbound";

    /**
     * The JMS outbound destination name. The simulator sends asynchronous messages to this destination.
     */
    private static final String SIMULATOR_SEND_DESTINATION_PROPERTY = "citrus.simulator.jms.send.destination";
    private static final String SIMULATOR_SEND_DESTINATION_ENV = "CITRUS_SIMULATOR_JMS_SEND_DESTINATION";
    private String sendDestination = "Citrus.Simulator.Outbound";

    /**
     * The Spring application context environment
     */
    private Environment env;

    @PostConstruct
    private void loadProperties() {
        receiveDestination = env.getProperty(SIMULATOR_RECEIVE_DESTINATION_PROPERTY, env.getProperty(SIMULATOR_RECEIVE_DESTINATION_ENV, receiveDestination));
        sendDestination = env.getProperty(SIMULATOR_SEND_DESTINATION_PROPERTY, env.getProperty(SIMULATOR_SEND_DESTINATION_ENV, sendDestination));

        log.info("Using the simulator configuration: {}", this.toString());
    }

    /**
     * Gets the receiveDestination.
     *
     * @return
     */
    public String getReceiveDestination() {
        return receiveDestination;
    }

    /**
     * Sets the receiveDestination.
     *
     * @param receiveDestination
     */
    public void setReceiveDestination(String receiveDestination) {
        this.receiveDestination = receiveDestination;
    }

    /**
     * Gets the sendDestination.
     *
     * @return
     */
    public String getSendDestination() {
        return sendDestination;
    }

    /**
     * Sets the sendDestination.
     *
     * @param sendDestination
     */
    public void setSendDestination(String sendDestination) {
        this.sendDestination = sendDestination;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "receiveDestination='" + receiveDestination + '\'' +
                ", sendDestination='" + sendDestination + '\'' +
                '}';
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
