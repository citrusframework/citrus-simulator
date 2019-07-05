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

package com.consol.citrus.simulator.jms;

import com.consol.citrus.endpoint.EndpointAdapter;
import com.consol.citrus.endpoint.adapter.EmptyResponseEndpointAdapter;
import com.consol.citrus.jms.endpoint.*;
import com.consol.citrus.simulator.SimulatorAutoConfiguration;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.endpoint.*;
import com.consol.citrus.simulator.scenario.mapper.ContentBasedXPathScenarioMapper;
import com.consol.citrus.simulator.scenario.mapper.ScenarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.util.StringUtils;

import javax.jms.ConnectionFactory;

@Configuration
@AutoConfigureAfter(SimulatorAutoConfiguration.class)
@EnableConfigurationProperties(SimulatorJmsConfigurationProperties.class)
@ConditionalOnProperty(prefix = "citrus.simulator.jms", value = "enabled", havingValue = "true")
public class SimulatorJmsAutoConfiguration {

    @Autowired(required = false)
    private SimulatorJmsConfigurer configurer;

    @Autowired
    private SimulatorJmsConfigurationProperties simulatorJmsConfiguration;

    @Autowired
    private SimulatorConfigurationProperties simulatorConfiguration;

    @Bean
    @ConditionalOnMissingBean
    public ConnectionFactory connectionFactory() {
        if (configurer != null) {
            return configurer.connectionFactory();
        }

        return new SingleConnectionFactory();
    }

    @Bean
    protected JmsEndpoint simulatorJmsInboundEndpoint(ConnectionFactory connectionFactory) {
        if (isSynchronous()) {
            JmsSyncEndpointConfiguration endpointConfiguration = new JmsSyncEndpointConfiguration();
            JmsSyncEndpoint jmsEndpoint = new JmsSyncEndpoint(endpointConfiguration);
            endpointConfiguration.setDestinationName(getInboundDestination());

            if (StringUtils.hasText(getReplyDestination())) {
                endpointConfiguration.setReplyDestinationName(getReplyDestination());
            }

            endpointConfiguration.setConnectionFactory(connectionFactory);

            return jmsEndpoint;
        } else {
            JmsEndpointConfiguration endpointConfiguration = new JmsEndpointConfiguration();
            JmsEndpoint jmsEndpoint = new JmsEndpoint(endpointConfiguration);
            endpointConfiguration.setDestinationName(getInboundDestination());
            endpointConfiguration.setConnectionFactory(connectionFactory);

            return jmsEndpoint;
        }
    }

    @Bean
    public SimulatorEndpointAdapter simulatorJmsEndpointAdapter() {
        return new SimulatorEndpointAdapter();
    }

    @Bean
    public ScenarioMapper simulatorJmsScenarioMapper() {
        if (configurer != null) {
            return configurer.scenarioMapper();
        }

        return new ContentBasedXPathScenarioMapper().addXPathExpression("local-name(/*)");
    }

    @Bean
    public SimulatorEndpointPoller simulatorJmsEndpointPoller(ApplicationContext applicationContext,
                                                  ConnectionFactory connectionFactory) {
        SimulatorEndpointPoller endpointPoller;

        if (useSoap()) {
            endpointPoller = new SimulatorSoapEndpointPoller();
        } else {
            endpointPoller = new SimulatorEndpointPoller();
        }

        endpointPoller.setInboundEndpoint(simulatorJmsInboundEndpoint(connectionFactory));

        SimulatorEndpointAdapter endpointAdapter = simulatorJmsEndpointAdapter();
        endpointAdapter.setApplicationContext(applicationContext);
        endpointAdapter.setMappingKeyExtractor(simulatorJmsScenarioMapper());
        endpointAdapter.setFallbackEndpointAdapter(simulatorJmsFallbackEndpointAdapter());

        if (!isSynchronous()) {
            endpointAdapter.setHandleResponse(false);
        }

        endpointPoller.setExceptionDelay(exceptionDelay(simulatorConfiguration));

        endpointPoller.setEndpointAdapter(endpointAdapter);

        return endpointPoller;
    }

    @Bean
    public EndpointAdapter simulatorJmsFallbackEndpointAdapter() {
        if (configurer != null) {
            return configurer.fallbackEndpointAdapter();
        }

        return new EmptyResponseEndpointAdapter();
    }

    /**
     * Gets the destination name to receive messages from.
     *
     * @return
     */
    protected String getInboundDestination() {
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
    protected String getReplyDestination() {
        if (configurer != null) {
            return configurer.replyDestination(simulatorJmsConfiguration);
        }

        return simulatorJmsConfiguration.getReplyDestination();
    }

    /**
     * Should the endpoint use synchronous reply communication.
     * @return
     */
    protected boolean isSynchronous() {
        if (configurer != null) {
            return configurer.synchronous(simulatorJmsConfiguration);
        }

        return simulatorJmsConfiguration.isSynchronous();
    }

    /**
     * Should the endpoint use SOAP envelope handling.
     * @return
     */
    protected boolean useSoap() {
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
