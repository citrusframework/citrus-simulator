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

import org.citrusframework.annotations.CitrusTest;
import org.citrusframework.container.BeforeSuite;
import org.citrusframework.container.SequenceBeforeSuite;
import org.citrusframework.http.client.HttpClient;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.sample.BankServiceSimulator;
import org.citrusframework.simulator.sample.config.HttpClientConfig;
import org.citrusframework.simulator.sample.model.BankAccount;
import org.citrusframework.simulator.sample.model.CalculateIbanResponse;
import org.citrusframework.simulator.sample.model.ValidateIbanResponse;
import org.citrusframework.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import static org.citrusframework.http.actions.HttpActionBuilder.http;

@Test
@ContextConfiguration(classes = { BankServiceSimulatorIT.EndpointConfig.class, HttpClientConfig.class, SimulatorConfigurationProperties.class})
public class BankServiceSimulatorIT extends TestNGCitrusSpringSupport {

    /**
     * Test Http REST client
     */
    @Autowired
    @Qualifier("simulatorHttpClientEndpoint")
    private HttpClient simulatorClient;

    /**
     * Sends a hello request to server expecting positive response message.
     */
    @CitrusTest
    public void testCalculate() {
        $(http().client(simulatorClient)
                .send()
                .get("/services/rest/bank")
                .queryParam("sortCode", "12345670")
                .queryParam("accountNumber", "0006219653"));

        $(http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(CalculateIbanResponse.builder()
                        .bankAccount(BankAccount.builder()
                                .iban("DE92123456700006219653")
                                .bic("ABCDEFG5670")
                                .bank("The Wealthy ABC bank")
                                .sortCode("12345670")
                                .accountNumber("0006219653")
                                .build()
                        )
                        .build().asJson()
                ))
        ;
    }

    /**
     * Sends goodbye request to server expecting positive response message.
     */
    @CitrusTest
    public void testValidate() {
        $(http().client(simulatorClient)
                .send()
                .get("/services/rest/bank")
                .queryParam("iban", "DE92123456700006219653"));

        $(http().client(simulatorClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(ValidateIbanResponse.builder()
                        .bankAccount(BankAccount.builder()
                                .iban("DE92123456700006219653")
                                .bic("ABCDEFG5670")
                                .bank("The Wealthy ABC bank")
                                .sortCode("12345670")
                                .accountNumber("0006219653")
                                .build()
                        )
                        .valid(true)
                        .build().asJson()
                ));
    }

    @Configuration
    @PropertySource("classpath:application.properties")
    public static class EndpointConfig {

        @Bean
        @ConditionalOnProperty(name = "simulator.mode", havingValue = "embedded")
        public BeforeSuite startEmbeddedSimulator() {
            return new SequenceBeforeSuite.Builder().actions(context -> SpringApplication.run(BankServiceSimulator.class)).build();
        }
    }
}
