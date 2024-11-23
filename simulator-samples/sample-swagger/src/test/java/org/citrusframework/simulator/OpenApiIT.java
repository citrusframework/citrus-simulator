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

import org.citrusframework.annotations.CitrusTest;
import org.citrusframework.container.BeforeSuite;
import org.citrusframework.container.SequenceBeforeSuite;
import org.citrusframework.dsl.endpoint.CitrusEndpoints;
import org.citrusframework.http.client.HttpClient;
import org.citrusframework.message.MessageType;
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
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
@Test
@Ignore
@ContextConfiguration(classes = OpenApiIT.EndpointConfig.class)
public class OpenApiIT extends TestNGCitrusSpringSupport {

    @Autowired
    @Qualifier("petstoreClientV3")
    private HttpClient petstoreClientV3;

    /**
     * Client to access simulator user interface
     */
    @Autowired
    @Qualifier("simulatorUiClient")
    private HttpClient simulatorUiClient;

    @CitrusTest
    public void uiInfoShouldSucceed() {
        $(http().client(simulatorUiClient)
            .send()
            .get("/api/manage/info")
            .message()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE));

        $(http().client(simulatorUiClient)
            .receive()
            .response(HttpStatus.OK)
            .message()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("{" +
                "\"simulator\":" +
                "{" +
                "\"name\":\"REST Petstore Simulator\"," +
                "\"version\":\"@ignore@\"" +
                "}," +
                "\"activeProfiles\": []" +
                "}"));
    }

    @CitrusTest
    public void addPetShouldSucceed() {
        variable("name", "hasso");
        variable("category", "dog");
        variable("tags", "huge");
        variable("status", "pending");

        $(http().client(petstoreClientV3)
            .send()
            .post("/pet")
            .message()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new Resources.ClasspathResource("templates/pet.json")));

        $(http().client(petstoreClientV3)
            .receive()
            .response(HttpStatus.OK));
    }

    @CitrusTest
    public void addPetShouldFailOnMissingName() {
        variable("category", "dog");
        variable("tags", "huge");
        variable("status", "pending");

        $(http().client(petstoreClientV3)
            .send()
            .post("/pet")
            .message()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new Resources.ClasspathResource("templates/pet_invalid.json")));

        $(http().client(petstoreClientV3)
            .receive()
            .response(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @CitrusTest
    public void deletePetShouldSucceed() {
        variable("id", "citrus:randomNumber(10)");

        $(http().client(petstoreClientV3)
            .send()
            .delete("/pet/${id}"));

        $(http().client(petstoreClientV3)
            .receive()
            .response(HttpStatus.OK));
    }

    @CitrusTest
    public void deletePetShouldFailOnWrongIdFormat() {

        $(http().client(petstoreClientV3)
            .send()
            .delete("/pet/xxxx"));

        $(http().client(petstoreClientV3)
            .receive()
            .response(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @CitrusTest
    public void getPetByIdShouldSucceed() {
        variable("id", "citrus:randomNumber(10)");

        $(http().client(petstoreClientV3)
            .send()
            .get("/pet/${id}")
            .message()
            .header("api_key", "xxx_api_key")
            .accept(MediaType.APPLICATION_JSON_VALUE));

        $(http().client(petstoreClientV3)
            .receive()
            .response(HttpStatus.OK)
            .message()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new Resources.ClasspathResource("templates/pet-control.json")));
    }

    @CitrusTest
    public void getPetByIdShouldNotFailOnMissingApiKey() {
        variable("id", "citrus:randomNumber(10)");

        $(http().client(petstoreClientV3)
            .send()
            .get("/pet/${id}")
            .message()
            .accept(MediaType.APPLICATION_JSON_VALUE));

        // api_key is not required in V3, therefore no error here
        $(http().client(petstoreClientV3)
            .receive()
            .response(HttpStatus.OK));
    }

    @CitrusTest
    public void updatePetShouldSucceed() {
        variable("name", "catty");
        variable("category", "cat");
        variable("tags", "cute");
        variable("status", "sold");

        $(http().client(petstoreClientV3)
            .send()
            .put("/pet")
            .message()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new Resources.ClasspathResource("templates/pet.json")));

        $(http().client(petstoreClientV3)
            .receive()
            .response(HttpStatus.OK));
    }

    @CitrusTest
    public void findByStatusShouldSucceed() {
        $(http().client(petstoreClientV3)
            .send()
            .get("/pet/findByStatus")
            .message()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .queryParam("status", "pending"));

        $(http().client(petstoreClientV3)
            .receive()
            .response(HttpStatus.OK)
            .message()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("[ citrus:readFile(templates/pet-control.json) ]"));
    }

    @CitrusTest
    public void findByStatusShouldNotFailOnMissingQueryParameter() {
        $(http().client(petstoreClientV3)
            .send()
            .get("/pet/findByStatus")
            .message()
            .accept(MediaType.APPLICATION_JSON_VALUE));

        // In petstore 3 status is not required.
        $(http().client(petstoreClientV3)
            .receive()
            .response(HttpStatus.OK));
    }

    @CitrusTest
    public void findByTagsShouldSucceed() {
        $(http().client(petstoreClientV3)
            .send()
            .get("/pet/findByTags")
            .message()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .queryParam("tags", "huge,cute"));

        $(http().client(petstoreClientV3)
            .receive()
            .response(HttpStatus.OK)
            .message()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("[ citrus:readFile(templates/pet-control.json) ]"));
    }

    @CitrusTest
    public void placeOrderShouldSucceed() {
        $(http().client(petstoreClientV3)
            .send()
            .post("/store/order")
            .message()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new Resources.ClasspathResource("templates/order.json"))
            .header("api_key", "xxx_api_key"));

        $(http().client(petstoreClientV3)
            .receive()
            .response(HttpStatus.OK));
    }

    @CitrusTest
    public void placeOrderShouldFailOnInvalidDateFormat() {
        $(http().client(petstoreClientV3)
            .send()
            .post("/store/order")
            .message()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new Resources.ClasspathResource("templates/order_invalid_date.json"))
            .header("api_key", "xxx_api_key"));

        $(http().client(petstoreClientV3)
            .receive()
            .response(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @CitrusTest
    public void loginUserShouldSucceed() {
        $(http().client(petstoreClientV3)
            .send()
            .get("/user/login")
            .queryParam("username", "citrus:randomString(10)")
            .queryParam("password", "citrus:randomString(8)")
            .message()
            .header("api_key", "xxx_api_key")
            .accept(MediaType.APPLICATION_JSON_VALUE));

        // X-Rate-Limit and X-Expires-After are not required in V3, therefore we cannot assert them here.
        $(http().client(petstoreClientV3)
            .receive()
            .response(HttpStatus.OK)
            .message()
            .type(MessageType.JSON)
            .body("@notEmpty()@"));
    }

    @Configuration
    public static class EndpointConfig {

        @Bean
        public HttpClient petstoreClientV3() {
            return CitrusEndpoints.http().client()
                .requestUrl(String.format("http://localhost:%s/petstore/api/v3", 8080))
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
        public BeforeSuite startEmbeddedSimulator() {
            return new SequenceBeforeSuite.Builder().actions(
                context -> SpringApplication.run(Simulator.class)).build();
        }
    }
}
