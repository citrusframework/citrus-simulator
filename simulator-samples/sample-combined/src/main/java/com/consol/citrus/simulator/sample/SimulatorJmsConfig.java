package com.consol.citrus.simulator.sample;

import com.consol.citrus.simulator.annotation.SimulatorJmsAdapter;
import com.consol.citrus.simulator.annotation.SimulatorJmsConfigurationProperties;
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
