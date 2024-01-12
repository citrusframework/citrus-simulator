/*
 * Copyright 2024 the original author or authors.
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

package org.citrusframework.simulator;

import org.citrusframework.container.BeforeSuite;
import org.citrusframework.container.SequenceBeforeSuite;
import org.citrusframework.dsl.endpoint.CitrusEndpoints;
import org.citrusframework.message.ErrorHandlingStrategy;
import org.citrusframework.simulator.sample.Simulator;
import org.citrusframework.ws.client.WebServiceClient;
import org.citrusframework.ws.interceptor.LoggingClientInterceptor;
import org.citrusframework.xml.XsdSchemaRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

@Configuration
public class EndpointConfig {
    @Bean
    public XsdSchemaRepository schemaRepository() {
        XsdSchemaRepository schemaRepository = new XsdSchemaRepository();
        schemaRepository.getLocations().add("classpath:xsd/HelloService.xsd");
        return schemaRepository;
    }

    @Bean
    public WebServiceClient simulatorClient() {
        return CitrusEndpoints.soap().client()
            .defaultUri(String.format("http://localhost:%s/services/ws/simulator", 8080))
            .interceptor(loggingClientInterceptor())
            .messageFactory(messageFactory())
            .faultStrategy(ErrorHandlingStrategy.PROPAGATE)
            .build();
    }

    @Bean
    public WebServiceClient nestedSimulatorClient() {
        return CitrusEndpoints.soap().client()
            .defaultUri(String.format("http://localhost:%s/services/ws/nested/simulator", 8080))
            .interceptor(loggingClientInterceptor())
            .messageFactory(messageFactory())
            .faultStrategy(ErrorHandlingStrategy.PROPAGATE)
            .build();
    }

    @Bean
    public SaajSoapMessageFactory messageFactory() {
        return new SaajSoapMessageFactory();
    }

    @Bean
    public LoggingClientInterceptor loggingClientInterceptor() {
        return new LoggingClientInterceptor();
    }

    @Bean
    @ConditionalOnProperty(name = "simulator.mode", havingValue = "embedded")
    public BeforeSuite startEmbeddedSimulator() {
        return new SequenceBeforeSuite.Builder().actions(context -> SpringApplication.run(
            Simulator.class)).build();
    }
}
