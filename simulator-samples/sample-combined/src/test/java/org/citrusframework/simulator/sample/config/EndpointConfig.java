package org.citrusframework.simulator.sample.config;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.citrusframework.container.BeforeSuite;
import org.citrusframework.context.TestContext;
import org.citrusframework.dsl.endpoint.CitrusEndpoints;
import org.citrusframework.http.client.HttpClient;
import org.citrusframework.jms.endpoint.JmsSyncEndpoint;
import org.citrusframework.simulator.sample.CombinedSimulator;
import org.citrusframework.xml.XsdSchemaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import jakarta.jms.ConnectionFactory;

import static org.citrusframework.container.SequenceBeforeSuite.Builder.beforeSuite;

/**
 * @author Christoph Deppisch
 */
@Configuration
@PropertySource({"classpath:application.properties"})
public class EndpointConfig {

    @Value("${spring.artemis.broker-url:tcp://localhost:61616}")
    private String brokerURL;

    @Bean
    public XsdSchemaRepository schemaRepository() {
        XsdSchemaRepository schemaRepository = new XsdSchemaRepository();
        schemaRepository.getLocations().add("classpath:xsd/HelloService.xsd");
        return schemaRepository;
    }

    @Bean
    public HttpClient simulatorClient() {
        return CitrusEndpoints.http().client()
                .requestUrl(String.format("http://localhost:%s/services/rest/simulator", 8080))
                .build();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(brokerURL);
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
    @ConditionalOnProperty(name = "simulator.mode", havingValue = "embedded")
    public BeforeSuite startEmbeddedSimulator() {
        return beforeSuite().actions(
            (TestContext context) -> SpringApplication.run(CombinedSimulator.class)
        ).build();
    }
}
