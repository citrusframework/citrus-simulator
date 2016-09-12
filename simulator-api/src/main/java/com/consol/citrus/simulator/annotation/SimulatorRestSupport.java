package com.consol.citrus.simulator.annotation;

import com.consol.citrus.channel.*;
import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.http.controller.HttpMessageController;
import com.consol.citrus.http.interceptor.LoggingHandlerInterceptor;
import com.consol.citrus.http.servlet.RequestCachingServletFilter;
import com.consol.citrus.simulator.endpoint.SimulatorEndpointAdapter;
import com.consol.citrus.simulator.endpoint.SimulatorMappingKeyExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author Christoph Deppisch
 */
@Configuration
public class SimulatorRestSupport {

    @Autowired(required = false)
    private SimulatorRestConfigurer configurer;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    /** Target Citrus Http controller */
    private HttpMessageController restController;

    @Bean
    public FilterRegistrationBean requestCachingFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new RequestCachingServletFilter());
        filterRegistrationBean.setUrlPatterns(Collections.singleton(getUrlMapping()));
        return filterRegistrationBean;
    }

    @Bean(name = "simulatorRestHandlerMapping")
    public HandlerMapping handlerMapping(ApplicationContext applicationContext) {
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        handlerMapping.setAlwaysUseFullPath(true);

        Map<String, Object> mappings = new HashMap<>();
        mappings.put(getUrlMapping(), getRestController(applicationContext));

        handlerMapping.setUrlMap(mappings);
        handlerMapping.setInterceptors(interceptors());

        return handlerMapping;
    }

    @Bean(name = "simulatorRestHandlerAdapter")
    public HandlerAdapter handlerAdapter(final ApplicationContext applicationContext) {
        final RequestMappingHandlerMapping handlerMapping = new RequestMappingHandlerMapping() {
            @Override
            protected void initHandlerMethods() {
                detectHandlerMethods(getRestController(applicationContext));
                super.initHandlerMethods();
            }

            @Override
            protected boolean isHandler(Class<?> beanType) {
                return (beanType.isAssignableFrom(HttpMessageController.class)) && super.isHandler(beanType);
            }
        };

        handlerMapping.setApplicationContext(applicationContext);
        handlerMapping.afterPropertiesSet();

        return new SimpleControllerHandlerAdapter() {
            @Override
            public boolean supports(Object handler) {
                return handler instanceof HttpMessageController;
            }

            @Override
            public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                return requestMappingHandlerAdapter.handle(request, response, handlerMapping.getHandler(request).getHandler());
            }
        };
    }

    @Bean(name = "simulator.rest.inbound")
    public MessageSelectingQueueChannel inboundChannel() {
        MessageSelectingQueueChannel inboundChannel = new MessageSelectingQueueChannel();
        return inboundChannel;
    }

    @Bean(name = "simulatorRestInboundEndpoint")
    public ChannelEndpoint inboundChannelEndpoint() {
        ChannelSyncEndpoint inboundChannelEndpoint = new ChannelSyncEndpoint();
        inboundChannelEndpoint.getEndpointConfiguration().setUseObjectMessages(true);
        inboundChannelEndpoint.getEndpointConfiguration().setChannel(inboundChannel());
        return inboundChannelEndpoint;
    }

    @Bean(name = "simulatorRestInboundAdapter")
    public ChannelEndpointAdapter inboundEndpointAdapter(ApplicationContext applicationContext) {
        ChannelSyncEndpointConfiguration endpointConfiguration = new ChannelSyncEndpointConfiguration();
        endpointConfiguration.setChannel(inboundChannel());
        ChannelEndpointAdapter endpointAdapter = new ChannelEndpointAdapter(endpointConfiguration);
        endpointAdapter.setApplicationContext(applicationContext);

        return endpointAdapter;
    }

    /**
     * Gets the Citrus Http REST controller.
     * @param applicationContext
     * @return
     */
    protected HttpMessageController getRestController(ApplicationContext applicationContext) {
        if (restController == null) {
            restController = new HttpMessageController();

            SimulatorEndpointAdapter endpointAdapter = simulatorEndpointAdapter();
            endpointAdapter.setApplicationContext(applicationContext);
            endpointAdapter.setMappingKeyExtractor(simulatorMappingKeyExtractor());

            restController.setEndpointAdapter(endpointAdapter);

            endpointAdapter.setResponseEndpointAdapter(inboundEndpointAdapter(applicationContext));
        }

        return restController;
    }

    @Bean
    public SimulatorEndpointAdapter simulatorEndpointAdapter() {
        return new SimulatorEndpointAdapter();
    }

    @Bean
    public MappingKeyExtractor simulatorMappingKeyExtractor() {
        SimulatorMappingKeyExtractor simulatorMappingKeyExtractor = new SimulatorMappingKeyExtractor();
        simulatorMappingKeyExtractor.setDelegate(getMappingKeyExtractor());
        return simulatorMappingKeyExtractor;
    }

    /**
     * Gets the url pattern to map this Http rest controller to. Clients must use this
     * context path in order to access the Http REST support on the simulator.
     * @return
     */
    protected String getUrlMapping() {
        if (configurer != null) {
            return configurer.urlMapping();
        }

        return "/services/rest/*";
    }

    /**
     * Provides list of endpoint interceptors.
     * @return
     */
    protected HandlerInterceptor[] interceptors() {
        if (configurer != null) {
            return configurer.interceptors();
        }

        return new HandlerInterceptor[] { new LoggingHandlerInterceptor() };
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
}
