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

import com.consol.citrus.channel.ChannelEndpoint;
import com.consol.citrus.channel.MessageSelectingQueueChannel;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.context.TestContextFactory;
import com.consol.citrus.endpoint.*;
import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import com.consol.citrus.jms.endpoint.JmsEndpointConfiguration;
import com.consol.citrus.message.Message;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.endpoint.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

import javax.jms.ConnectionFactory;

/**
 * @author Martin Maher
 */
@Configuration
@Import(SimulatorJmsSupport.class)
public class SimulatorJmsAsyncSupport {

    @Autowired(required = false)
    private SimulatorJmsAsyncConfigurer configurer;

    @Bean(name = "simulator.jms.async.inbound")
    public MessageSelectingQueueChannel inboundChannel() {
        MessageSelectingQueueChannel inboundChannel = new MessageSelectingQueueChannel();
        return inboundChannel;
    }

    @Bean(name = "simulatorJmsAsyncReceiveEndpoint")
    public ChannelEndpoint jmsReceiveEndpoint(SimulatorConfigurationProperties configuration) {
        ChannelEndpoint inboundChannelEndpoint = new ChannelEndpoint();
        inboundChannelEndpoint.getEndpointConfiguration().setUseObjectMessages(true);
        inboundChannelEndpoint.getEndpointConfiguration().setChannel(inboundChannel());
        inboundChannelEndpoint.getEndpointConfiguration().setTimeout(configuration.getDefaultTimeout());
        return inboundChannelEndpoint;
    }

    @Bean(name = "simulatorJmsAsyncReceiveEndpointAdapter")
    public EndpointAdapter jmsReceiveEndpointAdapter(final ApplicationContext applicationContext,
                                                     SimulatorConfigurationProperties configuration) {

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

        return new JmsReceiveEndpointAdapter(TestContextFactory.newInstance(applicationContext), jmsReceiveEndpoint(configuration));
    }


    @Bean(name = "simulatorJmsAsyncInboundEndpoint")
    protected JmsEndpoint jmsAsyncInbountEndpoint(ConnectionFactory connectionFactory,
                                                  SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        JmsEndpointConfiguration endpointConfiguration = new JmsEndpointConfiguration();
        JmsEndpoint jmsEndpoint = new JmsEndpoint(endpointConfiguration);
        endpointConfiguration.setDestinationName(getReceiveDestinationName(simulatorJmsConfiguration));
        endpointConfiguration.setConnectionFactory(connectionFactory);

        return jmsEndpoint;
    }

    @Bean(name = "simulatorJmsAsyncSendEndpoint")
    protected JmsEndpoint jmsSendEndpoint(ConnectionFactory connectionFactory,
                                          SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        JmsEndpointConfiguration endpointConfiguration = new JmsEndpointConfiguration();
        JmsEndpoint jmsEndpoint = new JmsEndpoint(endpointConfiguration);
        endpointConfiguration.setDestinationName(getSendDestinationName(simulatorJmsConfiguration));
        endpointConfiguration.setConnectionFactory(connectionFactory);

        return jmsEndpoint;
    }

    @Bean
    public SimulatorEndpointAdapter simulatorEndpointAdapter() {
        return new SimulatorEndpointAdapter();
    }

    @Bean
    @DependsOn("simulatorJmsAsyncInboundEndpoint")
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

    @Bean(name = "simulatorJmsAsyncEndpointPoller")
    public SimulatorEndpointPoller endpointPoller(ApplicationContext applicationContext,
                                                  ConnectionFactory connectionFactory,
                                                  SimulatorConfigurationProperties configuration,
                                                  SimulatorJmsConfigurationProperties jmsConfiguration) {
        SimulatorEndpointPoller endpointPoller;

        if (configurer != null && configurer.useSoapEnvelope()) {
            endpointPoller = new SimulatorSoapEndpointPoller();
        } else {
            endpointPoller = new SimulatorEndpointPoller();
        }

        endpointPoller.setTargetEndpoint(jmsAsyncInbountEndpoint(connectionFactory, jmsConfiguration));
        SimulatorEndpointAdapter endpointAdapter = simulatorEndpointAdapter();
        endpointAdapter.setApplicationContext(applicationContext);
        endpointAdapter.setMappingKeyExtractor(simulatorMappingKeyExtractor());
        endpointAdapter.setResponseEndpointAdapter(jmsReceiveEndpointAdapter(applicationContext, configuration));

        endpointPoller.setEndpointAdapter(endpointAdapter);

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

    /**
     * Gets the destination name to send messages to.
     *
     * @return
     */
    protected String getSendDestinationName(SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        if (configurer != null) {
            return configurer.sendDestinationName(simulatorJmsConfiguration);
        }

        return simulatorJmsConfiguration.getSendDestination();
    }
}
