/*
 * Copyright 2006-2024 the original author or authors.
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

package org.citrusframework.simulator.endpoint;

import jakarta.annotation.Nullable;
import org.citrusframework.channel.ChannelSyncEndpoint;
import org.citrusframework.channel.ChannelSyncEndpointConfiguration;
import org.citrusframework.context.TestContextFactory;
import org.citrusframework.endpoint.Endpoint;
import org.citrusframework.endpoint.EndpointAdapter;
import org.citrusframework.endpoint.adapter.EmptyResponseEndpointAdapter;
import org.citrusframework.simulator.SimulatorAutoConfiguration;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.correlation.CorrelationHandlerRegistry;
import org.citrusframework.simulator.scenario.mapper.ContentBasedXPathScenarioMapper;
import org.citrusframework.simulator.scenario.mapper.ScenarioMapper;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.citrusframework.simulator.ws.SoapMessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Configuration
@AutoConfigureAfter(SimulatorAutoConfiguration.class)
@ConditionalOnProperty(prefix = "citrus.simulator.endpoint", value = "enabled", havingValue = "true")
public class SimulatorEndpointAutoConfiguration {

    private static final String SIMULATOR_ENDPOINT_ADAPTER_BEAN_NAME = "simulatorEndpointAdapter";

    private final ApplicationContext applicationContext;
    private final SimulatorConfigurationProperties simulatorConfiguration;

    private @Nullable SimulatorEndpointComponentConfigurer configurer;

    public SimulatorEndpointAutoConfiguration(ApplicationContext applicationContext, SimulatorConfigurationProperties simulatorConfiguration, @Autowired(required = false) @Nullable SimulatorEndpointComponentConfigurer configurer) {
        this.applicationContext = applicationContext;
        this.simulatorConfiguration = simulatorConfiguration;
        this.configurer = configurer;
    }


    @Bean
    protected Endpoint simulatorEndpoint() {
        if (configurer != null) {
            return configurer.endpoint(applicationContext);
        } else {
            ChannelSyncEndpointConfiguration endpointConfiguration = new ChannelSyncEndpointConfiguration();
            ChannelSyncEndpoint syncEndpoint = new ChannelSyncEndpoint(endpointConfiguration);
            endpointConfiguration.setChannelName("simulator.inbound.endpoint");

            return syncEndpoint;
        }
    }

    @Bean(SIMULATOR_ENDPOINT_ADAPTER_BEAN_NAME)
    public SimulatorEndpointAdapter simulatorEndpointAdapter(CorrelationHandlerRegistry handlerRegistry, ScenarioExecutorService scenarioExecutorService, SimulatorConfigurationProperties configuration) {
        return new SimulatorEndpointAdapter(applicationContext, handlerRegistry, scenarioExecutorService, configuration);
    }

    @Bean
    public ScenarioMapper simulatorScenarioMapper() {
        if (configurer != null) {
            return configurer.scenarioMapper();
        }

        return new ContentBasedXPathScenarioMapper().addXPathExpression("local-name(/*)");
    }

    @Bean
    public SimulatorEndpointPoller simulatorEndpointPoller(@Qualifier(SIMULATOR_ENDPOINT_ADAPTER_BEAN_NAME) SimulatorEndpointAdapter simulatorEndpointAdapter, TestContextFactory testContextFactory, @Autowired(required = false) @Nullable SoapMessageHelper soapMessageHelper) {
        SimulatorEndpointPoller endpointPoller;

        if (configurer != null && configurer.useSoapEnvelope()) {
            if (isNull(soapMessageHelper)) {
                throw new IllegalArgumentException("JMS support with SOAP requires a bean of %s".formatted(SoapMessageHelper.class));
            }

            endpointPoller = new SimulatorSoapEndpointPoller(testContextFactory, soapMessageHelper);
        } else {
            endpointPoller = new SimulatorEndpointPoller(testContextFactory);
        }

        endpointPoller.setInboundEndpoint(simulatorEndpoint());
        simulatorEndpointAdapter.setMappingKeyExtractor(simulatorScenarioMapper());
        simulatorEndpointAdapter.setFallbackEndpointAdapter(simulatorFallbackEndpointAdapter());

        endpointPoller.setExceptionDelay(exceptionDelay());

        endpointPoller.setEndpointAdapter(simulatorEndpointAdapter);

        return endpointPoller;
    }

    @Bean
    public EndpointAdapter simulatorFallbackEndpointAdapter() {
        if (nonNull(configurer)) {
            return configurer.fallbackEndpointAdapter();
        }

        return new EmptyResponseEndpointAdapter();
    }

    /**
     * Gets the endpoint polling exception delay.
     * @return
     */
    protected Long exceptionDelay() {
        if (nonNull(configurer)) {
            return configurer.exceptionDelay(simulatorConfiguration);
        }

        return simulatorConfiguration.getExceptionDelay();
    }
}
