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

package org.citrusframework.simulator;

import static org.citrusframework.http.actions.HttpActionBuilder.http;
import static org.citrusframework.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import org.citrusframework.annotations.CitrusTest;
import org.citrusframework.container.BeforeSuite;
import org.citrusframework.container.SequenceBeforeSuite;
import org.citrusframework.dsl.endpoint.CitrusEndpoints;
import org.citrusframework.http.client.HttpClient;
import org.citrusframework.simulator.sample.Simulator;
import org.citrusframework.spi.Resources;
import org.citrusframework.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

@Test
@ContextConfiguration(classes = ResponseGeneratorIT.EndpointConfig.class)
public class ResponseGeneratorIT extends TestNGCitrusSpringSupport {

    @Autowired
    @Qualifier("pingClient")
    private HttpClient pingClient;

    @CitrusTest
    public void shouldPerformDefaultOpenApiPingOperation() {
        variable("id", "1234");

        $(http().client(pingClient)
            .send()
            .put("/ping/${id}")
            .message()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new Resources.ClasspathResource("templates/ping.json")));

        $(http().client(pingClient)
            .receive()
            .response(HttpStatus.OK)
            .message()
            .header("Ping-Time", "@isNumber()@")
            .validate(jsonPath()
                .expression("$.pingCount", not(equalTo("1001")))));
    }

    @CitrusTest
    public void shouldPerformSpecificApiPingOperation() {
        long currentTime = System.currentTimeMillis();
        long expectedPingLimit = currentTime -  1L;

        variable("id", "5000");

        $(http().client(pingClient)
            .send()
            .put("/ping/${id}")
            .message()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new Resources.ClasspathResource("templates/ping.json")));

        $(http().client(pingClient)
            .receive()
            .response(HttpStatus.OK)
            .message()
            .header("Ping-Time", "@isNumber()@")
            .validate(jsonPath()
                .expression("$.id", "5000")
                .expression("$.pingCount", "@greaterThan("+expectedPingLimit+")@"))
        );
    }

    @CitrusTest
    public void shouldReturnPingTime0() {
        variable("id", "15000");

        $(http().client(pingClient)
            .send()
            .put("/ping/${id}")
            .message()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new Resources.ClasspathResource("templates/ping.json")));

        $(http().client(pingClient)
            .receive()
            .response(HttpStatus.OK)
            .message()
            .header("Ping-Time", "0")
        );
    }

    @CitrusTest
    public void shouldFailOnBadRequest() {
        variable("id", "10000");

        $(http().client(pingClient)
            .send()
            .put("/ping/${id}")
            .message()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new Resources.ClasspathResource("templates/ping.json")));

        $(http().client(pingClient)
            .receive()
            .response(HttpStatus.BAD_REQUEST)
            .message().body("Requests with id == 10000 cannot be processed!"));
    }

    @CitrusTest
    public void shouldFailOnUnsupportedType() {
        variable("id", "10000");

        $(http().client(pingClient)
            .send()
            .put("/ping/${id}")
            .message()
            .accept(MediaType.APPLICATION_XML_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new Resources.ClasspathResource("templates/ping.json")));

        $(http().client(pingClient)
            .receive()
            .response(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @CitrusTest
    public void shouldFailOnMissingPingTimeHeader() {
        variable("id", "4000");

        $(http().client(pingClient)
            .send()
            .put("/ping/${id}")
            .message()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new Resources.ClasspathResource("templates/ping.json")));

        $(http().client(pingClient)
            .receive()
            .response(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Configuration
    public static class EndpointConfig {

        @Bean
        public HttpClient pingClient() {
            return CitrusEndpoints.http().client()
                .requestUrl(String.format("http://localhost:%s/pingapi/v1", 8080))
                .build();
        }

        @Bean
        @ConditionalOnProperty(name = "simulator.mode", havingValue = "embedded")
        public BeforeSuite startEmbeddedSimulator() {
            return new SequenceBeforeSuite.Builder().actions(context -> SpringApplication.run(
                Simulator.class)).build();
        }
    }
}
