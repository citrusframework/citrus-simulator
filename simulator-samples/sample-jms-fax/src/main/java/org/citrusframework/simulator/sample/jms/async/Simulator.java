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

package org.citrusframework.simulator.sample.jms.async;

import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import org.citrusframework.simulator.jms.SimulatorJmsAdapter;
import org.citrusframework.simulator.scenario.mapper.ContentBasedXPathScenarioMapper;
import org.citrusframework.simulator.scenario.mapper.ScenarioMapper;
import com.consol.citrus.xml.namespace.NamespaceContextBuilder;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.jms.ConnectionFactory;

/**
 * @author Martin Maher
 */
@SpringBootApplication
public class Simulator extends SimulatorJmsAdapter {

    public static void main(String[] args) {
        SpringApplication.run(Simulator.class, args);
    }

    @Autowired
    private NamespaceContextBuilder namespaceContextBuilder;

    @Value("${citrus.simulator.jms.status.destination}")
    private String statusDestinationName;

    @Override
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    @Override
    public ScenarioMapper scenarioMapper() {
        return new ContentBasedXPathScenarioMapper(namespaceContextBuilder)
                .addXPathExpression("//fax:clientId");
    }

    @Bean
    public JmsEndpoint simulatorJmsStatusEndpoint() {
        return CitrusEndpoints.jms()
                .asynchronous()
                .destination(statusDestinationName)
                .connectionFactory(connectionFactory())
                .build();
    }

}
