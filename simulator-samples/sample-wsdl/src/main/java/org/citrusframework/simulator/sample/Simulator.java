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

package org.citrusframework.simulator.sample;

import static java.util.Collections.singletonList;

import java.util.List;
import org.citrusframework.endpoint.EndpointAdapter;
import org.citrusframework.endpoint.adapter.StaticEndpointAdapter;
import org.citrusframework.message.Message;
import org.citrusframework.simulator.ws.SimulatorWebServiceAdapter;
import org.citrusframework.simulator.ws.SimulatorWebServiceConfigurationProperties;
import org.citrusframework.simulator.ws.WsdlScenarioGenerator;
import org.citrusframework.spi.Resources;
import org.citrusframework.ws.message.SoapFault;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author Christoph Deppisch
 */
@SpringBootApplication
public class Simulator extends SimulatorWebServiceAdapter {

    public static void main(String[] args) {
        SpringApplication.run(Simulator.class, args);
    }

    @Override
    public List<String> servletMappings(SimulatorWebServiceConfigurationProperties simulatorWebServiceConfiguration) {
        return singletonList("/services/ws/HelloService/*");
    }

    @Override
    public EndpointAdapter fallbackEndpointAdapter() {
        return new StaticEndpointAdapter() {

            @Override
            protected Message handleMessageInternal(Message message) {
                return new SoapFault()
                        .faultActor("SERVER")
                        .faultCode("{http://localhost:8080/HelloService/v1}HELLO:ERROR-1001")
                        .faultString("Internal server error");
            }
        };
    }

    @Bean
    public static WsdlScenarioGenerator scenarioGenerator() {
        return new WsdlScenarioGenerator(new Resources.ClasspathResource("xsd/Hello.wsdl"));
    }
}
