package org.citrusframework.simulator.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import io.apicurio.datamodels.openapi.models.OasOperation;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.citrusframework.context.TestContext;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.exceptions.TestCaseFailedException;
import org.citrusframework.functions.DefaultFunctionRegistry;
import org.citrusframework.http.actions.HttpServerResponseActionBuilder;
import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.log.DefaultLogModifier;
import org.citrusframework.message.Message;
import org.citrusframework.openapi.OpenApiRepository;
import org.citrusframework.openapi.actions.OpenApiClientResponseActionBuilder;
import org.citrusframework.openapi.model.OasModelHelper;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class HttpOperationScenarioIT {

    private static final Function<String, String> IDENTITY = (text) -> text;

    private final DirectScenarioEndpoint scenarioEndpoint = new DirectScenarioEndpoint();

    private static final OpenApiRepository openApiRepository = new OpenApiRepository();

    private static DefaultListableBeanFactory defaultListableBeanFactory;

    private ScenarioRunner scenarioRunner;

    private TestContext testContext;

    @BeforeAll
    static void beforeAll() {
        ConfigurableApplicationContext applicationContext = mock();
        defaultListableBeanFactory = new DefaultListableBeanFactory();
        doReturn(defaultListableBeanFactory).when(applicationContext).getBeanFactory();
        SimulatorConfigurationProperties simulatorConfigurationProperties = new SimulatorConfigurationProperties();
        simulatorConfigurationProperties.setApplicationContext(applicationContext);

        defaultListableBeanFactory.registerSingleton("SimulatorRestConfigurationProperties", new SimulatorRestConfigurationProperties());

        openApiRepository.addRepository(new ClasspathResource("swagger/petstore-v2.json"));
        openApiRepository.addRepository(new ClasspathResource("swagger/petstore-v3.json"));
    }

    @BeforeEach
    void beforeEach() {
        testContext = new TestContext();
        testContext.setReferenceResolver(mock());
        testContext.setMessageValidatorRegistry(new DefaultMessageValidatorRegistry());
        testContext.setFunctionRegistry(new DefaultFunctionRegistry());
        testContext.setValidationMatcherRegistry(new DefaultValidationMatcherRegistry());
        testContext.setLogModifier(new DefaultLogModifier());
        scenarioRunner = new ScenarioRunner(scenarioEndpoint, mock(), testContext);
    }

    static Stream<Arguments> scenarioExecution() {
        return Stream.of(
                arguments("v2_addPet_success", "POST_/petstore/v2/pet", "data/addPet.json", IDENTITY, null),
                arguments("v3_addPet_success", "POST_/petstore/v3/pet", "data/addPet.json", IDENTITY, null),
                arguments("v2_addPet_payloadValidationFailure", "POST_/petstore/v2/pet", "data/addPet_incorrect.json", IDENTITY, "Missing JSON entry, expected 'id' to be in '[photoUrls, wrong_id_property, name, category, tags, status]'"),
                arguments("v3_addPet_payloadValidationFailure", "POST_/petstore/v3/pet", "data/addPet_incorrect.json", IDENTITY, "Missing JSON entry, expected 'id' to be in '[photoUrls, wrong_id_property, name, category, tags, status]'"),
                arguments("v2_getPetById_success", "GET_/petstore/v2/pet/{petId}", null, (Function<String, String>)(text) -> text.replace("{petId}", "1234"), null),
                arguments("v3_getPetById_success", "GET_/petstore/v3/pet/{petId}", null, (Function<String, String>)(text) -> text.replace("{petId}", "1234"), null),
                arguments("v2_getPetById_pathParameterValidationFailure", "GET_/petstore/v2/pet/{petId}", null, (Function<String, String>)(text) -> text.replace("{petId}", "xxxx"), "MatchesValidationMatcher failed for field 'citrus_http_request_uri'. Received value is '/petstore/v2/pet/xxxx', control value is '/petstore/v2/pet/[0-9]+'"),
                arguments("v3_getPetById_pathParameterValidationFailure", "GET_/petstore/v3/pet/{petId}", null, (Function<String, String>)(text) -> text.replace("{petId}", "xxxx"), "MatchesValidationMatcher failed for field 'citrus_http_request_uri'. Received value is '/petstore/v3/pet/xxxx', control value is '/petstore/v3/pet/[0-9]+'")
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
        OpenApiClientResponseActionBuilder.fillMessageFromResponse(httpOperationScenario.getOpenApiSpecification(), testContext, controlMessage, httpOperationScenario.getOperation(), httpOperationScenario.getResponse());

        this.scenarioExecution(operationName, payloadFile, urlAdjuster, exceptionMessage, controlMessage);
    }

    @ParameterizedTest(name="{0}_custom_payload")
    @MethodSource("scenarioExecution")
    void scenarioExecutionWithProvider(String name, String operationName, String payloadFile, Function<String, String> urlAdjuster, String exceptionMessage) {

        String payload = "{\"id\":1234}";
        HttpResponseActionBuilderProvider httpResponseActionBuilderProvider = (oasOperation, receivedMessage) -> {
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
                TestCaseFailedException.class).hasMessage(exceptionMessage);
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

        public Message getSendMessage() {
            return sendMessage;
        }

    }
}
