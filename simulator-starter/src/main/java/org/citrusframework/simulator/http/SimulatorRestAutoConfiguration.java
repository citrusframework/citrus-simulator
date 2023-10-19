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

package org.citrusframework.simulator.http;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.citrusframework.endpoint.EndpointAdapter;
import org.citrusframework.endpoint.adapter.EmptyResponseEndpointAdapter;
import org.citrusframework.http.controller.HttpMessageController;
import org.citrusframework.http.interceptor.LoggingHandlerInterceptor;
import org.citrusframework.http.message.DelegatingHttpEntityMessageConverter;
import org.citrusframework.http.servlet.RequestCachingServletFilter;
import org.citrusframework.report.MessageListeners;
import org.citrusframework.simulator.SimulatorAutoConfiguration;
import org.citrusframework.simulator.endpoint.SimulatorEndpointAdapter;
import org.citrusframework.simulator.listener.SimulatorMessageListener;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.scenario.mapper.ScenarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Christoph Deppisch
 */
@Configuration
@ConditionalOnWebApplication
@AutoConfigureAfter(SimulatorAutoConfiguration.class)
@EnableConfigurationProperties(SimulatorRestConfigurationProperties.class)
@ConditionalOnProperty(prefix = "citrus.simulator.rest", value = "enabled", havingValue = "true", matchIfMissing = true)
public class SimulatorRestAutoConfiguration {

    @Autowired(required = false)
    private SimulatorRestConfigurer configurer;

    @Autowired
    private MessageListeners messageListeners;

    @Autowired
    private SimulatorMessageListener simulatorMessageListener;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Autowired
    private SimulatorRestConfigurationProperties simulatorRestConfiguration;

    /**
     * Target Citrus Http controller
     */
    private HttpMessageController restController;

    @Bean
    public FilterRegistrationBean<RequestCachingServletFilter> requestCachingFilter() {
        FilterRegistrationBean<RequestCachingServletFilter> filterRegistrationBean = new FilterRegistrationBean<>(new RequestCachingServletFilter());

        String urlMapping = getUrlMapping();
        if (urlMapping.endsWith("**")) {
            urlMapping = urlMapping.substring(0, urlMapping.length() - 1);
        }
        filterRegistrationBean.setUrlPatterns(Collections.singleton(urlMapping));

        return filterRegistrationBean;
    }

    @Bean
    public HandlerMapping simulatorRestHandlerMapping(ApplicationContext applicationContext) {
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        handlerMapping.setAlwaysUseFullPath(true);

        Map<String, Object> mappings = new HashMap<>();
        mappings.put(getUrlMapping(), createRestController(applicationContext));

        handlerMapping.setUrlMap(mappings);
        handlerMapping.setInterceptors(interceptors());

        return handlerMapping;
    }

    @Bean
    public HandlerAdapter simulatorRestHandlerAdapter(final ApplicationContext applicationContext) {
        final RequestMappingHandlerMapping handlerMapping = getRequestMappingHandlerMapping(applicationContext);

        requestMappingHandlerAdapter.getMessageConverters().add(0, new SimulatorHttpMessageConverter());
        requestMappingHandlerAdapter.getMessageConverters().add(new DelegatingHttpEntityMessageConverter());
        requestMappingHandlerAdapter.setCacheSeconds(0);

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

    private RequestMappingHandlerMapping getRequestMappingHandlerMapping(ApplicationContext applicationContext) {
        final RequestMappingHandlerMapping handlerMapping = new RequestMappingHandlerMapping() {
            @Override
            protected void initHandlerMethods() {
                detectHandlerMethods(createRestController(applicationContext));
                super.initHandlerMethods();
            }

            @Override
            protected boolean isHandler(Class<?> beanType) {
                return (beanType.isAssignableFrom(HttpMessageController.class)) && super.isHandler(beanType);
            }
        };

        handlerMapping.setApplicationContext(applicationContext);
        handlerMapping.afterPropertiesSet();
        return handlerMapping;
    }

    @Bean
    public SimulatorEndpointAdapter simulatorRestEndpointAdapter() {
        return new SimulatorEndpointAdapter();
    }

    @Bean
    public EndpointAdapter simulatorRestFallbackEndpointAdapter() {
        if (configurer != null) {
            return configurer.fallbackEndpointAdapter();
        }

        return new EmptyResponseEndpointAdapter();
    }

    /**
     * Gets the Citrus Http REST controller.
     *
     * @param applicationContext
     * @return
     */
    protected HttpMessageController createRestController(ApplicationContext applicationContext) {
        if (restController == null) {
            restController = new HttpMessageController();

            SimulatorEndpointAdapter endpointAdapter = simulatorRestEndpointAdapter();
            endpointAdapter.setApplicationContext(applicationContext);
            endpointAdapter.setMappingKeyExtractor(simulatorRestScenarioMapper());
            endpointAdapter.setFallbackEndpointAdapter(simulatorRestFallbackEndpointAdapter());

            restController.setEndpointAdapter(endpointAdapter);
        }

        return restController;
    }

    @Bean
    public ScenarioMapper simulatorRestScenarioMapper() {
        if (configurer != null) {
            return configurer.scenarioMapper();
        }

        return new HttpRequestAnnotationScenarioMapper();
    }

    @Bean
    protected HandlerInterceptor httpInterceptor() {
        messageListeners.addMessageListener(simulatorMessageListener);
        return new InterceptorHttp(messageListeners);
    }

    @Bean
    @ConditionalOnMissingBean(HttpScenarioGenerator.class)
    @ConditionalOnProperty(prefix = "citrus.simulator.rest.swagger", value = "enabled", havingValue = "true")
    public static HttpScenarioGenerator simulatorRestScenarioGenerator(Environment environment) {
        return new HttpScenarioGenerator(environment);
    }

    /**
     * Gets the url pattern to map this Http rest controller to. Clients must use this
     * context path in order to access the Http REST support on the simulator.
     *
     * @return
     */
    protected String getUrlMapping() {
        if (configurer != null) {
            return configurer.urlMapping(simulatorRestConfiguration);
        }

        return simulatorRestConfiguration.getUrlMapping();
    }

    /**
     * Provides list of endpoint interceptors.
     *
     * @return
     */
    protected HandlerInterceptor[] interceptors() {
        List<HandlerInterceptor> interceptors = new ArrayList<>();
        if (configurer != null) {
            Collections.addAll(interceptors, configurer.interceptors());
        }
        interceptors.add(new LoggingHandlerInterceptor());
        interceptors.add(httpInterceptor());
        return interceptors.toArray(new HandlerInterceptor[0]);
    }

}
