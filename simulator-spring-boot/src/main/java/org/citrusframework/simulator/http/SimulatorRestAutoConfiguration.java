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

package org.citrusframework.simulator.http;

import static java.util.Collections.addAll;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PROTECTED;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.citrusframework.endpoint.EndpointAdapter;
import org.citrusframework.endpoint.adapter.EmptyResponseEndpointAdapter;
import org.citrusframework.http.controller.HttpMessageController;
import org.citrusframework.http.interceptor.LoggingHandlerInterceptor;
import org.citrusframework.http.message.DelegatingHttpEntityMessageConverter;
import org.citrusframework.http.servlet.RequestCachingServletFilter;
import org.citrusframework.report.MessageListeners;
import org.citrusframework.simulator.SimulatorAutoConfiguration;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.correlation.CorrelationHandlerRegistry;
import org.citrusframework.simulator.endpoint.SimulatorEndpointAdapter;
import org.citrusframework.simulator.listener.SimulatorMessageListener;
import org.citrusframework.simulator.scenario.mapper.ScenarioMapper;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@ConditionalOnWebApplication
@AutoConfigureAfter(SimulatorAutoConfiguration.class)
@EnableConfigurationProperties(SimulatorRestConfigurationProperties.class)
@ConditionalOnProperty(prefix = "citrus.simulator.rest", value = "enabled", havingValue = "true", matchIfMissing = true)
public class SimulatorRestAutoConfiguration {

    public static final String REST_ENDPOINT_ADAPTER_BEAN_NAME = "simulatorRestEndpointAdapter";

    @Getter(PROTECTED)
    private final ApplicationContext applicationContext;

    @Getter(PROTECTED)
    private final SimulatorRestConfigurationProperties simulatorRestConfiguration;

    @Getter(PROTECTED)
    private final @Nullable SimulatorRestConfigurer restConfigurer;

    /**
     * Target Citrus Http controller
     */
    private HttpMessageController restController;

    public SimulatorRestAutoConfiguration(ApplicationContext applicationContext, SimulatorRestConfigurationProperties simulatorRestConfiguration, @Autowired(required = false) @Nullable SimulatorRestConfigurer restConfigurer) {
        this.applicationContext = applicationContext;
        this.simulatorRestConfiguration = simulatorRestConfiguration;
        this.restConfigurer = restConfigurer;
    }

    @Bean
    public FilterRegistrationBean<RequestCachingServletFilter> requestCachingFilter() {
        FilterRegistrationBean<RequestCachingServletFilter> filterRegistrationBean = new FilterRegistrationBean<>(new RequestCachingServletFilter());

        List<String> urlMappings = getUrlMappings().stream()
            .map(urlMapping -> {
                if (urlMapping.endsWith("**")) {
                    return urlMapping.substring(0, urlMapping.length() - 1);
                }
                return urlMapping;
            }).toList();

        filterRegistrationBean.setUrlPatterns(urlMappings);

        return filterRegistrationBean;
    }

    @Bean
    public HandlerMapping simulatorRestHandlerMapping(MessageListeners messageListeners, @Qualifier(REST_ENDPOINT_ADAPTER_BEAN_NAME) SimulatorEndpointAdapter simulatorRestEndpointAdapter, SimulatorMessageListener simulatorMessageListener) {
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        handlerMapping.setAlwaysUseFullPath(true);

        Map<String, Object> mappings = new HashMap<>();
        HttpMessageController controller = createRestController(simulatorRestEndpointAdapter);
        getUrlMappings().forEach(urlMapping -> mappings.put(urlMapping, controller));

        handlerMapping.setUrlMap(mappings);
        handlerMapping.setInterceptors((Object[]) interceptors(messageListeners, simulatorMessageListener));

        return handlerMapping;
    }

