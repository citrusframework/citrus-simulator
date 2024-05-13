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

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.http.actions.HttpActionBuilder;
import org.citrusframework.http.actions.HttpServerActionBuilder;
import org.citrusframework.http.actions.HttpServerResponseActionBuilder.HttpMessageBuilderSupport;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * @author Christoph Deppisch
 */
public class HttpScenarioActionBuilder extends HttpActionBuilder {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().enable(INDENT_OUTPUT);

    /** Scenario endpoint */
    private final ScenarioEndpoint scenarioEndpoint;

    public HttpScenarioActionBuilder(ScenarioEndpoint scenarioEndpoint) {
        this.scenarioEndpoint = scenarioEndpoint;
    }

    /**
     * Default scenario receive operation.
     * @return
     */
    public HttpServerActionBuilder.HttpServerReceiveActionBuilder receive() {
        return server(scenarioEndpoint).receive();
    }

    /**
     * Default scenario send response operation.
     * @return
     */
    public HttpServerActionBuilder.HttpServerSendActionBuilder send() {
        return server(scenarioEndpoint).send();
    }

    /**
     * Send scenario {@code application/json} response operation.
     * @return
     */
    public HttpMessageBuilderSupport sendOkJson(String json) {
        return server(scenarioEndpoint)
            .respond(HttpStatus.OK)
            .message()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(json);
    }

    /**
     * Send scenario {@code application/json} response operation from serialized {@link Object}.
     * @return
     */
    public HttpMessageBuilderSupport sendOkJson(Object jsonObject) {
        try {
            return sendOkJson(OBJECT_MAPPER.writeValueAsString(jsonObject));
        } catch (JsonProcessingException e) {
            throw new CitrusRuntimeException(e);
        }
    }
}
