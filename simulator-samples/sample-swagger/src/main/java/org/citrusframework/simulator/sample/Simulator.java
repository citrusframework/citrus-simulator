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

import java.util.List;
import org.citrusframework.endpoint.EndpointAdapter;
import org.citrusframework.endpoint.adapter.StaticEndpointAdapter;
import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.message.Message;
import org.citrusframework.openapi.OpenApiRepository;
import org.citrusframework.simulator.http.HttpRequestAnnotationScenarioMapper;
import org.citrusframework.simulator.http.HttpRequestPathScenarioMapper;
import org.citrusframework.simulator.http.HttpResponseActionBuilderProvider;
import org.citrusframework.simulator.http.HttpScenarioGenerator;
import org.citrusframework.simulator.http.SimulatorRestAdapter;
import org.citrusframework.simulator.http.SimulatorRestConfigurationProperties;
import org.citrusframework.simulator.scenario.mapper.ScenarioMapper;
import org.citrusframework.simulator.scenario.mapper.ScenarioMappers;
import org.citrusframework.spi.Resources;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

/**
 * @author Christoph Deppisch
 */
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
    public List<String> urlMappings(
        SimulatorRestConfigurationProperties simulatorRestConfiguration) {
        return List.of("/petstore/v2/**", "/petstore/api/v3/**", "/pingapi/v1/**");
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
        return new HttpScenarioGenerator(
            Resources.create("classpath:swagger/petstore-api.json"));
    }

    @Bean
    public static OpenApiRepository swaggerRepository() {
        OpenApiRepository openApiRepository = new OpenApiRepository();
        openApiRepository.setRootContextPath("/petstore");
        openApiRepository.setLocations(List.of("swagger/petstore-api.json"));
        return openApiRepository;
    }

    @Bean
    public static OpenApiRepository openApiRepository() {
        OpenApiRepository openApiRepository = new OpenApiRepository();
        openApiRepository.setRootContextPath("/petstore");
        openApiRepository.setLocations(List.of("openapi/petstore-v3.json"));
        return openApiRepository;
    }

    @Bean
    public static OpenApiRepository pingApiRepository() {
        OpenApiRepository openApiRepository = new OpenApiRepository();
        openApiRepository.setLocations(List.of("openapi/ping-v1.yaml"));
        return openApiRepository;
    }

    @Bean
    static HttpResponseActionBuilderProvider httpResponseActionBuilderProvider() {
        return new SpecificPingResponseMessageBuilder();
    }

}
