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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.apicurio.datamodels.openapi.models.OasOperation;
import io.apicurio.datamodels.openapi.models.OasResponse;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.Getter;
import org.citrusframework.context.SpringBeanReferenceResolver;
import org.citrusframework.context.TestContext;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.exceptions.TestCaseFailedException;
import org.citrusframework.exceptions.ValidationException;
import org.citrusframework.functions.DefaultFunctionRegistry;
import org.citrusframework.http.actions.HttpServerResponseActionBuilder;
import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.log.DefaultLogModifier;
import org.citrusframework.message.Message;
import org.citrusframework.openapi.OpenApiRepository;
import org.citrusframework.openapi.actions.OpenApiClientResponseActionBuilder;
import org.citrusframework.openapi.model.OasModelHelper;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.http.HttpOperationScenarioIT.HttpOperationScenarioTestConfiguration;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.simulator.scenario.ScenarioEndpointConfiguration;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.spi.Resources.ClasspathResource;
import org.citrusframework.util.FileUtils;
import org.citrusframework.validation.DefaultMessageHeaderValidator;
import org.citrusframework.validation.DefaultMessageValidatorRegistry;
import org.citrusframework.validation.context.HeaderValidationContext;
import org.citrusframework.validation.json.JsonMessageValidationContext;
import org.citrusframework.validation.json.JsonTextMessageValidator;
import org.citrusframework.validation.matcher.DefaultValidationMatcherRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@IntegrationTest
@ContextConfiguration(classes = HttpOperationScenarioTestConfiguration.class)
class HttpOperationScenarioIT {

    private static final Function<String, String> IDENTITY = (text) -> text;

    private final DirectScenarioEndpoint scenarioEndpoint = new DirectScenarioEndpoint();

    private static DefaultListableBeanFactory defaultListableBeanFactory;

    private ScenarioRunner scenarioRunner;

    private TestContext testContext;

    @BeforeEach
    void beforeEach(ApplicationContext applicationContext) {
        defaultListableBeanFactory = (DefaultListableBeanFactory) ((ConfigurableApplicationContext)applicationContext).getBeanFactory();
        testContext = new TestContext();
        testContext.setReferenceResolver(new SpringBeanReferenceResolver(applicationContext));
        testContext.setMessageValidatorRegistry(new DefaultMessageValidatorRegistry());
        testContext.setFunctionRegistry(new DefaultFunctionRegistry());
        testContext.setValidationMatcherRegistry(new DefaultValidationMatcherRegistry());
        testContext.setLogModifier(new DefaultLogModifier());

        scenarioRunner = new ScenarioRunner(scenarioEndpoint, applicationContext, testContext);
    }

