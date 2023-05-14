package org.citrusframework.simulator.sample;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.citrusframework.simulator.jms.SimulatorJmsAdapter;
import org.citrusframework.simulator.jms.SimulatorJmsConfigurationProperties;

/**
 * @author Christoph Deppisch
 */
public class SimulatorJmsConfig extends SimulatorJmsAdapter {

    private final String brokerURL;

    public SimulatorJmsConfig(String brokerURL) {
        this.brokerURL = brokerURL;
    }

    @Override
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(brokerURL);
    }

    @Override
    public boolean synchronous(SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        return true;
    }
}
