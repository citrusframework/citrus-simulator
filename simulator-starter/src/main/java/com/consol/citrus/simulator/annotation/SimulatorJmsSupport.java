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
import com.consol.citrus.context.TestContext;
import com.consol.citrus.context.TestContextFactory;
import com.consol.citrus.endpoint.*;
import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.jms.endpoint.*;
import com.consol.citrus.message.Message;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.endpoint.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.jms.connection.SingleConnectionFactory;

import javax.jms.ConnectionFactory;

@Configuration
@EnableConfigurationProperties(SimulatorJmsConfigurationProperties.class)
public class SimulatorJmsSupport {

    @Autowired(required = false)
    private SimulatorJmsConfigurer configurer;

    @Bean
    @ConditionalOnMissingBean
    public ConnectionFactory connectionFactory() {
        if (configurer != null) {
            return configurer.connectionFactory();
        }

        return new SingleConnectionFactory();
    }

    @Bean(name = "simulator.jms.inbound")
    public MessageSelectingQueueChannel inboundChannel() {
        MessageSelectingQueueChannel inboundChannel = new MessageSelectingQueueChannel();
        return inboundChannel;
    }

    @Bean(name = "simulatorJmsInboundEndpoint")
    public ChannelEndpoint inboundChannelEndpoint(SimulatorConfigurationProperties configuration,
                                                  SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        if (isSynchronous(simulatorJmsConfiguration)) {
            ChannelSyncEndpoint inboundChannelEndpoint = new ChannelSyncEndpoint();
            inboundChannelEndpoint.getEndpointConfiguration().setUseObjectMessages(true);
            inboundChannelEndpoint.getEndpointConfiguration().setChannel(inboundChannel());
            inboundChannelEndpoint.getEndpointConfiguration().setTimeout(configuration.getDefaultTimeout());
            return inboundChannelEndpoint;
        } else {
            ChannelEndpoint inboundChannelEndpoint = new ChannelEndpoint();
            inboundChannelEndpoint.getEndpointConfiguration().setUseObjectMessages(true);
            inboundChannelEndpoint.getEndpointConfiguration().setChannel(inboundChannel());
            inboundChannelEndpoint.getEndpointConfiguration().setTimeout(configuration.getDefaultTimeout());
            return inboundChannelEndpoint;
        }
    }

    @Bean(name = "simulatorJmsInboundEndpointAdapter")
    public EndpointAdapter inboundEndpointAdapter(ApplicationContext applicationContext,
                                                         SimulatorConfigurationProperties configuration,
                                                         SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        if (isSynchronous(simulatorJmsConfiguration)) {
            ChannelSyncEndpointConfiguration endpointConfiguration = new ChannelSyncEndpointConfiguration();
            endpointConfiguration.setChannel(inboundChannel());
            ChannelEndpointAdapter endpointAdapter = new ChannelEndpointAdapter(endpointConfiguration);
            endpointAdapter.setApplicationContext(applicationContext);

            return endpointAdapter;
        } else {
            class JmsReceiveEndpointAdapter implements EndpointAdapter {
                private final TestContextFactory testContextFactory;
                private final Endpoint endpoint;

                public JmsReceiveEndpointAdapter(TestContextFactory testContextFactory, Endpoint endpoint) {
                    this.testContextFactory = testContextFactory;
                    this.endpoint = endpoint;
                }

                @Override
                public Message handleMessage(Message message) {
                    getEndpoint().createProducer().send(message, getTestContext());
                    return null;
                }

                @Override
                public Endpoint getEndpoint() {
                    return endpoint;
                }

                @Override
                public EndpointConfiguration getEndpointConfiguration() {
                    return null;
                }

                private TestContext getTestContext() {
                    return testContextFactory.getObject();
                }
            }

            return new JmsReceiveEndpointAdapter(TestContextFactory.newInstance(applicationContext), inboundChannelEndpoint(configuration, simulatorJmsConfiguration));
        }
    }

