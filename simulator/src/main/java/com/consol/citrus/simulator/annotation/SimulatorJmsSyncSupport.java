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
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import com.consol.citrus.jms.endpoint.JmsSyncEndpoint;
import com.consol.citrus.jms.endpoint.JmsSyncEndpointConfiguration;
import com.consol.citrus.simulator.endpoint.SimulatorEndpointAdapter;
import com.consol.citrus.simulator.endpoint.SimulatorEndpointPoller;
import com.consol.citrus.simulator.endpoint.SimulatorMappingKeyExtractor;
import com.consol.citrus.simulator.endpoint.SimulatorSoapEndpointPoller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.connection.SingleConnectionFactory;

import javax.jms.ConnectionFactory;

import static com.consol.citrus.simulator.annotation.SimulatorJmsSyncConfigurer.RECEIVE_DESTINATION_VALUE_DEFAULT;

/**
 * @author Christoph Deppisch
 */
@Configuration
public class SimulatorJmsSyncSupport {

    @Autowired(required = false)
    private SimulatorJmsSyncConfigurer configurer;

    /**
     * Inbound JMS destination name
     */
    private static final String RECEIVE_DESTINATION_NAME =
            System.getProperty(SimulatorJmsSyncConfigurer.RECEIVE_DESTINATION_NAME_KEY, RECEIVE_DESTINATION_VALUE_DEFAULT);

    @Bean(name = "simulator.jms.sync.inbound")
    public MessageSelectingQueueChannel inboundChannel() {
        MessageSelectingQueueChannel inboundChannel = new MessageSelectingQueueChannel();
        return inboundChannel;
    }

    @Bean(name = "simulatorJmsSyncInboundEndpoint")
    public ChannelEndpoint inboundChannelEndpoint() {
        ChannelSyncEndpoint inboundChannelEndpoint = new ChannelSyncEndpoint();
        inboundChannelEndpoint.getEndpointConfiguration().setUseObjectMessages(true);
        inboundChannelEndpoint.getEndpointConfiguration().setChannel(inboundChannel());
        return inboundChannelEndpoint;
    }

    @Bean(name = "simulatorJmsSyncInboundAdapter")
    public ChannelEndpointAdapter inboundEndpointAdapter(ApplicationContext applicationContext) {
        ChannelSyncEndpointConfiguration endpointConfiguration = new ChannelSyncEndpointConfiguration();
        endpointConfiguration.setChannel(inboundChannel());
        ChannelEndpointAdapter endpointAdapter = new ChannelEndpointAdapter(endpointConfiguration);
        endpointAdapter.setApplicationContext(applicationContext);

        return endpointAdapter;
    }

    @Bean(name = "simulatorJmsSyncEndpoint")
    protected JmsEndpoint jmsEndpoint() {
        JmsSyncEndpointConfiguration endpointConfiguration = new JmsSyncEndpointConfiguration();
        JmsSyncEndpoint jmsEndpoint = new JmsSyncEndpoint(endpointConfiguration);
        endpointConfiguration.setDestinationName(getReceiveDestinationName());
        endpointConfiguration.setConnectionFactory(connectionFactory());

        return jmsEndpoint;
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

    @Bean
    @DependsOn("simulatorSyncJmsInboundEndpoint")
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
    protected String getReceiveDestinationName() {
        if (configurer != null) {
            return configurer.receiveDestinationName();
        }

        return RECEIVE_DESTINATION_NAME;
    }
}
