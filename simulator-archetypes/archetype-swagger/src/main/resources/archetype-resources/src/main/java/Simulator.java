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

package ${package};

import com.consol.citrus.endpoint.EndpointAdapter;
import com.consol.citrus.endpoint.adapter.StaticEndpointAdapter;
import com.consol.citrus.http.message.HttpMessage;
import com.consol.citrus.message.Message;
import org.citrusframework.simulator.scenario.mapper.ScenarioMapper;
import org.citrusframework.simulator.scenario.mapper.ScenarioMappers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;

@SpringBootApplication
public class Simulator extends SimulatorRestAdapter {

    public static void main(String[] args) {
        SpringApplication.run(Simulator.class, args);
    }

    @Override
    public ScenarioMapper scenarioMapper() {
        return ScenarioMappers.of(new HttpRequestPathScenarioMapper(),
                                  new HttpRequestAnnotationScenarioMapper());
    }

    @Override
    public String urlMapping(SimulatorRestConfigurationProperties simulatorRestConfiguration) {
        return "/petstore/v2/**";
    }

    @Override
    public EndpointAdapter fallbackEndpointAdapter() {
        return new StaticEndpointAdapter() {
            @Override
            protected Message handleMessageInternal(Message message) {
                return new HttpMessage().status(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        };
    }

    @Bean
    public static HttpScenarioGenerator scenarioGenerator() {
        HttpScenarioGenerator generator = new HttpScenarioGenerator(new ClassPathResource("swagger/petstore-api.json"));
        generator.setContextPath("/petstore");
        return generator;
    }
}
