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

package com.consol.citrus.simulator.annotation;

import com.consol.citrus.message.ErrorHandlingStrategy;
import com.consol.citrus.ws.client.WebServiceClient;
import com.consol.citrus.ws.interceptor.LoggingClientInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
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
@Import(SimulatorWebServiceLoggingSupport.class)
@EnableConfigurationProperties(SimulatorWebServiceClientConfigurationProperties.class)
public class SimulatorWebServiceClientSupport {

    @Autowired(required = false)
    private SimulatorWebServiceClientConfigurer configurer;

    @Autowired
    private LoggingClientInterceptor loggingClientInterceptor;


    @Bean(name = "simulatorWsClientEndpoint")
    public WebServiceClient webServiceClientEndpoint(SimulatorWebServiceClientConfigurationProperties configProperties) {
        WebServiceClient endpoint = new WebServiceClient();
        endpoint.getEndpointConfiguration().setDefaultUri(getRequestUrl(configProperties));
        endpoint.getEndpointConfiguration().setMessageFactory(getMessageFactory());
        endpoint.getEndpointConfiguration().setInterceptors(Arrays.asList(interceptors()));
        endpoint.getEndpointConfiguration().setErrorHandlingStrategy(ErrorHandlingStrategy.PROPAGATE);
        return endpoint;
    }

    @Bean
    protected SoapMessageFactory getMessageFactory() {
        return new SaajSoapMessageFactory();
    }

    protected String getRequestUrl(SimulatorWebServiceClientConfigurationProperties configProperties) {
        if (configurer != null) {
            return configurer.requestUrl();
        }
        return configProperties.getRequestUrl();
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
        return interceptors.toArray(new ClientInterceptor[interceptors.size()]);
    }
}
