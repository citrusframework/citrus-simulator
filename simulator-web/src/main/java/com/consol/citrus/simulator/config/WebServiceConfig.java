package com.consol.citrus.simulator.config;

import com.consol.citrus.channel.*;
import com.consol.citrus.dsl.endpoint.TestExecutingEndpointAdapter;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.ws.interceptor.LoggingEndpointInterceptor;
import com.consol.citrus.ws.server.WebServiceEndpoint;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.ws.server.*;
import org.springframework.ws.server.endpoint.mapping.UriEndpointMapping;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

import java.util.Properties;

/**
 * @author Christoph Deppisch
 */
@Configuration
public class WebServiceConfig {

    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, "/ws/*");
    }

    @Bean(name = "simulatorEndpointMapping")
    public EndpointMapping endpointMapping(ApplicationContext applicationContext) {
        UriEndpointMapping endpointMapping = new UriEndpointMapping();
        endpointMapping.setOrder(Ordered.HIGHEST_PRECEDENCE);

        WebServiceEndpoint webServiceEndpoint = new WebServiceEndpoint();
        TestExecutingEndpointAdapter endpointAdapter = new TestExecutingEndpointAdapter();
        endpointAdapter.setApplicationContext(applicationContext);
        endpointAdapter.setMappingKeyExtractor(new XPathPayloadMappingKeyExtractor());
        webServiceEndpoint.setEndpointAdapter(endpointAdapter);

        endpointAdapter.setResponseEndpointAdapter(inboundEndpointAdapter());

        endpointMapping.setDefaultEndpoint(webServiceEndpoint);
        endpointMapping.setInterceptors(new EndpointInterceptor[] { new LoggingEndpointInterceptor() });

        Properties mappings = new Properties();
        mappings.put("/ws/simulator", webServiceEndpoint);
        endpointMapping.setMappings(mappings);
        endpointMapping.setUsePath(true);

        return endpointMapping;
    }

    @Bean(name = "simulator.inbound")
    public MessageSelectingQueueChannel inboundChannel() {
        MessageSelectingQueueChannel inboundChannel = new MessageSelectingQueueChannel();
        return inboundChannel;
    }

    @Bean(name = "simInboundEndpoint")
    public ChannelEndpoint inboundChannelEndpoint() {
        ChannelSyncEndpoint inboundChannelEndpoint = new ChannelSyncEndpoint();
        inboundChannelEndpoint.getEndpointConfiguration().setUseObjectMessages(true);
        inboundChannelEndpoint.getEndpointConfiguration().setChannelName("simulator.inbound");
        return inboundChannelEndpoint;
    }

    @Bean(name = "simulatorInboundAdapter")
    public ChannelEndpointAdapter inboundEndpointAdapter() {
        ChannelSyncEndpointConfiguration endpointConfiguration = new ChannelSyncEndpointConfiguration();
        endpointConfiguration.setChannelName("simulator.inbound");
        return new ChannelEndpointAdapter(endpointConfiguration);
    }
}