    static Stream<Arguments> scenarioExecution() {
        return Stream.of(
                arguments("v2_addPet_success", "POST_/petstore/v2/pet", "data/addPet.json", IDENTITY, null),
                arguments("v3_addPet_success", "POST_/petstore/v3/pet", "data/addPet.json", IDENTITY, null),
                arguments("v2_addPet_payloadValidationFailure", "POST_/petstore/v2/pet", "data/addPet_incorrect.json", IDENTITY, "OpenApi request validation failed for operation: /post/pet (addPet)\n"
                    + "\tERROR - Object instance has properties which are not allowed by the schema: [\"wrong_id_property\"]: []"),
                arguments("v3_addPet_payloadValidationFailure", "POST_/petstore/v3/pet", "data/addPet_incorrect.json", IDENTITY, "OpenApi request validation failed for operation: /post/pet (addPet)\n"
                    + "\tERROR - Object instance has properties which are not allowed by the schema: [\"wrong_id_property\"]: []"),
                arguments("v2_getPetById_success", "GET_/petstore/v2/pet/{petId}", null, (Function<String, String>)(text) -> text.replace("{petId}", "1234"), null),
                arguments("v3_getPetById_success", "GET_/petstore/v3/pet/{petId}", null, (Function<String, String>)(text) -> text.replace("{petId}", "1234"), null),
                arguments("v2_getPetById_pathParameterValidationFailure", "GET_/petstore/v2/pet/{petId}", null, (Function<String, String>)(text) -> text.replace("{petId}", "xxxx"), "OpenApi request validation failed for operation: /get/pet/{petId} (getPetById)\n"
                    + "\tERROR - Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"]): []"),
                arguments("v3_getPetById_pathParameterValidationFailure", "GET_/petstore/v3/pet/{petId}", null, (Function<String, String>)(text) -> text.replace("{petId}", "xxxx"), "OpenApi request validation failed for operation: /get/pet/{petId} (getPetById)\n"
                    + "\tERROR - Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"]): []")
            );
    }

    @ParameterizedTest(name="{0}")
    @MethodSource()
    void scenarioExecution(String name, String operationName, String payloadFile, Function<String, String> urlAdjuster, String exceptionMessage)
        throws IOException {
        if (defaultListableBeanFactory.containsSingleton("httpResponseActionBuilderProvider")) {
            defaultListableBeanFactory.destroySingleton("httpResponseActionBuilderProvider");
        }

        HttpOperationScenario httpOperationScenario = getHttpOperationScenario(operationName);
        HttpMessage controlMessage = new HttpMessage();
        OasResponse oasResponse = httpOperationScenario.determineResponse(null);
        OpenApiClientResponseActionBuilder.fillMessageFromResponse(httpOperationScenario.getOpenApiSpecification(),
            testContext, controlMessage, httpOperationScenario.getOperation(), oasResponse);

        this.scenarioExecution(operationName, payloadFile, urlAdjuster, exceptionMessage, controlMessage);
    }

    @ParameterizedTest(name="{0}_custom_payload")
    @MethodSource("scenarioExecution")
    void scenarioExecutionWithProvider(String name, String operationName, String payloadFile, Function<String, String> urlAdjuster, String exceptionMessage) {

        String payload = "{\"id\":1234}";
        HttpResponseActionBuilderProvider httpResponseActionBuilderProvider = (scenarioRunner, simulatorScenario, receivedMessage) -> {
            HttpServerResponseActionBuilder serverResponseActionBuilder = new HttpServerResponseActionBuilder();
            serverResponseActionBuilder
                .endpoint(scenarioEndpoint)
                .getMessageBuilderSupport()
                    .body(payload);
            return serverResponseActionBuilder;
        };

        if (!defaultListableBeanFactory.containsSingleton("httpResponseActionBuilderProvider")) {
            defaultListableBeanFactory.registerSingleton("httpResponseActionBuilderProvider",
                httpResponseActionBuilderProvider);
        }

        HttpOperationScenario httpOperationScenario = getHttpOperationScenario(operationName);
        try {
            ReflectionTestUtils.setField(httpOperationScenario, "httpResponseActionBuilderProvider",
                httpResponseActionBuilderProvider);

            HttpMessage correctPayloadMessage = new HttpMessage(payload);
            assertThatCode(() -> this.scenarioExecution(operationName, payloadFile, urlAdjuster, exceptionMessage,
                correctPayloadMessage)).doesNotThrowAnyException();

            if (exceptionMessage == null) {
                String otherPayload = "{\"id\":12345}";
                HttpMessage incorrectPayloadMessage = new HttpMessage(otherPayload);
                assertThatThrownBy(
                    () -> this.scenarioExecution(operationName, payloadFile, urlAdjuster,
                        exceptionMessage,
                        incorrectPayloadMessage)).isInstanceOf(CitrusRuntimeException.class);
            }
        } finally {
            ReflectionTestUtils.setField(httpOperationScenario, "httpResponseActionBuilderProvider",
                null);
        }
    }

    private void scenarioExecution(String operationName, String payloadFile, Function<String, String> urlAdjuster, String exceptionMessage, Message controlMessage)
        throws IOException {
        HttpOperationScenario httpOperationScenario = getHttpOperationScenario(operationName);
        OasOperation oasOperation = httpOperationScenario.getOperation();

        String payload = payloadFile != null ? FileUtils.readToString(new ClasspathResource(payloadFile)) : null;

        Message receiveMessage = new HttpMessage()
            .setPayload(payload)
            .setHeader("citrus_http_request_uri", urlAdjuster.apply(httpOperationScenario.getPath()))
            .setHeader("citrus_http_method", httpOperationScenario.getMethod().toUpperCase());

        OasModelHelper.getRequestContentType(oasOperation)
            .ifPresent(contentType -> receiveMessage.setHeader(HttpHeaders.CONTENT_TYPE, contentType));

        scenarioEndpoint.setReceiveMessage(receiveMessage);

        ReflectionTestUtils.setField(httpOperationScenario, "scenarioEndpoint",
            scenarioEndpoint);

        if (exceptionMessage != null) {
            assertThatThrownBy(() -> httpOperationScenario.run(scenarioRunner)).isInstanceOf(
                TestCaseFailedException.class).cause().isInstanceOf(ValidationException.class).hasMessage(exceptionMessage);
        } else {
            assertThatCode(() -> httpOperationScenario.run(scenarioRunner)).doesNotThrowAnyException();

            Message sendMessage = scenarioEndpoint.getSendMessage();

            JsonTextMessageValidator jsonTextMessageValidator = new JsonTextMessageValidator();
            jsonTextMessageValidator.validateMessage(sendMessage, controlMessage, testContext,
                List.of(new JsonMessageValidationContext()));
            DefaultMessageHeaderValidator defaultMessageHeaderValidator = new DefaultMessageHeaderValidator();
            defaultMessageHeaderValidator.validateMessage(sendMessage, controlMessage, testContext, List.of(new HeaderValidationContext()));
        }

    }

    private HttpOperationScenario getHttpOperationScenario(String operationName) {
        Object bean = defaultListableBeanFactory.getBean(operationName);

        assertThat(bean).isInstanceOf(HttpOperationScenario.class);

        return  (HttpOperationScenario) bean;
    }

    private static class DirectScenarioEndpoint extends ScenarioEndpoint {

        private Message receiveMessage;

        @Getter
        private Message sendMessage;

        public DirectScenarioEndpoint() {
            super(new ScenarioEndpointConfiguration());
        }

        @Override
        public void send(Message message, TestContext context) {
            this.sendMessage = new HttpMessage(message);

            if (sendMessage.getPayload() instanceof String stringPayload) {
                this.sendMessage.setPayload(
                    context.replaceDynamicContentInString(stringPayload));
            }
        }

        @Override
        public Message receive(TestContext context) {
            return receiveMessage;
        }

        @Override
        public Message receive(TestContext context, long timeout) {
            return receiveMessage;
        }

        public void setReceiveMessage(Message receiveMessage) {
            this.receiveMessage = receiveMessage;
        }

    }

    @TestConfiguration
    public static class HttpOperationScenarioTestConfiguration {

        @Bean
        public OpenApiRepository repository() {
            OpenApiRepository openApiRepository = new OpenApiRepository();
            openApiRepository.setLocations(List.of("swagger/petstore-v2.json", "swagger/petstore-v3.json"));
            return openApiRepository;
        }
    }
}
