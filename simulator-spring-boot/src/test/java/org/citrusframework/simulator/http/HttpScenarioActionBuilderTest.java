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
