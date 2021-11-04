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

package org.citrusframework.simulator;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.dsl.runner.TestRunnerBeforeSuiteSupport;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.jms.endpoint.JmsSyncEndpoint;
import org.citrusframework.simulator.sample.Simulator;
import com.consol.citrus.xml.XsdSchemaRepository;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
@Test
@ContextConfiguration(classes = SimulatorJmsIT.EndpointConfig.class)
public class SimulatorJmsIT extends TestNGCitrusTestDesigner {

    private String defaultResponse = "<DefaultResponse>This is a default response!</DefaultResponse>";

    @Autowired
    private JmsSyncEndpoint jmsSyncEndpoint;

    @CitrusTest
    public void testHelloRequest() {
        send(jmsSyncEndpoint)
                .payload("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say Hello!" +
                         "</Hello>");

        receive(jmsSyncEndpoint)
                .payload("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Hi there!" +
                         "</HelloResponse>");
    }

    @CitrusTest
    public void testGoodByeRequest() {
        send(jmsSyncEndpoint)
                .payload("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say GoodBye!" +
                         "</GoodBye>");

        receive(jmsSyncEndpoint)
                .payload("<GoodByeResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Bye bye!" +
                         "</GoodByeResponse>");
    }

    @CitrusTest
    public void testDefaultRequest() {
        send(jmsSyncEndpoint)
                .payload("<Default>" +
                            "Should trigger default scenario" +
                        "</Default>");

        receive(jmsSyncEndpoint)
                .payload(defaultResponse);
    }

    @CitrusTest
    public void testInterveningRequest() {
        variable("correlationId", "citrus:randomNumber(10)");

        send(jmsSyncEndpoint)
                .payload("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Go to sleep!" +
                        "</GoodNight>")
                .header("correlationId", "${correlationId}");

        receive(jmsSyncEndpoint)
                .payload("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Good Night!" +
                        "</GoodNightResponse>");

        send(jmsSyncEndpoint)
                .payload("<InterveningRequest>Before correlation</InterveningRequest>");

        receive(jmsSyncEndpoint)
                .payload(defaultResponse);

        send(jmsSyncEndpoint)
                .payload("<InterveningRequest>In between!</InterveningRequest>")
                .header("correlationId", "${correlationId}");

        receive(jmsSyncEndpoint)
                .payload("<InterveningResponse>In between!</InterveningResponse>");

        sleep(2000L);

        send(jmsSyncEndpoint)
                .payload("<InterveningRequest>After correlation</InterveningRequest>")
                .header("correlationId", "${correlationId}");

        receive(jmsSyncEndpoint)
                .payload(defaultResponse);
    }

    @Configuration
    public static class EndpointConfig {
        @Bean
        public XsdSchemaRepository schemaRepository() {
            XsdSchemaRepository schemaRepository = new XsdSchemaRepository();
            schemaRepository.getLocations().add("classpath:xsd/HelloService.xsd");
            return schemaRepository;
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
}
