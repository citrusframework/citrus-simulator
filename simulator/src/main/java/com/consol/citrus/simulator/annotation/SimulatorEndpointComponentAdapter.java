package com.consol.citrus.simulator.annotation;

import com.consol.citrus.channel.ChannelSyncEndpoint;
import com.consol.citrus.channel.ChannelSyncEndpointConfiguration;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import org.springframework.context.ApplicationContext;

/**
 * @author Christoph Deppisch
 */
public abstract class SimulatorEndpointComponentAdapter implements SimulatorEndpointComponentConfigurer {

    @Override
    public Endpoint endpoint(ApplicationContext applicationContext) {
        ChannelSyncEndpointConfiguration endpointConfiguration = new ChannelSyncEndpointConfiguration();
        ChannelSyncEndpoint syncEndpoint = new ChannelSyncEndpoint(endpointConfiguration);
        endpointConfiguration.setChannelName("simulator.endpoint.inbound");

        return syncEndpoint;
    }

    @Override
    public boolean useSoapEnvelope() {
        return false;
    }

    @Override
    public MappingKeyExtractor mappingKeyExtractor() {
        return new XPathPayloadMappingKeyExtractor();
    }
}