    @Bean(name = "simulatorJmsEndpoint")
    protected JmsEndpoint jmsInboundEndpoint(ConnectionFactory connectionFactory,
                                      SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        if (isSynchronous(simulatorJmsConfiguration)) {
            JmsSyncEndpointConfiguration endpointConfiguration = new JmsSyncEndpointConfiguration();
            JmsSyncEndpoint jmsEndpoint = new JmsSyncEndpoint(endpointConfiguration);
            endpointConfiguration.setDestinationName(getInboundDestination(simulatorJmsConfiguration));
            endpointConfiguration.setConnectionFactory(connectionFactory);

            return jmsEndpoint;
        } else {
            JmsEndpointConfiguration endpointConfiguration = new JmsEndpointConfiguration();
            JmsEndpoint jmsEndpoint = new JmsEndpoint(endpointConfiguration);
            endpointConfiguration.setDestinationName(getInboundDestination(simulatorJmsConfiguration));
            endpointConfiguration.setConnectionFactory(connectionFactory);

            return jmsEndpoint;
        }
    }

    @Bean(name = "simulatorJmsOutboundEndpoint")
    @DependsOn("simulatorJmsInboundEndpoint")
    protected Endpoint jmsOutboundEndpoint(ConnectionFactory connectionFactory,
                                          SimulatorConfigurationProperties configuration,
                                          SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        if (isSynchronous(simulatorJmsConfiguration)) {
            return inboundChannelEndpoint(configuration, simulatorJmsConfiguration);
        } else {
            JmsEndpointConfiguration endpointConfiguration = new JmsEndpointConfiguration();
            JmsEndpoint jmsEndpoint = new JmsEndpoint(endpointConfiguration);
            endpointConfiguration.setDestinationName(getOutboundDestination(simulatorJmsConfiguration));
            endpointConfiguration.setConnectionFactory(connectionFactory);

            return jmsEndpoint;
        }
    }

    @Bean
    public SimulatorEndpointAdapter simulatorEndpointAdapter() {
        return new SimulatorEndpointAdapter();
    }

    @Bean
    @DependsOn("simulatorJmsInboundEndpoint")
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

    @Bean(name = "simulatorJmsEndpointPoller")
    public SimulatorEndpointPoller endpointPoller(ApplicationContext applicationContext,
                                                  ConnectionFactory connectionFactory,
                                                  SimulatorConfigurationProperties configuration,
                                                  SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        SimulatorEndpointPoller endpointPoller;

        if (useSoap(simulatorJmsConfiguration)) {
            endpointPoller = new SimulatorSoapEndpointPoller();
        } else {
            endpointPoller = new SimulatorEndpointPoller();
        }

        endpointPoller.setTargetEndpoint(jmsInboundEndpoint(connectionFactory, simulatorJmsConfiguration));
        SimulatorEndpointAdapter endpointAdapter = simulatorEndpointAdapter();
        endpointAdapter.setApplicationContext(applicationContext);
        endpointAdapter.setMappingKeyExtractor(simulatorMappingKeyExtractor());

        endpointPoller.setEndpointAdapter(endpointAdapter);

        endpointAdapter.setResponseEndpointAdapter(inboundEndpointAdapter(applicationContext, configuration, simulatorJmsConfiguration));

        return endpointPoller;
    }

    /**
     * Gets the destination name to receive messages from.
     *
     * @return
     */
    protected String getInboundDestination(SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        if (configurer != null) {
            return configurer.inboundDestination(simulatorJmsConfiguration);
        }

        return simulatorJmsConfiguration.getInboundDestination();
    }

    /**
     * Gets the destination name to send messages to.
     *
     * @return
     */
    protected String getOutboundDestination(SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        if (configurer != null) {
            return configurer.outboundDestination(simulatorJmsConfiguration);
        }

        return simulatorJmsConfiguration.getOutboundDestination();
    }

    /**
     * Should the endpoint use synchronous reply communication.
     * @param simulatorJmsConfiguration
     * @return
     */
    protected boolean isSynchronous(SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        if (configurer != null) {
            return configurer.synchronous(simulatorJmsConfiguration);
        }

        return simulatorJmsConfiguration.isSynchronous();
    }

    /**
     * Should the endpoint use SOAP envelope handling.
     * @param simulatorJmsConfiguration
     * @return
     */
    protected boolean useSoap(SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        if (configurer != null) {
            return configurer.useSoap(simulatorJmsConfiguration);
        }

        return simulatorJmsConfiguration.isUseSoap();
    }
}
