package org.citrusframework.simulator;

import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.dsl.runner.TestRunnerBeforeSuiteSupport;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.jms.endpoint.JmsSyncEndpoint;
import org.citrusframework.simulator.sample.Simulator;
import com.consol.citrus.xml.XsdSchemaRepository;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

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
    public HttpClient simulatorClient() {
        return CitrusEndpoints.http().client()
                .requestUrl(String.format("http://localhost:%s/services/rest/simulator", 8080))
                .build();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(name = "simulator.mode", havingValue = "embedded")
    public BrokerService messageBroker() throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.setPersistent(false);
        brokerService.addConnector("tcp://localhost:61616");
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
    public TestRunnerBeforeSuiteSupport startEmbeddedSimulator() {
        return new TestRunnerBeforeSuiteSupport() {
            @Override
            public void beforeSuite(TestRunner runner) {
                SpringApplication.run(Simulator.class);
            }
        };
    }
}
