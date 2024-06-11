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

package org.citrusframework.simulator;

import org.apache.activemq.artemis.core.config.impl.SecurityConfiguration;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.spi.core.security.ActiveMQJAASSecurityManager;
import org.apache.activemq.artemis.spi.core.security.jaas.InVMLoginModule;
import org.citrusframework.container.BeforeSuite;
import org.citrusframework.container.SequenceBeforeSuite;
import org.citrusframework.dsl.endpoint.CitrusEndpoints;
import org.citrusframework.http.client.HttpClient;
import org.citrusframework.jms.endpoint.JmsSyncEndpoint;
import org.citrusframework.message.ErrorHandlingStrategy;
import org.citrusframework.simulator.sample.Simulator;
import org.citrusframework.ws.client.WebServiceClient;
import org.citrusframework.ws.interceptor.LoggingClientInterceptor;
import org.citrusframework.xml.XsdSchemaRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import java.util.Collections;

import static java.lang.String.format;

/**
 * @author Christoph Deppisch
 */
@Configuration
public class EndpointConfig {

    @Bean
    public XsdSchemaRepository schemaRepository() {
        XsdSchemaRepository schemaRepository = new XsdSchemaRepository();
        schemaRepository.getLocations().add("classpath:xsd/HelloService.xsd");
        return schemaRepository;
    }

    @Bean
    public HttpClient simulatorRESTClient() {
        return CitrusEndpoints.http().client()
            .requestUrl(format("http://localhost:%s/services/rest/simulator", 8080))
            .build();
    }

    @Bean
    public WebServiceClient simulatorWSClient(LoggingClientInterceptor loggingClientInterceptor, SaajSoapMessageFactory messageFactory) {
        return CitrusEndpoints.soap().client()
            .defaultUri(format("http://localhost:%s/services/ws/simulator", 8080))
            .interceptor(loggingClientInterceptor)
            .messageFactory(messageFactory)
            .faultStrategy(ErrorHandlingStrategy.PROPAGATE)
            .build();
    }

    @Bean
    public LoggingClientInterceptor loggingClientInterceptor() {
        return new LoggingClientInterceptor();
    }

    @Bean
    public SaajSoapMessageFactory messageFactory() {
        return new SaajSoapMessageFactory();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(name = "simulator.mode", havingValue = "embedded")
    public EmbeddedActiveMQ messageBroker() {
        EmbeddedActiveMQ brokerService = new EmbeddedActiveMQ();
        SecurityConfiguration securityConfiguration = new SecurityConfiguration(Collections.singletonMap("citrus", "citrus"), Collections.singletonMap("citrus", Collections.singletonList("citrus")));
        securityConfiguration.setDefaultUser("citrus");
        brokerService.setSecurityManager(new ActiveMQJAASSecurityManager(InVMLoginModule.class.getName(), securityConfiguration));
        return brokerService;
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    @Bean
    public JmsSyncEndpoint simulatorEndpoint() {
        return CitrusEndpoints.jms()
            .synchronous()
            .connectionFactory(connectionFactory())
            .destination("Citrus.Simulator.Inbound")
            .timeout(10000L)
            .build();
    }

    @Bean
    @DependsOn("messageBroker")
    @ConditionalOnProperty(name = "simulator.mode", havingValue = "embedded")
    public BeforeSuite startEmbeddedSimulator() {
        return new SequenceBeforeSuite.Builder().actions(context -> SpringApplication.run(Simulator.class)).build();
    }
}
