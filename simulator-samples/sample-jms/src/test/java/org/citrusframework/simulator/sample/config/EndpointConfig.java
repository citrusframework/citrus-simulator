package org.citrusframework.simulator.sample.config;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.citrusframework.dsl.endpoint.CitrusEndpoints;
import org.citrusframework.jms.endpoint.JmsSyncEndpoint;
import org.citrusframework.xml.XsdSchemaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import jakarta.jms.ConnectionFactory;

@Configuration
@PropertySource({"classpath:application.properties"})
public class EndpointConfig {

    @Bean
    public XsdSchemaRepository schemaRepository() {
        XsdSchemaRepository schemaRepository = new XsdSchemaRepository();
        schemaRepository.getLocations().add("classpath:xsd/HelloService.xsd");
        return schemaRepository;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
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
}
