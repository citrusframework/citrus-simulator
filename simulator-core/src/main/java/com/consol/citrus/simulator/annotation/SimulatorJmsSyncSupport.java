/*
 * Copyright 2006-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.simulator.annotation;

import com.consol.citrus.channel.*;
import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.jms.endpoint.*;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.endpoint.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

import javax.jms.ConnectionFactory;

/**
 * @author Christoph Deppisch
 */
@Configuration
@Import(SimulatorJmsSupport.class)
public class SimulatorJmsSyncSupport {

    @Autowired(required = false)
    private SimulatorJmsSyncConfigurer configurer;

    @Bean(name = "simulator.jms.sync.inbound")
    public MessageSelectingQueueChannel inboundChannel() {
        MessageSelectingQueueChannel inboundChannel = new MessageSelectingQueueChannel();
        return inboundChannel;
    }

    @Bean(name = "simulatorJmsSyncReceiveEndpoint")
    public ChannelEndpoint inboundChannelEndpoint(SimulatorConfigurationProperties configuration) {
        ChannelSyncEndpoint inboundChannelEndpoint = new ChannelSyncEndpoint();
        inboundChannelEndpoint.getEndpointConfiguration().setUseObjectMessages(true);
        inboundChannelEndpoint.getEndpointConfiguration().setChannel(inboundChannel());
        inboundChannelEndpoint.getEndpointConfiguration().setTimeout(configuration.getDefaultTimeout());
        return inboundChannelEndpoint;
    }

    @Bean(name = "simulatorJmsSyncReceiveEndpointAdapterAdapter")
    public ChannelEndpointAdapter inboundEndpointAdapter(ApplicationContext applicationContext) {
        ChannelSyncEndpointConfiguration endpointConfiguration = new ChannelSyncEndpointConfiguration();
        endpointConfiguration.setChannel(inboundChannel());
        ChannelEndpointAdapter endpointAdapter = new ChannelEndpointAdapter(endpointConfiguration);
        endpointAdapter.setApplicationContext(applicationContext);

        return endpointAdapter;
    }

    @Bean(name = "simulatorJmsSyncInboundEndpoint")
    protected JmsEndpoint jmsEndpoint(ConnectionFactory connectionFactory,
                                      SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        JmsSyncEndpointConfiguration endpointConfiguration = new JmsSyncEndpointConfiguration();
        JmsSyncEndpoint jmsEndpoint = new JmsSyncEndpoint(endpointConfiguration);
        endpointConfiguration.setDestinationName(getReceiveDestinationName(simulatorJmsConfiguration));
        endpointConfiguration.setConnectionFactory(connectionFactory);

        return jmsEndpoint;
    }

    @Bean
    public SimulatorEndpointAdapter simulatorEndpointAdapter() {
        return new SimulatorEndpointAdapter();
    }

    @Bean
    @DependsOn("simulatorJmsSyncInboundEndpoint")
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

    @Bean(name = "simulatorJmsSyncEndpointPoller")
    public SimulatorEndpointPoller endpointPoller(ApplicationContext applicationContext,
                                                  ConnectionFactory connectionFactory,
                                                  SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        SimulatorEndpointPoller endpointPoller;

        if (configurer != null && configurer.useSoapEnvelope()) {
            endpointPoller = new SimulatorSoapEndpointPoller();
        } else {
            endpointPoller = new SimulatorEndpointPoller();
        }

        endpointPoller.setTargetEndpoint(jmsEndpoint(connectionFactory, simulatorJmsConfiguration));
        SimulatorEndpointAdapter endpointAdapter = simulatorEndpointAdapter();
        endpointAdapter.setApplicationContext(applicationContext);
        endpointAdapter.setMappingKeyExtractor(simulatorMappingKeyExtractor());

        endpointPoller.setEndpointAdapter(endpointAdapter);

        endpointAdapter.setResponseEndpointAdapter(inboundEndpointAdapter(applicationContext));

        return endpointPoller;
    }

    /**
     * Gets the destination name to receive messages from.
     *
     * @return
     */
    protected String getReceiveDestinationName(SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        if (configurer != null) {
            return configurer.receiveDestinationName(simulatorJmsConfiguration);
        }

        return simulatorJmsConfiguration.getReceiveDestination();
    }
}
