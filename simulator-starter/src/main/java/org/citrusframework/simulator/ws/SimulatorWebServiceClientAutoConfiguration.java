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

import org.citrusframework.message.ErrorHandlingStrategy;
import org.citrusframework.simulator.SimulatorAutoConfiguration;
import org.citrusframework.ws.client.WebServiceClient;
import org.citrusframework.ws.interceptor.LoggingClientInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Martin Maher
 */
@Configuration
@AutoConfigureAfter(SimulatorAutoConfiguration.class)
@Import(SimulatorWebServiceLoggingAutoConfiguration.class)
@EnableConfigurationProperties(SimulatorWebServiceClientConfigurationProperties.class)
@ConditionalOnProperty(prefix = "citrus.simulator.ws.client", value = "enabled", havingValue = "true")
public class SimulatorWebServiceClientAutoConfiguration {

    @Autowired(required = false)
    private SimulatorWebServiceClientConfigurer configurer;

    @Autowired
    private LoggingClientInterceptor loggingClientInterceptor;

    @Autowired
    private SimulatorWebServiceClientConfigurationProperties simulatorConfiguration;

    @Bean
    public WebServiceClient simulatorWsClientEndpoint() {
        WebServiceClient endpoint = new WebServiceClient();
        endpoint.getEndpointConfiguration().setDefaultUri(getRequestUrl());
        endpoint.getEndpointConfiguration().setMessageFactory(getMessageFactory());
        endpoint.getEndpointConfiguration().setInterceptors(Arrays.asList(interceptors()));
        endpoint.getEndpointConfiguration().setErrorHandlingStrategy(ErrorHandlingStrategy.PROPAGATE);
        return endpoint;
    }

    @Bean
    protected SoapMessageFactory getMessageFactory() {
        return new SaajSoapMessageFactory();
    }

    protected String getRequestUrl() {
        if (configurer != null) {
            return configurer.requestUrl();
        }
        return simulatorConfiguration.getRequestUrl();
    }

    /**
     * Provides list of endpoint interceptors.
     *
     * @return
     */
    protected ClientInterceptor[] interceptors() {
        List<ClientInterceptor> interceptors = new ArrayList<>();
        if (configurer != null) {
            Collections.addAll(interceptors, configurer.interceptors());
        }
        interceptors.add(loggingClientInterceptor);
        return interceptors.toArray(new ClientInterceptor[0]);
    }
}
