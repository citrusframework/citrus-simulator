package com.consol.citrus.simulator.annotation;

import com.consol.citrus.channel.*;
import com.consol.citrus.jms.endpoint.*;
import com.consol.citrus.simulator.endpoint.SimulatorEndpointPoller;
import com.consol.citrus.simulator.endpoint.SimulatorSoapEndpointPoller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

/**
 * @author Christoph Deppisch
 */
public class SimulatorJmsSupport {

    @Autowired(required = false)
    private SimulatorJmsConfigurer configurer;

    /** Inbound JMS destination name */
    private static final String JMS_INBOUND_DESTINATION_NAME =
            System.getProperty("citrus.simulator.jms.destination", "Citrus.Simulator.Inbound");

    @Bean(name = "simulatorJmsEndpointPoller")
    public SimulatorEndpointPoller endpointPoller() {
        SimulatorEndpointPoller endpointPoller;

        if (configurer != null && configurer.useSoapEnvelope()) {
            endpointPoller =new SimulatorSoapEndpointPoller();
        } else {
            endpointPoller =new SimulatorEndpointPoller();
        }

        endpointPoller.setTargetEndpoint(jmsEndpoint());
        endpointPoller.setEndpointAdapter(inboundEndpointAdapter());

        return endpointPoller;
    }

    @Bean(name = "simulator.jms.inbound")
    public MessageSelectingQueueChannel inboundChannel() {
        MessageSelectingQueueChannel inboundChannel = new MessageSelectingQueueChannel();
        return inboundChannel;
    }

    @Bean(name = "simulatorJmsInboundEndpoint")
    public ChannelEndpoint inboundChannelEndpoint() {
        ChannelSyncEndpoint inboundChannelEndpoint = new ChannelSyncEndpoint();
        inboundChannelEndpoint.getEndpointConfiguration().setUseObjectMessages(true);
        inboundChannelEndpoint.getEndpointConfiguration().setChannel(inboundChannel());
        return inboundChannelEndpoint;
    }

    @Bean(name = "simulatorJmsInboundAdapter")
    public ChannelEndpointAdapter inboundEndpointAdapter() {
        ChannelSyncEndpointConfiguration endpointConfiguration = new ChannelSyncEndpointConfiguration();
        endpointConfiguration.setChannel(inboundChannel());
        return new ChannelEndpointAdapter(endpointConfiguration);
    }

    @Bean(name = "simulatorJmsEndpoint")
    protected JmsEndpoint jmsEndpoint() {
        JmsSyncEndpointConfiguration endpointConfiguration = new JmsSyncEndpointConfiguration();
        JmsSyncEndpoint jmsSyncEndpoint = new JmsSyncEndpoint(endpointConfiguration);
        endpointConfiguration.setDestinationName(getDestinationName());

        return jmsSyncEndpoint;
    }

    /**
     * Gets the destination name to receive messages from.
     * @return
     */
    protected String getDestinationName() {
        if (configurer != null) {
            return configurer.destinationName();
        }

        return JMS_INBOUND_DESTINATION_NAME;
    }
}
