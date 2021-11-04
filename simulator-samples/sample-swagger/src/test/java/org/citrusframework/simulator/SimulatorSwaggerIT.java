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
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import org.citrusframework.simulator.sample.Simulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
@Test
@ContextConfiguration(classes = SimulatorSwaggerIT.EndpointConfig.class)
public class SimulatorSwaggerIT extends TestNGCitrusTestDesigner {

    /** Test Http REST client */
    @Autowired
    @Qualifier("petstoreClient")
    private HttpClient petstoreClient;

    /** Client to access simulator user interface */
    @Autowired
    @Qualifier("simulatorUiClient")
    private HttpClient simulatorUiClient;

    @CitrusTest
    public void testUiInfo() {
        http().client(simulatorUiClient)
                .send()
                .get("/api/manage/info")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        http().client(simulatorUiClient)
                .receive()
                .response(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .payload("{" +
                        "\"simulator\":" +
                            "{" +
                                "\"name\":\"REST Petstore Simulator\"," +
                                "\"version\":\"@ignore@\"" +
                            "}" +
                        "}");
    }

    @CitrusTest
    public void testUiSummaryResults() {
        http().client(simulatorUiClient)
                .send()
                .get("/api/summary/results")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        http().client(simulatorUiClient)
                .receive()
                .response(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .payload("{" +
                            "\"size\":\"@isNumber()@\"," +
                            "\"failed\":\"@isNumber()@\"," +
                            "\"success\":\"@isNumber()@\"," +
                            "\"skipped\":0," +
                            "\"skippedPercentage\":\"0.0\"," +
                            "\"failedPercentage\":\"@ignore@\"," +
                            "\"successPercentage\":\"@ignore@\"}");
    }

    @CitrusTest
    public void testAddPet() {
        variable("name", "hasso");
        variable("category", "dog");
        variable("tags", "huge");
        variable("status", "pending");

        http().client(petstoreClient)
                .send()
                .post("/pet")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .payload(new ClassPathResource("templates/pet.json"));

        http().client(petstoreClient)
                .receive()
                .response(HttpStatus.OK);
    }

    @CitrusTest
    public void testDeletePet() {
        variable("id", "citrus:randomNumber(10)");

        http().client(petstoreClient)
                .send()
                .delete("/pet/${id}");

        http().client(petstoreClient)
                .receive()
                .response(HttpStatus.OK);
    }

    @CitrusTest
    public void testGetPetById() {
        variable("id", "citrus:randomNumber(10)");

        http().client(petstoreClient)
                .send()
                .get("/pet/${id}")
                .accept(MediaType.APPLICATION_JSON_VALUE);

        http().client(petstoreClient)
                .receive()
                .response(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .payload(new ClassPathResource("templates/pet-control.json"));
    }

    @CitrusTest
    public void testUpdatePet() {
        variable("name", "catty");
        variable("category", "cat");
        variable("tags", "cute");
        variable("status", "sold");

        http().client(petstoreClient)
                .send()
                .put("/pet")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .payload(new ClassPathResource("templates/pet.json"));

        http().client(petstoreClient)
                .receive()
                .response(HttpStatus.OK);
    }

    @CitrusTest
    public void testFindByStatus() {
        http().client(petstoreClient)
                .send()
                .get("/pet/findByStatus")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("status", "pending");

        http().client(petstoreClient)
                .receive()
                .response(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .payload("[ citrus:readFile(templates/pet-control.json) ]");
    }

    @CitrusTest
    public void testFindByStatusMissingQueryParameter() {
        http().client(petstoreClient)
                .send()
                .get("/pet/findByStatus")
                .accept(MediaType.APPLICATION_JSON_VALUE);

        http().client(petstoreClient)
                .receive()
                .response(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @CitrusTest
    public void testFindByTags() {
        http().client(petstoreClient)
                .send()
                .get("/pet/findByTags")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("tags", "huge,cute");

        http().client(petstoreClient)
                .receive()
                .response(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .payload("[ citrus:readFile(templates/pet-control.json) ]");
    }

    @CitrusTest
    public void testPlaceOrder() {
        http().client(petstoreClient)
                .send()
                .post("/store/order")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .payload(new ClassPathResource("templates/order.json"));

        http().client(petstoreClient)
                .receive()
                .response(HttpStatus.OK);
    }

    @CitrusTest
    public void testLoginUser() {
        http().client(petstoreClient)
                .send()
                .get("/user/login")
                .queryParam("username", "citrus:randomString(10)")
                .queryParam("password", "citrus:randomString(8)")
                .accept("text/plain");

        http().client(petstoreClient)
                .receive()
                .response(HttpStatus.OK)
                .messageType(MessageType.PLAINTEXT)
                .payload("@notEmpty()@")
                .header("X-Rate-Limit", "@isNumber()@")
                .header("X-Expires-After", "@matchesDatePattern('yyyy-MM-dd'T'hh:mm:ss')@");
    }

    @Configuration
    public static class EndpointConfig {

        @Bean
        public HttpClient petstoreClient() {
            return CitrusEndpoints.http().client()
                    .requestUrl(String.format("http://localhost:%s/petstore/v2", 8080))
                    .build();
        }

        @Bean
        public HttpClient simulatorUiClient() {
            return CitrusEndpoints.http().client()
                    .requestUrl(String.format("http://localhost:%s", 8080))
                    .build();
        }

        @Bean
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
