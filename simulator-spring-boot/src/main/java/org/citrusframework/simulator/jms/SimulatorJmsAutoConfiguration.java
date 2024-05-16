/*
 * Copyright the original author or authors.
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

package org.citrusframework.simulator.jms;

import jakarta.annotation.Nullable;
import jakarta.jms.ConnectionFactory;
import lombok.Getter;
import org.citrusframework.context.TestContextFactory;
import org.citrusframework.endpoint.EndpointAdapter;
import org.citrusframework.endpoint.adapter.EmptyResponseEndpointAdapter;
import org.citrusframework.jms.endpoint.JmsEndpoint;
import org.citrusframework.jms.endpoint.JmsEndpointConfiguration;
import org.citrusframework.jms.endpoint.JmsSyncEndpoint;
import org.citrusframework.jms.endpoint.JmsSyncEndpointConfiguration;
import org.citrusframework.simulator.SimulatorAutoConfiguration;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.correlation.CorrelationHandlerRegistry;
import org.citrusframework.simulator.endpoint.SimulatorEndpointAdapter;
import org.citrusframework.simulator.endpoint.SimulatorEndpointPoller;
import org.citrusframework.simulator.endpoint.SimulatorSoapEndpointPoller;
import org.citrusframework.simulator.scenario.mapper.ContentBasedXPathScenarioMapper;
import org.citrusframework.simulator.scenario.mapper.ScenarioMapper;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.citrusframework.simulator.ws.SoapMessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.util.StringUtils;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PROTECTED;

@Configuration
@AutoConfigureAfter(SimulatorAutoConfiguration.class)
@EnableConfigurationProperties(SimulatorJmsConfigurationProperties.class)
@ConditionalOnProperty(prefix = "citrus.simulator.jms", value = "enabled", havingValue = "true")
public class SimulatorJmsAutoConfiguration {

    public static final String JMS_ENDPOINT_ADAPTER_BEAN_NAME = "simulatorJmsEndpointAdapter";

    @Getter(PROTECTED)
    private final SimulatorConfigurationProperties simulatorConfiguration;

    @Getter(PROTECTED)
    private final SimulatorJmsConfigurationProperties simulatorJmsConfiguration;

    @Getter(PROTECTED)
    private final @Nullable SimulatorJmsConfigurer jmsConfigurer;

    public SimulatorJmsAutoConfiguration(SimulatorConfigurationProperties simulatorConfiguration, SimulatorJmsConfigurationProperties simulatorJmsConfiguration, @Autowired(required = false) @Nullable SimulatorJmsConfigurer jmsConfigurer) {
        this.simulatorConfiguration = simulatorConfiguration;
        this.simulatorJmsConfiguration = simulatorJmsConfiguration;
        this.jmsConfigurer = jmsConfigurer;
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectionFactory connectionFactory() {
        if (nonNull(jmsConfigurer)) {
            return jmsConfigurer.connectionFactory();
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
            endpointConfiguration.setPubSubDomain(isPubSubDomain());

            return jmsEndpoint;
        }
    }

    @Bean(JMS_ENDPOINT_ADAPTER_BEAN_NAME)
    public SimulatorEndpointAdapter simulatorJmsEndpointAdapter(ApplicationContext applicationContext, CorrelationHandlerRegistry handlerRegistry, ScenarioExecutorService scenarioExecutorService) {
        return new SimulatorEndpointAdapter(applicationContext, handlerRegistry, scenarioExecutorService, getSimulatorConfiguration());
    }

    @Bean
    public ScenarioMapper simulatorJmsScenarioMapper() {
        if (nonNull(jmsConfigurer)) {
            return jmsConfigurer.scenarioMapper();
        }

        return new ContentBasedXPathScenarioMapper().addXPathExpression("local-name(/*)");
    }

    @Bean
    public SimulatorEndpointPoller simulatorJmsEndpointPoller(ConnectionFactory connectionFactory, @Qualifier(JMS_ENDPOINT_ADAPTER_BEAN_NAME) SimulatorEndpointAdapter simulatorJmsEndpointAdapter, TestContextFactory testContextFactory, @Autowired(required = false) @Nullable SoapMessageHelper soapMessageHelper) {
        SimulatorEndpointPoller endpointPoller;

        if (useSoap()) {
            if (isNull(soapMessageHelper)) {
                throw new IllegalArgumentException("JMS support with SOAP requires a bean of %s".formatted(SoapMessageHelper.class));
            }

            endpointPoller = new SimulatorSoapEndpointPoller(testContextFactory, soapMessageHelper);
        } else {
            endpointPoller = new SimulatorEndpointPoller(testContextFactory);
        }

        endpointPoller.setInboundEndpoint(simulatorJmsInboundEndpoint(connectionFactory));

        simulatorJmsEndpointAdapter.setMappingKeyExtractor(simulatorJmsScenarioMapper());
        simulatorJmsEndpointAdapter.setFallbackEndpointAdapter(simulatorJmsFallbackEndpointAdapter());

        if (!isSynchronous()) {
            simulatorJmsEndpointAdapter.setHandleResponse(false);
        }

        endpointPoller.setExceptionDelay(exceptionDelay(simulatorConfiguration));

        endpointPoller.setEndpointAdapter(simulatorJmsEndpointAdapter);

        return endpointPoller;
    }

    @Bean
    public EndpointAdapter simulatorJmsFallbackEndpointAdapter() {
        if (nonNull(jmsConfigurer)) {
            return jmsConfigurer.fallbackEndpointAdapter();
        }

        return new EmptyResponseEndpointAdapter();
    }

    /**
     * Gets the destination name to receive messages from.
     */
    protected String getInboundDestination() {
        if (nonNull(jmsConfigurer)) {
            return jmsConfigurer.inboundDestination(simulatorJmsConfiguration);
        }

        return simulatorJmsConfiguration.getInboundDestination();
    }

    /**
     * Gets the destination name to send messages to.
     */
    protected String getReplyDestination() {
        if (nonNull(jmsConfigurer)) {
            return jmsConfigurer.replyDestination(simulatorJmsConfiguration);
        }

        return simulatorJmsConfiguration.getReplyDestination();
    }

    /**
     * Should the endpoint use synchronous reply communication.
     */
    protected boolean isSynchronous() {
        if (nonNull(jmsConfigurer)) {
            return jmsConfigurer.synchronous(simulatorJmsConfiguration);
        }

        return simulatorJmsConfiguration.isSynchronous();
    }

    /**
     * Should the endpoint use SOAP envelope handling.
     */
    protected boolean useSoap() {
        if (nonNull(jmsConfigurer)) {
            return jmsConfigurer.useSoap(simulatorJmsConfiguration);
        }

        return simulatorJmsConfiguration.isUseSoap();
    }

    /**
     * Should the endpoint use pub-sub domain.
     */
    protected boolean isPubSubDomain() {
        if (nonNull(jmsConfigurer)) {
            return jmsConfigurer.pubSubDomain(simulatorJmsConfiguration);
        }

        return simulatorJmsConfiguration.isPubSubDomain();
    }

    /**
     * Gets the endpoint polling exception delay.
     */
    protected Long exceptionDelay(SimulatorConfigurationProperties simulatorConfiguration) {
        if (nonNull(jmsConfigurer)) {
            return jmsConfigurer.exceptionDelay(simulatorConfiguration);
        }

        return simulatorConfiguration.getExceptionDelay();
    }
}
