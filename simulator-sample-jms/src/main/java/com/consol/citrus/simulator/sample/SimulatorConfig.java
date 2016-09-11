package com.consol.citrus.simulator.sample;

import com.consol.citrus.simulator.annotation.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.*;

import javax.jms.ConnectionFactory;

/**
 * @author Christoph Deppisch
 */
@Configuration
@EnableJms
@ComponentScan
public class SimulatorConfig extends SimulatorJmsAdapter {

    @Override
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

}
