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

import static org.citrusframework.actions.EchoAction.Builder.echo;

import io.apicurio.datamodels.openapi.models.OasDocument;
import io.apicurio.datamodels.openapi.models.OasOperation;
import io.apicurio.datamodels.openapi.models.OasResponse;
import jakarta.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import org.citrusframework.http.actions.HttpServerResponseActionBuilder;
import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.message.MessageHeaders;
import org.citrusframework.openapi.OpenApiSpecification;
import org.citrusframework.openapi.actions.OpenApiActionBuilder;
import org.citrusframework.openapi.actions.OpenApiServerActionBuilder;
import org.citrusframework.openapi.actions.OpenApiServerRequestActionBuilder;
import org.citrusframework.openapi.model.OasModelHelper;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.variable.dictionary.json.JsonPathMappingDataDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@Getter
public class HttpOperationScenario extends AbstractSimulatorScenario implements HttpScenario {

    private static final Logger logger = LoggerFactory.getLogger(HttpOperationScenario.class);

    private final String path;

    private final String scenarioId;

    private final OpenApiSpecification openApiSpecification;

    private final OasOperation operation;

    private JsonPathMappingDataDictionary inboundDataDictionary;

    private JsonPathMappingDataDictionary outboundDataDictionary;

    private final HttpResponseActionBuilderProvider httpResponseActionBuilderProvider;

    public HttpOperationScenario(String path, String scenarioId, OpenApiSpecification openApiSpecification, OasOperation operation, HttpResponseActionBuilderProvider httpResponseActionBuilderProvider) {
        this.path = path;
        this.scenarioId = scenarioId;
        this.openApiSpecification = openApiSpecification;
        this.operation = operation;
        this.httpResponseActionBuilderProvider = httpResponseActionBuilderProvider;
    }

    @Override
    public void run(ScenarioRunner scenario) {
        scenario.name(operation.operationId);
        scenario.$(echo("Generated scenario from swagger operation: " + operation.operationId));

        OpenApiServerActionBuilder openApiServerActionBuilder = new OpenApiActionBuilder(
            openApiSpecification).server(getScenarioEndpoint());

        HttpMessage receivedMessage = receive(scenario, openApiServerActionBuilder);
        respond(scenario, openApiServerActionBuilder, receivedMessage);
    }

    private HttpMessage receive(ScenarioRunner scenarioRunner,
        OpenApiServerActionBuilder openApiServerActionBuilder) {

        OpenApiServerRequestActionBuilder requestActionBuilder = openApiServerActionBuilder.receive(
            operation.operationId);

        requestActionBuilder
            .message()
            .header(MessageHeaders.MESSAGE_PREFIX + "generated", true);

        if (operation.getParameters() != null && inboundDataDictionary != null) {
            requestActionBuilder.message().dictionary(inboundDataDictionary);
        }

        AtomicReference<HttpMessage> receivedMessage = new AtomicReference<>();
        requestActionBuilder.getMessageProcessors().add(
            (message, context) -> receivedMessage.set((HttpMessage)message));

        // Verify incoming request
        scenarioRunner.$(requestActionBuilder);

        return receivedMessage.get();
    }

    private void respond(ScenarioRunner scenarioRunner,
        OpenApiServerActionBuilder openApiServerActionBuilder, HttpMessage receivedMessage) {

        HttpServerResponseActionBuilder responseBuilder = null;
        if (httpResponseActionBuilderProvider != null) {
            responseBuilder = httpResponseActionBuilderProvider.provideHttpServerResponseActionBuilder(scenarioRunner, this, receivedMessage);
        }

        if (responseBuilder == null) {
            responseBuilder = createRandomMessageResponseBuilder(openApiServerActionBuilder, receivedMessage.getAccept());
        }

        scenarioRunner.$(responseBuilder);
    }

    /**
     * Creates a builder that creates a random message based on OpenApi specification.
     * @param openApiServerActionBuilder
     * @return
     */
    private HttpServerResponseActionBuilder createRandomMessageResponseBuilder(
        OpenApiServerActionBuilder openApiServerActionBuilder, String accept) {
        HttpServerResponseActionBuilder responseBuilder;

        OasResponse response = determineResponse(accept);

        HttpStatus httpStatus = getStatusFromResponseOrDefault(response);
        responseBuilder = openApiServerActionBuilder.send(operation.operationId, httpStatus, accept);
        responseBuilder.message()
            .status(httpStatus)
            .header(MessageHeaders.MESSAGE_PREFIX + "generated", true);

        return responseBuilder;
    }

    private HttpStatus getStatusFromResponseOrDefault(@Nullable OasResponse response) {
        return response != null && response.getStatusCode() != null ? HttpStatus.valueOf(
            Integer.parseInt(response.getStatusCode())) : HttpStatus.OK;
    }

    OasResponse determineResponse(String accept) {
        return OasModelHelper.getResponseForRandomGeneration(getOasDocument(), operation, null, accept).orElse(null);
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

}