    @Bean
    public HandlerAdapter simulatorRestHandlerAdapter(RequestMappingHandlerAdapter requestMappingHandlerAdapter, @Qualifier(REST_ENDPOINT_ADAPTER_BEAN_NAME) SimulatorEndpointAdapter simulatorRestEndpointAdapter) {
        final RequestMappingHandlerMapping handlerMapping = getRequestMappingHandlerMapping(simulatorRestEndpointAdapter);

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

    private RequestMappingHandlerMapping getRequestMappingHandlerMapping(SimulatorEndpointAdapter simulatorRestEndpointAdapter) {
        final RequestMappingHandlerMapping handlerMapping = new RequestMappingHandlerMapping() {

            @Override
            protected void initHandlerMethods() {
                detectHandlerMethods(createRestController(simulatorRestEndpointAdapter));
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

    @Bean(name = REST_ENDPOINT_ADAPTER_BEAN_NAME)
    public SimulatorEndpointAdapter simulatorRestEndpointAdapter(CorrelationHandlerRegistry handlerRegistry, ScenarioExecutorService scenarioExecutorService, SimulatorConfigurationProperties configuration) {
        return new SimulatorEndpointAdapter(applicationContext, handlerRegistry, scenarioExecutorService, configuration);
    }

    @Bean
    public EndpointAdapter simulatorRestFallbackEndpointAdapter() {
        if (nonNull(restConfigurer)) {
            return restConfigurer.fallbackEndpointAdapter();
        }

        return new EmptyResponseEndpointAdapter();
    }

    /**
     * Gets the Citrus Http REST controller.
     */
    protected HttpMessageController createRestController(SimulatorEndpointAdapter simulatorRestEndpointAdapter) {
        if (isNull(restController)) {
            restController = new HttpMessageController();

            simulatorRestEndpointAdapter.setMappingKeyExtractor(simulatorRestScenarioMapper());
            simulatorRestEndpointAdapter.setFallbackEndpointAdapter(simulatorRestFallbackEndpointAdapter());

            restController.setEndpointAdapter(simulatorRestEndpointAdapter);
        }

        return restController;
    }

    @Bean
    public ScenarioMapper simulatorRestScenarioMapper() {
        if (nonNull(restConfigurer)) {
            return restConfigurer.scenarioMapper();
        }

        return new HttpRequestAnnotationScenarioMapper();
    }

    @Bean
    protected HandlerInterceptor httpInterceptor(MessageListeners messageListeners,
                                                 SimulatorMessageListener simulatorMessageListener) {
        messageListeners.addMessageListener(simulatorMessageListener);
        return new InterceptorHttp(messageListeners);
    }

    @Bean
    @ConditionalOnMissingBean(HttpScenarioGenerator.class)
    @ConditionalOnProperty(prefix = "citrus.simulator.rest.swagger", value = "enabled", havingValue = "true")
    public HttpScenarioGenerator simulatorRestScenarioGenerator() {
        return new HttpScenarioGenerator(simulatorRestConfiguration);
    }

    /**
     * Gets the url pattern to map this Http rest controller to. Clients must use this
     * context path in order to access the Http REST support on the simulator.
     */
    @NotNull
    protected List<String> getUrlMappings() {
        if (nonNull(restConfigurer)) {
            List<String> configuredUrls = restConfigurer.urlMappings(simulatorRestConfiguration);
            return configuredUrls != null ? configuredUrls : Collections.emptyList();
        }

        return simulatorRestConfiguration.getUrlMappings();
    }

    /**
     * Provides list of endpoint interceptors.
     */
    protected HandlerInterceptor[] interceptors(MessageListeners messageListeners, SimulatorMessageListener simulatorMessageListener) {
        List<HandlerInterceptor> interceptors = new ArrayList<>();
        if (nonNull(restConfigurer)) {
            addAll(interceptors, restConfigurer.interceptors());
        }
        interceptors.add(new LoggingHandlerInterceptor());
        interceptors.add(httpInterceptor(messageListeners, simulatorMessageListener));
        return interceptors.toArray(new HandlerInterceptor[0]);
    }
}
