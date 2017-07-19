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

import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.jms.endpoint.*;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.endpoint.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.util.StringUtils;

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

    @Bean(name = "simulatorJmsInboundEndpoint")
    protected JmsEndpoint jmsInboundEndpoint(ConnectionFactory connectionFactory,
                                      SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        if (isSynchronous(simulatorJmsConfiguration)) {
            JmsSyncEndpointConfiguration endpointConfiguration = new JmsSyncEndpointConfiguration();
            JmsSyncEndpoint jmsEndpoint = new JmsSyncEndpoint(endpointConfiguration);
            endpointConfiguration.setDestinationName(getInboundDestination(simulatorJmsConfiguration));

            if (StringUtils.hasText(getReplyDestination(simulatorJmsConfiguration))) {
                endpointConfiguration.setReplyDestinationName(getReplyDestination(simulatorJmsConfiguration));
            }

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

    @Bean(name = "simulatorJmsEndpointAdapter")
    public SimulatorEndpointAdapter simulatorEndpointAdapter() {
        return new SimulatorEndpointAdapter();
    }

    @Bean(name = "simulatorJmsMappingKeyExtractor")
    public MappingKeyExtractor simulatorMappingKeyExtractor() {
        if (configurer != null) {
            return configurer.mappingKeyExtractor();
        }

        return new XPathPayloadMappingKeyExtractor();
    }

    @Bean(name = "simulatorJmsEndpointPoller")
    public SimulatorEndpointPoller endpointPoller(ApplicationContext applicationContext,
                                                  ConnectionFactory connectionFactory,
                                                  SimulatorConfigurationProperties simulatorConfiguration,
                                                  SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        SimulatorEndpointPoller endpointPoller;

        if (useSoap(simulatorJmsConfiguration)) {
            endpointPoller = new SimulatorSoapEndpointPoller();
        } else {
            endpointPoller = new SimulatorEndpointPoller();
        }

        endpointPoller.setInboundEndpoint(jmsInboundEndpoint(connectionFactory, simulatorJmsConfiguration));

        SimulatorEndpointAdapter endpointAdapter = simulatorEndpointAdapter();
        endpointAdapter.setApplicationContext(applicationContext);
        endpointAdapter.setMappingKeyExtractor(simulatorMappingKeyExtractor());

        if (!isSynchronous(simulatorJmsConfiguration)) {
            endpointAdapter.setHandleResponse(false);
        }

        endpointPoller.setExceptionDelay(exceptionDelay(simulatorConfiguration));

        endpointPoller.setEndpointAdapter(endpointAdapter);

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
    protected String getReplyDestination(SimulatorJmsConfigurationProperties simulatorJmsConfiguration) {
        if (configurer != null) {
            return configurer.replyDestination(simulatorJmsConfiguration);
        }

        return simulatorJmsConfiguration.getReplyDestination();
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

    /**
     * Gets the endpoint polling exception delay.
     * @param simulatorConfiguration
     * @return
     */
    protected Long exceptionDelay(SimulatorConfigurationProperties simulatorConfiguration) {
        if (configurer != null) {
            return configurer.exceptionDelay(simulatorConfiguration);
        }

        return simulatorConfiguration.getExceptionDelay();
    }
}
