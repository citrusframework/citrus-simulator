package com.consol.citrus.simulator.annotation;

import com.consol.citrus.channel.*;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.simulator.endpoint.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

/**
 * @author Christoph Deppisch
 */
@Configuration
public class SimulatorEndpointSupport {

    @Autowired(required = false)
    private SimulatorConfigurer configurer;

    @Bean(name = "simulator.inbound")
    public MessageSelectingQueueChannel inboundChannel() {
        MessageSelectingQueueChannel inboundChannel = new MessageSelectingQueueChannel();
        return inboundChannel;
    }

    @Bean(name = "simulatorInboundEndpoint")
    public ChannelEndpoint inboundChannelEndpoint() {
        ChannelSyncEndpoint inboundChannelEndpoint = new ChannelSyncEndpoint();
        inboundChannelEndpoint.getEndpointConfiguration().setUseObjectMessages(true);
        inboundChannelEndpoint.getEndpointConfiguration().setChannel(inboundChannel());
        return inboundChannelEndpoint;
    }

    @Bean(name = "simulatorInboundAdapter")
    public ChannelEndpointAdapter inboundEndpointAdapter(ApplicationContext applicationContext) {
        ChannelSyncEndpointConfiguration endpointConfiguration = new ChannelSyncEndpointConfiguration();
        endpointConfiguration.setChannel(inboundChannel());
        ChannelEndpointAdapter endpointAdapter = new ChannelEndpointAdapter(endpointConfiguration);
        endpointAdapter.setApplicationContext(applicationContext);

        return endpointAdapter;
    }

    @Bean(name = "simulatorEndpoint")
    protected Endpoint simEndpoint(ApplicationContext applicationContext) {
        if (configurer != null) {
            return configurer.endpoint(applicationContext);
        } else {
            ChannelSyncEndpointConfiguration endpointConfiguration = new ChannelSyncEndpointConfiguration();
            ChannelSyncEndpoint syncEndpoint = new ChannelSyncEndpoint(endpointConfiguration);
            endpointConfiguration.setChannelName("simulator.endpoint.inbound");

            return syncEndpoint;
        }
    }

    @Bean
    public SimulatorEndpointAdapter simulatorEndpointAdapter() {
        return new SimulatorEndpointAdapter();
    }

    @Bean
    @DependsOn("simulatorInboundEndpoint")
    public MappingKeyExtractor simulatorMappingKeyExtractor() {
        SimulatorMappingKeyExtractor simulatorMappingKeyExtractor = new SimulatorMappingKeyExtractor();
        simulatorMappingKeyExtractor.setDelegate(delegateMappingKeyExtractor());
        return simulatorMappingKeyExtractor;
    }

    @Bean
    public MappingKeyExtractor delegateMappingKeyExtractor() {
        if (configurer != null) {
            return configurer.mappingKeyExtractor();
        }

        return new XPathPayloadMappingKeyExtractor();
    }

    @Bean(name = "simulatorEndpointPoller")
    public SimulatorEndpointPoller endpointPoller(ApplicationContext applicationContext) {
        SimulatorEndpointPoller endpointPoller;

        if (configurer != null && configurer.useSoapEnvelope()) {
            endpointPoller = new SimulatorSoapEndpointPoller();
        } else {
            endpointPoller = new SimulatorEndpointPoller();
        }

        endpointPoller.setTargetEndpoint(simEndpoint(applicationContext));
        SimulatorEndpointAdapter endpointAdapter = simulatorEndpointAdapter();
        endpointAdapter.setApplicationContext(applicationContext);
        endpointAdapter.setMappingKeyExtractor(simulatorMappingKeyExtractor());

        endpointPoller.setEndpointAdapter(endpointAdapter);

        endpointAdapter.setResponseEndpointAdapter(inboundEndpointAdapter(applicationContext));

        return endpointPoller;
    }
}
