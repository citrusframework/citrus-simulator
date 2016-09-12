package com.consol.citrus.simulator.annotation;

import com.consol.citrus.channel.*;
import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.jms.endpoint.*;
import com.consol.citrus.simulator.endpoint.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.SingleConnectionFactory;

import javax.jms.ConnectionFactory;

/**
 * @author Christoph Deppisch
 */
@Configuration
public class SimulatorJmsSupport {

    @Autowired(required = false)
    private SimulatorJmsConfigurer configurer;

    /** Inbound JMS destination name */
    private static final String JMS_INBOUND_DESTINATION_NAME =
            System.getProperty("citrus.simulator.jms.destination", "Citrus.Simulator.Inbound");

    @Bean(name = "simulatorJmsEndpointPoller")
    public SimulatorEndpointPoller endpointPoller(ApplicationContext applicationContext) {
        SimulatorEndpointPoller endpointPoller;

        if (configurer != null && configurer.useSoapEnvelope()) {
            endpointPoller = new SimulatorSoapEndpointPoller();
        } else {
            endpointPoller = new SimulatorEndpointPoller();
        }

        endpointPoller.setTargetEndpoint(jmsEndpoint());
        SimulatorEndpointAdapter endpointAdapter = simulatorEndpointAdapter();
        endpointAdapter.setApplicationContext(applicationContext);
        endpointAdapter.setMappingKeyExtractor(getMappingKeyExtractor());

        endpointPoller.setEndpointAdapter(endpointAdapter);

        endpointAdapter.setResponseEndpointAdapter(inboundEndpointAdapter(applicationContext));

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
    public ChannelEndpointAdapter inboundEndpointAdapter(ApplicationContext applicationContext) {
        ChannelSyncEndpointConfiguration endpointConfiguration = new ChannelSyncEndpointConfiguration();
        endpointConfiguration.setChannel(inboundChannel());
        ChannelEndpointAdapter endpointAdapter = new ChannelEndpointAdapter(endpointConfiguration);
        endpointAdapter.setApplicationContext(applicationContext);

        return endpointAdapter;
    }

    @Bean(name = "simulatorJmsEndpoint")
    protected JmsEndpoint jmsEndpoint() {
        JmsSyncEndpointConfiguration endpointConfiguration = new JmsSyncEndpointConfiguration();
        JmsSyncEndpoint jmsSyncEndpoint = new JmsSyncEndpoint(endpointConfiguration);
        endpointConfiguration.setDestinationName(getDestinationName());
        endpointConfiguration.setConnectionFactory(connectionFactory());

        return jmsSyncEndpoint;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        if (configurer != null) {
            return configurer.connectionFactory();
        }

        return new SingleConnectionFactory();
    }

    @Bean
    public SimulatorEndpointAdapter simulatorEndpointAdapter() {
        return new SimulatorEndpointAdapter();
    }

    /**
     * Gets the mapping key extractor.
     * @return
     */
    protected MappingKeyExtractor getMappingKeyExtractor() {
        if (configurer != null) {
            return configurer.mappingKeyExtractor();
        }

        return new XPathPayloadMappingKeyExtractor();
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
