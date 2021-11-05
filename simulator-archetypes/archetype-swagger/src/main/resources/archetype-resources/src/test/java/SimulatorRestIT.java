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

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Test;

@Test
public class SimulatorRestIT extends TestNGCitrusTestDesigner {

    /** Test Http REST client */
    @Autowired
    private HttpClient petstoreClient;

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
}
