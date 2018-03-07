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

package com.consol.citrus.simulator.file;

import com.consol.citrus.channel.ChannelEndpoint;
import com.consol.citrus.channel.ChannelEndpointConfiguration;
import com.consol.citrus.endpoint.EndpointAdapter;
import com.consol.citrus.endpoint.adapter.EmptyResponseEndpointAdapter;
import com.consol.citrus.simulator.SimulatorAutoConfiguration;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.config.SimulatorConfigurer;
import com.consol.citrus.simulator.endpoint.SimulatorEndpointAdapter;
import com.consol.citrus.simulator.endpoint.SimulatorEndpointPoller;
import com.consol.citrus.simulator.scenario.mapper.ContentBasedXPathScenarioMapper;
import com.consol.citrus.simulator.scenario.mapper.ScenarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(SimulatorAutoConfiguration.class)
@EnableConfigurationProperties(SimulatorFileConfigurationProperties.class)
@ConditionalOnProperty(prefix = "citrus.simulator.file", value = "enabled", havingValue = "true")
public class SimulatorFileAutoConfiguration {

    private final SimulatorConfigurer configurer;

    private final SimulatorFileConfigurationProperties simulatorFileConfiguration;

    private final SimulatorConfigurationProperties simulatorConfiguration;

    @Autowired
    public SimulatorFileAutoConfiguration(SimulatorConfigurer configurer,
                                          SimulatorFileConfigurationProperties simulatorFileConfiguration,
                                          SimulatorConfigurationProperties simulatorConfiguration) {
        this.configurer = configurer;
        this.simulatorFileConfiguration = simulatorFileConfiguration;
        this.simulatorConfiguration = simulatorConfiguration;
    }

    @Bean(name = "simulatorFileInboundEndpoint")
    protected ChannelEndpoint fileInboundEndpoint() {
        ChannelEndpointConfiguration channelEndpointConfiguration = new ChannelEndpointConfiguration();
        ChannelEndpoint channelEndpoint = new ChannelEndpoint(channelEndpointConfiguration);
        channelEndpointConfiguration.setChannelName(simulatorFileConfiguration.getInboundChannel());

        return channelEndpoint;
    }

    @Bean(name = "simulatorFileEndpointAdapter")
    public SimulatorEndpointAdapter simulatorEndpointAdapter() {

        return new SimulatorEndpointAdapter();
    }

    @Bean(name = "simulatorFileScenarioMapper")
    public ScenarioMapper simulatorScenarioMapper() {
        if (configurer != null) {
            return configurer.scenarioMapper();
        }

        return new ContentBasedXPathScenarioMapper().addXPathExpression("local-name(/*)");
    }

    @Bean(name = "simulatorFileEndpointPoller")
    public SimulatorEndpointPoller endpointPoller(ApplicationContext applicationContext) {
        SimulatorEndpointPoller endpointPoller = new SimulatorEndpointPoller();

        endpointPoller.setInboundEndpoint(fileInboundEndpoint());

        SimulatorEndpointAdapter endpointAdapter = simulatorEndpointAdapter();
        endpointAdapter.setApplicationContext(applicationContext);
        endpointAdapter.setMappingKeyExtractor(simulatorScenarioMapper());
        endpointAdapter.setFallbackEndpointAdapter(simulatorFallbackEndpointAdapter());

        endpointPoller.setExceptionDelay(exceptionDelay(simulatorConfiguration));
        endpointPoller.setEndpointAdapter(endpointAdapter);

        return endpointPoller;
    }

    @Bean(name = "simulatorFileFallbackEndpointAdapter")
    public EndpointAdapter simulatorFallbackEndpointAdapter() {
        if (configurer != null) {
            return configurer.fallbackEndpointAdapter();
        }

        return new EmptyResponseEndpointAdapter();
    }

    /**
     * Gets the endpoint polling exception delay.
     *
     * @param simulatorConfiguration simulator configuration properties
     * @return endpoint polling exception delay
     */
    protected Long exceptionDelay(SimulatorConfigurationProperties simulatorConfiguration) {
        if (configurer != null) {
            return configurer.exceptionDelay(simulatorConfiguration);
        }

        return simulatorConfiguration.getExceptionDelay();
    }
}
