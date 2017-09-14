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

package com.consol.citrus.simulator.endpoint;

import com.consol.citrus.channel.ChannelSyncEndpoint;
import com.consol.citrus.channel.ChannelSyncEndpointConfiguration;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.endpoint.EndpointAdapter;
import com.consol.citrus.endpoint.adapter.EmptyResponseEndpointAdapter;
import com.consol.citrus.simulator.SimulatorAutoConfiguration;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.scenario.mapper.ContentBasedXPathScenarioMapper;
import com.consol.citrus.simulator.scenario.mapper.ScenarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Christoph Deppisch
 */
@Configuration
@AutoConfigureAfter(SimulatorAutoConfiguration.class)
@ConditionalOnProperty(prefix = "citrus.simulator.endpoint", value = "enabled", havingValue = "true")
public class SimulatorEndpointAutoConfiguration {

    @Autowired(required = false)
    private SimulatorEndpointComponentConfigurer configurer;

    @Autowired
    private SimulatorConfigurationProperties simulatorConfiguration;

    @Bean(name = "simulatorEndpoint")
    protected Endpoint simulatorEndpoint(ApplicationContext applicationContext) {
        if (configurer != null) {
            return configurer.endpoint(applicationContext);
        } else {
            ChannelSyncEndpointConfiguration endpointConfiguration = new ChannelSyncEndpointConfiguration();
            ChannelSyncEndpoint syncEndpoint = new ChannelSyncEndpoint(endpointConfiguration);
            endpointConfiguration.setChannelName("simulator.inbound.endpoint");

            return syncEndpoint;
        }
    }

    @Bean(name = "simulatorEndpointAdapter")
    public SimulatorEndpointAdapter simulatorEndpointAdapter() {
        return new SimulatorEndpointAdapter();
    }

    @Bean(name = "simulatorScenarioMapper")
    public ScenarioMapper simulatorScenarioMapper() {
        if (configurer != null) {
            return configurer.scenarioMapper();
        }

        return new ContentBasedXPathScenarioMapper().addXPathExpression("local-name(/*)");
    }

    @Bean(name = "simulatorEndpointPoller")
    public SimulatorEndpointPoller endpointPoller(ApplicationContext applicationContext) {
        SimulatorEndpointPoller endpointPoller;

        if (configurer != null && configurer.useSoapEnvelope()) {
            endpointPoller = new SimulatorSoapEndpointPoller();
        } else {
            endpointPoller = new SimulatorEndpointPoller();
        }

        endpointPoller.setInboundEndpoint(simulatorEndpoint(applicationContext));
        SimulatorEndpointAdapter endpointAdapter = simulatorEndpointAdapter();
        endpointAdapter.setApplicationContext(applicationContext);
        endpointAdapter.setMappingKeyExtractor(simulatorScenarioMapper());
        endpointAdapter.setFallbackEndpointAdapter(simulatorFallbackEndpointAdapter());

        endpointPoller.setExceptionDelay(exceptionDelay());

        endpointPoller.setEndpointAdapter(endpointAdapter);

        return endpointPoller;
    }

    @Bean(name = "simulatorFallbackEndpointAdapter")
    public EndpointAdapter simulatorFallbackEndpointAdapter() {
        if (configurer != null) {
            return configurer.fallbackEndpointAdapter();
        }

        return new EmptyResponseEndpointAdapter();
    }

    /**
     * Gets the endpoint polling exception delay.
     * @return
     */
    protected Long exceptionDelay() {
        if (configurer != null) {
            return configurer.exceptionDelay(simulatorConfiguration);
        }

        return simulatorConfiguration.getExceptionDelay();
    }
}
