package org.citrusframework.simulator.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.citrusframework.http.actions.HttpServerResponseActionBuilder.HttpMessageBuilderSupport;
import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class HttpScenarioActionBuilderTest {

    private static final TestJsonObject JSON_OBJECT_REPRESENTATION = new TestJsonObject("value");
    private static final String JSON_STRING_REPRESENTATION = """
        {
          "property" : "value"
        }""";

    @Mock
    private ScenarioEndpoint scenarioEndpointMock;

    private HttpScenarioActionBuilder fixture;

    private static void verifyOkJsonResponse(HttpMessageBuilderSupport httpMessageBuilderSupport) {
        HttpMessage httpMessage = (HttpMessage) ReflectionTestUtils.getField(httpMessageBuilderSupport, "httpMessage");
        assertNotNull(httpMessage);

        assertEquals(HttpStatus.OK, httpMessage.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, httpMessage.getContentType());
        assertEquals(JSON_STRING_REPRESENTATION, httpMessage.getPayload(String.class).replace("\r\n", "\n"));
    }

    @BeforeEach
    void beforeEachSetup() {
        fixture = new HttpScenarioActionBuilder(scenarioEndpointMock);
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
