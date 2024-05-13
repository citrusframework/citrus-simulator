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

package org.citrusframework.simulator.http;

import org.citrusframework.http.actions.HttpServerResponseActionBuilder.HttpMessageBuilderSupport;
import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class HttpScenarioActionBuilderTest {

    private static final TestJsonObject JSON_OBJECT_REPRESENTATION = new TestJsonObject("value");
    private static final String JSON_STRING_REPRESENTATION = """
        {
          "property" : "value"
        }""";

    private HttpScenarioActionBuilder fixture;

    private static void verifyOkJsonResponse(HttpMessageBuilderSupport httpMessageBuilderSupport) {
        assertThat(httpMessageBuilderSupport).extracting("httpMessage")
            .isInstanceOfSatisfying(HttpMessage.class, httpMessage -> {
                assertEquals(HttpStatus.OK, httpMessage.getStatusCode());
                assertEquals(MediaType.APPLICATION_JSON_VALUE, httpMessage.getContentType());
                assertEquals(JSON_STRING_REPRESENTATION, httpMessage.getPayload(String.class).replace("\r\n", "\n"));
            });
    }

    @BeforeEach
    void beforeEachSetup() {
        fixture = new HttpScenarioActionBuilder(mock(ScenarioEndpoint.class));
    }

    @Test
    void sendOkJsonFromString() {
        HttpMessageBuilderSupport httpMessageBuilderSupport = fixture.sendOkJson(JSON_STRING_REPRESENTATION);
        verifyOkJsonResponse(httpMessageBuilderSupport);
    }

    @Test
    void sendOkJsonFromObject() {
        HttpMessageBuilderSupport httpMessageBuilderSupport = fixture.sendOkJson(JSON_OBJECT_REPRESENTATION);
        verifyOkJsonResponse(httpMessageBuilderSupport);
    }

    private record TestJsonObject(String property) {
    }
}
