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

import static java.lang.String.format;
import static org.citrusframework.actions.EchoAction.Builder.echo;

import io.apicurio.datamodels.openapi.models.OasDocument;
import io.apicurio.datamodels.openapi.models.OasOperation;
import io.apicurio.datamodels.openapi.models.OasResponse;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import org.citrusframework.http.actions.HttpServerResponseActionBuilder;
import org.citrusframework.message.Message;
import org.citrusframework.message.MessageHeaders;
import org.citrusframework.openapi.OpenApiSpecification;
import org.citrusframework.openapi.actions.OpenApiActionBuilder;
import org.citrusframework.openapi.actions.OpenApiServerActionBuilder;
import org.citrusframework.openapi.actions.OpenApiServerRequestActionBuilder;
import org.citrusframework.openapi.model.OasModelHelper;
import org.citrusframework.simulator.config.OpenApiScenarioIdGenerationMode;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.variable.dictionary.json.JsonPathMappingDataDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@Getter
public class HttpOperationScenario extends AbstractSimulatorScenario {

    private static final Logger logger = LoggerFactory.getLogger(HttpOperationScenario.class);

    private final String path;

    private final String scenarioId;

    private final OpenApiSpecification openApiSpecification;

    private final OasOperation operation;

    private OasResponse response;

    private HttpStatus statusCode = HttpStatus.OK;

    private JsonPathMappingDataDictionary inboundDataDictionary;

    private JsonPathMappingDataDictionary outboundDataDictionary;

    private final HttpResponseActionBuilderProvider httpResponseActionBuilderProvider;

    public HttpOperationScenario(String path, String scenarioId, OpenApiSpecification openApiSpecification, OasOperation operation, HttpResponseActionBuilderProvider httpResponseActionBuilderProvider) {
        this.path = path;
        this.scenarioId = scenarioId;
        this.openApiSpecification = openApiSpecification;
        this.operation = operation;
        this.httpResponseActionBuilderProvider = httpResponseActionBuilderProvider;

        // Note, that in case of an absent response, an OK response will be sent. This is to maintain backwards compatibility with previous swagger implementation.
        // Also, the petstore api lacks the definition of good responses for several operations
        this.response = OasModelHelper.getResponseForRandomGeneration(getOasDocument(), operation).orElse(null);
    }

    @Override
    public void run(ScenarioRunner scenario) {
        scenario.name(operation.operationId);
        scenario.$(echo("Generated scenario from swagger operation: " + operation.operationId));

        OpenApiServerActionBuilder openApiServerActionBuilder = new OpenApiActionBuilder(
            openApiSpecification).server(getScenarioEndpoint());

        Message receivedMessage = receive(scenario, openApiServerActionBuilder);
        respond(scenario, openApiServerActionBuilder, receivedMessage);
    }

    private Message receive(ScenarioRunner scenario,
        OpenApiServerActionBuilder openApiServerActionBuilder) {

        OpenApiServerRequestActionBuilder requestActionBuilder = openApiServerActionBuilder.receive(
            operation.operationId);

        requestActionBuilder
            .message()
            .header(MessageHeaders.MESSAGE_PREFIX + "generated", true);

        if (operation.getParameters() != null && inboundDataDictionary != null) {
            requestActionBuilder.message().dictionary(inboundDataDictionary);
        }

        AtomicReference<Message> receivedMessage = new AtomicReference<>();
        requestActionBuilder.getMessageProcessors().add(
            (message, context) -> receivedMessage.set(message));

        // Verify incoming request
        scenario.$(requestActionBuilder);

        return receivedMessage.get();
    }

    private void respond(ScenarioRunner scenario,
        OpenApiServerActionBuilder openApiServerActionBuilder, Message receivedMessage) {

        HttpServerResponseActionBuilder responseBuilder = null;
        if (httpResponseActionBuilderProvider != null) {
            responseBuilder = httpResponseActionBuilderProvider.provideHttpServerResponseActionBuilder(operation, receivedMessage);
        }

        HttpStatus httpStatus = response != null && response.getStatusCode() != null ? HttpStatus.valueOf(Integer.parseInt(response.getStatusCode())) : HttpStatus.OK;
        responseBuilder = responseBuilder != null ? responseBuilder : openApiServerActionBuilder.send(
            operation.operationId, httpStatus);

        responseBuilder.message()
            .status(httpStatus)
            .header(MessageHeaders.MESSAGE_PREFIX + "generated", true);

        // Return generated response
        scenario.$(responseBuilder);
    }

    /**
     * Gets the document.
     *
     * @return
     */
    public OasDocument getOasDocument() {
        return openApiSpecification.getOpenApiDoc(null);
    }

    public String getMethod() {
        return operation.getMethod() != null ? operation.getMethod().toUpperCase() : null;
    }

    /**
     * Sets the response.
     *
     * @param response
     */
    public void setResponse(OasResponse response) {
        this.response = response;
    }

    /**
     * Sets the statusCode.
     *
     * @param statusCode
     */
    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Sets the inboundDataDictionary.
     *
     * @param inboundDataDictionary
     */
    public void setInboundDataDictionary(JsonPathMappingDataDictionary inboundDataDictionary) {
        this.inboundDataDictionary = inboundDataDictionary;
    }

    /**
     * Sets the outboundDataDictionary.
     *
     * @param outboundDataDictionary
     */
    public void setOutboundDataDictionary(JsonPathMappingDataDictionary outboundDataDictionary) {
        this.outboundDataDictionary = outboundDataDictionary;
    }

    /**
     * Retrieve a unique scenario id for the oas operation.
     *
     * @param openApiScenarioIdGenerationMode
     * @param path
     * @param oasOperation
     * @return
     */
    public static String getUniqueScenarioId(
        OpenApiScenarioIdGenerationMode openApiScenarioIdGenerationMode, String path, OasOperation oasOperation) {

        return switch(openApiScenarioIdGenerationMode)  {
            case OPERATION_ID -> oasOperation.operationId;
            case FULL_PATH -> format("%s_%s", oasOperation.getMethod().toUpperCase(), path);
        };
    }
}
