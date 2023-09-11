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

import java.util.Collections;

import org.apache.activemq.artemis.core.config.impl.SecurityConfiguration;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.spi.core.security.ActiveMQJAASSecurityManager;
import org.apache.activemq.artemis.spi.core.security.jaas.InVMLoginModule;
import org.citrusframework.annotations.CitrusTest;
import org.citrusframework.container.BeforeSuite;
import org.citrusframework.container.SequenceBeforeSuite;
import org.citrusframework.dsl.endpoint.CitrusEndpoints;
import org.citrusframework.jms.endpoint.JmsSyncEndpoint;
import org.citrusframework.simulator.sample.Simulator;
import org.citrusframework.testng.spring.TestNGCitrusSpringSupport;
import org.citrusframework.xml.XsdSchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import static org.citrusframework.actions.ReceiveMessageAction.Builder.receive;
import static org.citrusframework.actions.SendMessageAction.Builder.send;
import static org.citrusframework.actions.SleepAction.Builder.sleep;

/**
 * @author Christoph Deppisch
 */
@Test
@ContextConfiguration(classes = SimulatorJmsIT.EndpointConfig.class)
public class SimulatorJmsIT extends TestNGCitrusSpringSupport {

    private final String defaultResponse = "<DefaultResponse>This is a default response!</DefaultResponse>";

    @Autowired
    private JmsSyncEndpoint jmsSyncEndpoint;

    @CitrusTest
    public void testHelloRequest() {
        $(send(jmsSyncEndpoint)
                .message()
                .body("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say Hello!" +
                         "</Hello>"));

        $(receive(jmsSyncEndpoint)
                .message()
                .body("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Hi there!" +
                         "</HelloResponse>"));
    }

    @CitrusTest
    public void testGoodByeRequest() {
        $(send(jmsSyncEndpoint)
                .message()
                .body("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Say GoodBye!" +
                         "</GoodBye>"));

        $(receive(jmsSyncEndpoint)
                .message()
                .body("<GoodByeResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Bye bye!" +
                         "</GoodByeResponse>"));
    }

    @CitrusTest
    public void testDefaultRequest() {
        $(send(jmsSyncEndpoint)
                .message()
                .body("<Default>" +
                            "Should trigger default scenario" +
                        "</Default>"));

        $(receive(jmsSyncEndpoint)
                .message()
                .body(defaultResponse));
    }

    @CitrusTest
    public void testInterveningRequest() {
        variable("correlationId", "citrus:randomNumber(10)");

        $(send(jmsSyncEndpoint)
                .message()
                .body("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Go to sleep!" +
                        "</GoodNight>")
                .header("correlationId", "${correlationId}"));

        $(receive(jmsSyncEndpoint)
                .message()
                .body("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                            "Good Night!" +
                        "</GoodNightResponse>"));

        $(send(jmsSyncEndpoint)
                .message()
                .body("<InterveningRequest>Before correlation</InterveningRequest>"));

        $(receive(jmsSyncEndpoint)
                .message()
                .body(defaultResponse));

        $(send(jmsSyncEndpoint)
                .message()
                .body("<InterveningRequest>In between!</InterveningRequest>")
                .header("correlationId", "${correlationId}"));

        $(receive(jmsSyncEndpoint)
                .message()
                .body("<InterveningResponse>In between!</InterveningResponse>"));

        $(sleep().milliseconds(2000L));

        $(send(jmsSyncEndpoint)
                .message()
                .body("<InterveningRequest>After correlation</InterveningRequest>")
                .header("correlationId", "${correlationId}"));

        $(receive(jmsSyncEndpoint)
                .message()
                .body(defaultResponse));
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
}
