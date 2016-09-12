/*
 * Copyright 2006-2016 the original author or authors.
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
import com.consol.citrus.simulator.endpoint.SimulatorEndpointAdapter;
import com.consol.citrus.ws.interceptor.LoggingEndpointInterceptor;
import com.consol.citrus.ws.server.WebServiceEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.EndpointMapping;
import org.springframework.ws.server.endpoint.MessageEndpoint;
import org.springframework.ws.server.endpoint.mapping.UriEndpointMapping;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

/**
 * @author Christoph Deppisch
 */
@Configuration
public class SimulatorWebServiceSupport {

    @Autowired(required = false)
    private SimulatorWebServiceConfigurer configurer;

    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, getDispatcherServletMapping());
    }

    @Bean(name = "simulatorWsEndpointMapping")
    public EndpointMapping endpointMapping(ApplicationContext applicationContext) {
        UriEndpointMapping endpointMapping = new UriEndpointMapping();
        endpointMapping.setOrder(Ordered.HIGHEST_PRECEDENCE);

        endpointMapping.setDefaultEndpoint(webServiceEndpoint(applicationContext));
        endpointMapping.setInterceptors(interceptors());

        return endpointMapping;
    }

    @Bean(name = "simulatorWsEndpoint")
    public MessageEndpoint webServiceEndpoint(ApplicationContext applicationContext) {
        WebServiceEndpoint webServiceEndpoint = new WebServiceEndpoint();
        SimulatorEndpointAdapter endpointAdapter = simulatorEndpointAdapter();
        endpointAdapter.setApplicationContext(applicationContext);
        endpointAdapter.setMappingKeyExtractor(getMappingKeyExtractor());
        webServiceEndpoint.setEndpointAdapter(endpointAdapter);

        endpointAdapter.setResponseEndpointAdapter(inboundEndpointAdapter(applicationContext));

        return webServiceEndpoint;
    }

    @Bean(name = "simulator.ws.inbound")
    public MessageSelectingQueueChannel inboundChannel() {
        MessageSelectingQueueChannel inboundChannel = new MessageSelectingQueueChannel();
        return inboundChannel;
    }

    @Bean(name = "simulatorWsInboundEndpoint")
    public ChannelEndpoint inboundChannelEndpoint() {
        ChannelSyncEndpoint inboundChannelEndpoint = new ChannelSyncEndpoint();
        inboundChannelEndpoint.getEndpointConfiguration().setUseObjectMessages(true);
        inboundChannelEndpoint.getEndpointConfiguration().setChannel(inboundChannel());
        return inboundChannelEndpoint;
    }

    @Bean(name = "simulatorWsInboundAdapter")
    public ChannelEndpointAdapter inboundEndpointAdapter(ApplicationContext applicationContext) {
        ChannelSyncEndpointConfiguration endpointConfiguration = new ChannelSyncEndpointConfiguration();
        endpointConfiguration.setChannel(inboundChannel());
        ChannelEndpointAdapter endpointAdapter = new ChannelEndpointAdapter(endpointConfiguration);
        endpointAdapter.setApplicationContext(applicationContext);

        return endpointAdapter;
    }

    @Bean
    protected SimulatorEndpointAdapter simulatorEndpointAdapter() {
        return new SimulatorEndpointAdapter();
    }

    /**
     * Gets the web service message dispatcher servlet mapping. Clients must use this
     * context path in order to access the web service support on the simulator.
     * @return
     */
    protected String getDispatcherServletMapping() {
        if (configurer != null) {
            return configurer.servletMapping();
        }

        return "/services/ws/*";
    }

    /**
     * Gets the mapping key extractor.
     * @return
     */
    protected MappingKeyExtractor getMappingKeyExtractor() {
        if (configurer != null) {
            return configurer.mappingKeyExtractor();
        }

        return new XPathPayloadMappingKeyExtractor();
    }

    /**
     * Provides list of endpoint interceptors.
     * @return
     */
    protected EndpointInterceptor[] interceptors() {
        if (configurer != null) {
            return configurer.interceptors();
        }

        return new EndpointInterceptor[] { new LoggingEndpointInterceptor() };
    }
}
