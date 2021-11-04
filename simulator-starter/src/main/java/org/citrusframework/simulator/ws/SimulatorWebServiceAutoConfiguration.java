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

package org.citrusframework.simulator.ws;

import com.consol.citrus.endpoint.EndpointAdapter;
import com.consol.citrus.endpoint.adapter.EmptyResponseEndpointAdapter;
import org.citrusframework.simulator.SimulatorAutoConfiguration;
import org.citrusframework.simulator.endpoint.SimulatorEndpointAdapter;
import org.citrusframework.simulator.scenario.mapper.ContentBasedXPathScenarioMapper;
import org.citrusframework.simulator.scenario.mapper.ScenarioMapper;
import com.consol.citrus.ws.interceptor.LoggingEndpointInterceptor;
import com.consol.citrus.ws.server.WebServiceEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.EndpointMapping;
import org.springframework.ws.server.endpoint.MessageEndpoint;
import org.springframework.ws.server.endpoint.adapter.MessageEndpointAdapter;
import org.springframework.ws.server.endpoint.mapping.UriEndpointMapping;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

import java.util.*;

/**
 * @author Christoph Deppisch
 */
@Configuration
@AutoConfigureAfter(SimulatorAutoConfiguration.class)
@Import(SimulatorWebServiceLoggingAutoConfiguration.class)
@EnableConfigurationProperties(SimulatorWebServiceConfigurationProperties.class)
@ConditionalOnProperty(prefix = "citrus.simulator.ws", value = "enabled", havingValue = "true")
@ConditionalOnWebApplication
public class SimulatorWebServiceAutoConfiguration {

    @Autowired(required = false)
    private SimulatorWebServiceConfigurer configurer;

    @Autowired
    private LoggingEndpointInterceptor loggingEndpointInterceptor;

    @Autowired
    private SimulatorWebServiceConfigurationProperties simulatorWebServiceConfiguration;

    @Bean
    public MessageEndpointAdapter messageEndpointAdapter() {
        return new MessageEndpointAdapter();
    }

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> simulatorServletRegistrationBean(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, getServletMapping());
    }

    @Bean
    public EndpointMapping simulatorWsEndpointMapping(ApplicationContext applicationContext) {
        UriEndpointMapping endpointMapping = new UriEndpointMapping();
        endpointMapping.setOrder(Ordered.HIGHEST_PRECEDENCE);

        endpointMapping.setDefaultEndpoint(simulatorWsEndpoint(applicationContext));
        endpointMapping.setInterceptors(interceptors());

        return endpointMapping;
    }

    @Bean
    public MessageEndpoint simulatorWsEndpoint(ApplicationContext applicationContext) {
        WebServiceEndpoint webServiceEndpoint = new WebServiceEndpoint();
        SimulatorEndpointAdapter endpointAdapter = simulatorWsEndpointAdapter();
        endpointAdapter.setApplicationContext(applicationContext);
        endpointAdapter.setMappingKeyExtractor(simulatorWsScenarioMapper());
        endpointAdapter.setFallbackEndpointAdapter(simulatorWsFallbackEndpointAdapter());

        webServiceEndpoint.setEndpointAdapter(endpointAdapter);

        return webServiceEndpoint;
    }

    @Bean
    public SimulatorEndpointAdapter simulatorWsEndpointAdapter() {
        return new SimulatorEndpointAdapter();
    }

    @Bean
    public ScenarioMapper simulatorWsScenarioMapper() {
        if (configurer != null) {
            return configurer.scenarioMapper();
        }

        return new ContentBasedXPathScenarioMapper().addXPathExpression("local-name(/*)");
    }

    @Bean
    public EndpointAdapter simulatorWsFallbackEndpointAdapter() {
        if (configurer != null) {
            return configurer.fallbackEndpointAdapter();
        }

        return new EmptyResponseEndpointAdapter();
    }

    @Bean
    @ConditionalOnMissingBean(WsdlScenarioGenerator.class)
    @ConditionalOnProperty(prefix = "citrus.simulator.ws.wsdl", value = "enabled", havingValue = "true")
    public static WsdlScenarioGenerator simulatorWsdlScenarioGenerator(Environment environment) {
        return new WsdlScenarioGenerator(environment);
    }

    /**
     * Gets the web service message dispatcher servlet mapping. Clients must use this
     * context path in order to access the web service support on the simulator.
     *
     * @return
     */
    protected String getServletMapping() {
        if (configurer != null) {
            return configurer.servletMapping(simulatorWebServiceConfiguration);
        }

        return simulatorWebServiceConfiguration.getServletMapping();
    }

    /**
     * Provides list of endpoint interceptors.
     *
     * @return
     */
    protected EndpointInterceptor[] interceptors() {
        List<EndpointInterceptor> interceptors = new ArrayList<>();
        if (configurer != null) {
            Collections.addAll(interceptors, configurer.interceptors());
        }
        interceptors.add(loggingEndpointInterceptor);
        return interceptors.toArray(new EndpointInterceptor[interceptors.size()]);
    }
}
