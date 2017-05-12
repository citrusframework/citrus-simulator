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
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.endpoint.EndpointAdapter;
import com.consol.citrus.endpoint.EndpointConfiguration;
import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import com.consol.citrus.jms.endpoint.JmsEndpointConfiguration;
import com.consol.citrus.message.Message;
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

import static com.consol.citrus.simulator.annotation.SimulatorJmsAsyncConfigurer.*;

/**
 * @author Martin Maher
 */
@Configuration
public class SimulatorJmsAsyncSupport {

    @Autowired(required = false)
    private SimulatorJmsAsyncConfigurer configurer;

    /**
     * Inbound JMS destination name
     */
    private static final String RECEIVE_DESTINATION_NAME =
            System.getProperty(RECEIVE_DESTINATION_NAME_KEY, RECEIVE_DESTINATION_VALUE_DEFAULT);

    /**
     * Outbound JMS destination name
     */
    private static final String SEND_DESTINATION_NAME =
            System.getProperty(SEND_DESTINATION_NAME_KEY, SEND_DESTINATION_VALUE_DEFAULT);

    @Bean(name = "simulator.jms.async.inbound")
    public MessageSelectingQueueChannel inboundChannel() {
        MessageSelectingQueueChannel inboundChannel = new MessageSelectingQueueChannel();
        return inboundChannel;
    }

    @Bean(name = "simulatorJmsAsyncReceiveEndpoint")
    public ChannelEndpoint jmsReceiveEndpoint() {
        ChannelEndpoint inboundChannelEndpoint = new ChannelEndpoint();
        inboundChannelEndpoint.getEndpointConfiguration().setUseObjectMessages(true);
        inboundChannelEndpoint.getEndpointConfiguration().setChannel(inboundChannel());
        return inboundChannelEndpoint;
    }

    @Bean(name = "simulatorJmsAsyncReceiveEndpointAdapter")
    public EndpointAdapter jmsReceiveEndpointAdapter(final ApplicationContext applicationContext) {

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

        return new JmsReceiveEndpointAdapter(TestContextFactory.newInstance(applicationContext), jmsReceiveEndpoint());
    }


    @Bean(name = "simulatorJmsAsyncInboundEndpoint")
    protected JmsEndpoint jmsAsyncInbountEndpoint() {
        JmsEndpointConfiguration endpointConfiguration = new JmsEndpointConfiguration();
        JmsEndpoint jmsEndpoint = new JmsEndpoint(endpointConfiguration);
        endpointConfiguration.setDestinationName(getReceiveDestinationName());
        endpointConfiguration.setConnectionFactory(connectionFactory());

        return jmsEndpoint;
    }

    @Bean(name = "simulatorJmsAsyncSendEndpoint")
    protected JmsEndpoint jmsSendEndpoint() {
        JmsEndpointConfiguration endpointConfiguration = new JmsEndpointConfiguration();
        JmsEndpoint jmsEndpoint = new JmsEndpoint(endpointConfiguration);
        endpointConfiguration.setDestinationName(getSendDestinationName());
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
    public SimulatorEndpointPoller endpointPoller(ApplicationContext applicationContext) {
        SimulatorEndpointPoller endpointPoller;

        if (configurer != null && configurer.useSoapEnvelope()) {
            endpointPoller = new SimulatorSoapEndpointPoller();
        } else {
            endpointPoller = new SimulatorEndpointPoller();
        }

        endpointPoller.setTargetEndpoint(jmsAsyncInbountEndpoint());
        SimulatorEndpointAdapter endpointAdapter = simulatorEndpointAdapter();
        endpointAdapter.setApplicationContext(applicationContext);
        endpointAdapter.setMappingKeyExtractor(simulatorMappingKeyExtractor());
        endpointAdapter.setResponseEndpointAdapter(jmsReceiveEndpointAdapter(applicationContext));

        endpointPoller.setEndpointAdapter(endpointAdapter);

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

    /**
     * Gets the destination name to send messages to.
     *
     * @return
     */
    protected String getSendDestinationName() {
        if (configurer != null) {
            return configurer.sendDestinationName();
        }

        return SEND_DESTINATION_NAME;
    }
}
