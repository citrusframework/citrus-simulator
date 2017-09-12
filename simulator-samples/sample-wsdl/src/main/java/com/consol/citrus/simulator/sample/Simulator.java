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

package com.consol.citrus.simulator.sample;

import com.consol.citrus.simulator.annotation.EnableWebServiceSimulation;
import com.consol.citrus.simulator.annotation.SimulatorApplication;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.ws.WsdlScenarioGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Christoph Deppisch
 */
@SpringBootApplication
@SimulatorApplication
@EnableWebServiceSimulation
public class Simulator {

    public static void main(String[] args) {
        SpringApplication.run(Simulator.class, args);
    }

    @Bean
    public static WsdlScenarioGenerator scenarioGenerator(SimulatorConfigurationProperties simulatorConfiguration) {
        WsdlScenarioGenerator generator = new WsdlScenarioGenerator(new ClassPathResource("xsd/Hello.wsdl"));
        generator.setSimulatorConfiguration(simulatorConfiguration);
        return generator;
    }
}
