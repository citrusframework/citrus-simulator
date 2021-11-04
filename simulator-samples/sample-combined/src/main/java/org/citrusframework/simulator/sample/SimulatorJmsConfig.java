package org.citrusframework.simulator.sample;

import org.citrusframework.simulator.jms.SimulatorJmsAdapter;
import org.citrusframework.simulator.jms.SimulatorJmsConfigurationProperties;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.ConnectionFactory;

/**
 * @author Christoph Deppisch
 */
public class SimulatorJmsConfig extends SimulatorJmsAdapter {

    @Override
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    @Override
    public boolean synchronous(SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        return true;
    }
}
