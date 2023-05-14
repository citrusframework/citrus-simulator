package org.citrusframework.simulator.http;

import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.RefModel;
import io.swagger.models.Response;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.HeaderParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.DateProperty;
import io.swagger.models.properties.DateTimeProperty;
import io.swagger.models.properties.DoubleProperty;
import io.swagger.models.properties.FloatProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.LongProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;
import org.citrusframework.http.actions.HttpServerRequestActionBuilder;
import org.citrusframework.http.actions.HttpServerResponseActionBuilder;
import org.citrusframework.http.message.HttpMessageHeaders;
import org.citrusframework.message.MessageHeaders;
import org.citrusframework.simulator.exception.SimulatorException;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.ScenarioDesigner;
import org.citrusframework.variable.dictionary.json.JsonPathMappingDataDictionary;
import org.hamcrest.CustomMatcher;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.HEAD;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

/**
 * @author Christoph Deppisch
 */
public class HttpOperationScenario extends AbstractSimulatorScenario {

    /** Operation in wsdl */
    private final Operation operation;

    /** Schema model definitions */
    private final Map<String, Model> definitions;

    /** Request path */
    private final String path;

    /** Request method */
    private final HttpMethod method;

    /** Response */
    private Response response;

    /** Response status code */
    private HttpStatus statusCode = HttpStatus.OK;

    private JsonPathMappingDataDictionary inboundDataDictionary;
    private JsonPathMappingDataDictionary outboundDataDictionary;

    /**
     * Default constructor.
     * @param path
     * @param method
     * @param operation
     * @param definitions
     */
    public HttpOperationScenario(String path, HttpMethod method, Operation operation, Map<String, Model> definitions) {
        this.operation = operation;
        this.definitions = definitions;
        this.path = path;
        this.method = method;

        if (operation.getResponses() != null) {
            this.response = operation.getResponses().get("200");
        }
    }

    @Override
    public void run(ScenarioDesigner scenario) {
        scenario.name(operation.getOperationId());

        HttpServerRequestActionBuilder requestBuilder;
        if (method.equals(GET)) {
            requestBuilder = scenario
                    .http()
                    .receive()
                    .get();
        } else if (method.equals(POST)) {
            requestBuilder = scenario
                    .http()
                    .receive()
                    .post();
        } else if (method.equals(PUT)) {
            requestBuilder = scenario
                    .http()
                    .receive()
                    .put();
        } else if (method.equals(HEAD)) {
            requestBuilder = scenario
                    .http()
                    .receive()
                    .head();
        } else if (method.equals(DELETE)) {
            requestBuilder = scenario
                    .http()
                    .receive()
                    .delete();
        } else {
            throw new SimulatorException("Unsupported request method: " + method.name());
        }

        requestBuilder
            // TODO: .messageType(MessageType.JSON)
            .getMessageBuilderSupport()
            .header(MessageHeaders.MESSAGE_PREFIX + "generated", true);

        requestBuilder
                .getMessageBuilderSupport()
                .header(HttpMessageHeaders.HTTP_REQUEST_URI, new CustomMatcher<String>(String.format("request path matching %s", path)) {
                @Override
                public boolean matches(Object item) {
                    return ((item instanceof String) && new AntPathMatcher().match(path, (String) item));
                }
            });

        if (operation.getParameters() != null) {
            operation.getParameters().stream()
                    .filter(p -> p instanceof HeaderParameter)
                    .filter(Parameter::getRequired)
                    .forEach(p -> requestBuilder.getMessageBuilderSupport().header(p.getName(), createValidationExpression(((HeaderParameter) p))));

            String queryParams = operation.getParameters().stream()
                    .filter(param -> param instanceof QueryParameter)
                    .filter(Parameter::getRequired)
                    .map(param -> "containsString(" + param.getName() + ")")
                    .collect(Collectors.joining(", "));

            if (StringUtils.hasText(queryParams)) {
                requestBuilder.getMessageBuilderSupport().header(HttpMessageHeaders.HTTP_QUERY_PARAMS, "@assertThat(allOf(" + queryParams + "))@");
            }

            operation.getParameters().stream()
                    .filter(p -> p instanceof BodyParameter)
                    .filter(Parameter::getRequired)
                    .forEach(p -> requestBuilder.getMessageBuilderSupport().body(createValidationPayload((BodyParameter) p)));

            if (inboundDataDictionary != null) {
                requestBuilder.getMessageBuilderSupport().dictionary(inboundDataDictionary);
            }
        }

        HttpServerResponseActionBuilder.HttpMessageBuilderSupport responseBuilder = scenario
            .http()
            .send()
            .response(statusCode)
            // TODO: .messageType(MessageType.JSON)
            .getMessageBuilderSupport()
            .header(MessageHeaders.MESSAGE_PREFIX + "generated", true)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        if (response != null) {
            if (response.getHeaders() != null) {
                for (Map.Entry<String, Property> header : response.getHeaders().entrySet()) {
                    responseBuilder.header(header.getKey(), createRandomValue(header.getValue(), false));
                }
            }

            if (response.getSchema() != null) {
                if (outboundDataDictionary != null &&
                        (response.getSchema() instanceof RefProperty || response.getSchema() instanceof ArrayProperty)) {
                    responseBuilder.dictionary(outboundDataDictionary);
                }

                responseBuilder.body(createRandomValue(response.getSchema(), false));
            }
        }
    }

    /**
     * Create payload from schema with random values.
     * @param property
     * @param quotes
     * @return
     */
    private String createRandomValue(Property property, boolean quotes) {
        StringBuilder payload = new StringBuilder();
        if (property instanceof RefProperty) {
            Model model = definitions.get(((RefProperty) property).getSimpleRef());
            payload.append("{");

            if (model.getProperties() != null) {
                for (Map.Entry<String, Property> entry : model.getProperties().entrySet()) {
                    payload.append("\"").append(entry.getKey()).append("\": ").append(createRandomValue(entry.getValue(), true)).append(",");
                }
            }

            if (payload.toString().endsWith(",")) {
                payload.replace(payload.length() - 1, payload.length(), "");
            }

            payload.append("}");
        } else if (property instanceof ArrayProperty) {
            payload.append("[");
            payload.append(createRandomValue(((ArrayProperty) property).getItems(), true));
            payload.append("]");
        } else if (property instanceof StringProperty || property instanceof DateProperty || property instanceof DateTimeProperty) {
            if (quotes) {
                payload.append("\"");
            }

            if (property instanceof DateProperty) {
                payload.append("citrus:currentDate()");
            } else if (property instanceof DateTimeProperty) {
                payload.append("citrus:currentDate('yyyy-MM-dd'T'hh:mm:ss')");
            } else if (!CollectionUtils.isEmpty(((StringProperty) property).getEnum())) {
                payload.append("citrus:randomEnumValue(").append(((StringProperty) property).getEnum().stream().map(value -> "'" + value + "'").collect(Collectors.joining(","))).append(")");
            } else {
                payload.append("citrus:randomString(").append(((StringProperty) property).getMaxLength() != null && ((StringProperty) property).getMaxLength() > 0 ? ((StringProperty) property).getMaxLength() : (((StringProperty) property).getMinLength() != null && ((StringProperty) property).getMinLength() > 0 ? ((StringProperty) property).getMinLength() : 10)).append(")");
            }

            if (quotes) {
                payload.append("\"");
            }
        } else if (property instanceof IntegerProperty || property instanceof LongProperty) {
            payload.append("citrus:randomNumber(10)");
        } else if (property instanceof FloatProperty || property instanceof DoubleProperty) {
            payload.append("citrus:randomNumber(10)");
        } else if (property instanceof BooleanProperty) {
            payload.append("citrus:randomEnumValue('true', 'false')");
        } else {
            if (quotes) {
                payload.append("\"\"");
            } else {
                payload.append("");
            }
        }

        return payload.toString();
    }

    /**
     * Creates control payload for validation.
     * @param parameter
     * @return
     */
    private String createValidationPayload(BodyParameter parameter) {
        StringBuilder payload = new StringBuilder();

        Model model = parameter.getSchema();

        if (model instanceof RefModel) {
            model = definitions.get(((RefModel) model).getSimpleRef());
        }

        if (model instanceof ArrayModel) {
            payload.append("[");
            payload.append(createValidationExpression(((ArrayModel) model).getItems()));
            payload.append("]");
        } else {

            payload.append("{");

            if (model.getProperties() != null) {
                for (Map.Entry<String, Property> entry : model.getProperties().entrySet()) {
                    payload.append("\"").append(entry.getKey()).append("\": ").append(createValidationExpression(entry.getValue())).append(",");
                }
            }

            if (payload.toString().endsWith(",")) {
                payload.replace(payload.length() - 1, payload.length(), "");
            }

            payload.append("}");
        }

        return payload.toString();
    }

    /**
     * Create validation expression using functions according to parameter type and format.
     * @param property
     * @return
     */
    private String createValidationExpression(Property property) {
        StringBuilder payload = new StringBuilder();
        if (property instanceof RefProperty) {
            Model model = definitions.get(((RefProperty) property).getSimpleRef());
            payload.append("{");

            if (model.getProperties() != null) {
                for (Map.Entry<String, Property> entry : model.getProperties().entrySet()) {
                    payload.append("\"").append(entry.getKey()).append("\": ").append(createValidationExpression(entry.getValue())).append(",");
                }
            }

            if (payload.toString().endsWith(",")) {
                payload.replace(payload.length() - 1, payload.length(), "");
            }

            payload.append("}");
        } else if (property instanceof ArrayProperty) {
            payload.append("\"@ignore@\"");
        } else if (property instanceof StringProperty) {
            if (StringUtils.hasText(((StringProperty) property).getPattern())) {
                payload.append("\"@matches(").append(((StringProperty) property).getPattern()).append(")@\"");
            } else if (!CollectionUtils.isEmpty(((StringProperty) property).getEnum())) {
                payload.append("\"@matches(").append(((StringProperty) property).getEnum().stream().collect(Collectors.joining("|"))).append(")@\"");
            } else {
                payload.append("\"@notEmpty()@\"");
            }
        } else if (property instanceof DateProperty) {
            payload.append("\"@matchesDatePattern('yyyy-MM-dd')@\"");
        } else if (property instanceof DateTimeProperty) {
            payload.append("\"@matchesDatePattern('yyyy-MM-dd'T'hh:mm:ss')@\"");
        } else if (property instanceof IntegerProperty || property instanceof LongProperty) {
            payload.append("\"@isNumber()@\"");
        } else if (property instanceof FloatProperty || property instanceof DoubleProperty) {
            payload.append("\"@isNumber()@\"");
        } else if (property instanceof BooleanProperty) {
            payload.append("\"@matches(true|false)@\"");
        } else {
            payload.append("\"@ignore@\"");
        }

        return payload.toString();
    }

    /**
     * Create validation expression using functions according to parameter type and format.
     * @param parameter
     * @return
     */
    private String createValidationExpression(AbstractSerializableParameter parameter) {
        switch (parameter.getType()) {
            case "integer":
                return "@isNumber()@";
            case "string":
                if (parameter.getFormat() != null && parameter.getFormat().equals("date")) {
                    return "\"@matchesDatePattern('yyyy-MM-dd')@\"";
                } else if (parameter.getFormat() != null && parameter.getFormat().equals("date-time")) {
                    return "\"@matchesDatePattern('yyyy-MM-dd'T'hh:mm:ss')@\"";
                } else if (StringUtils.hasText(parameter.getPattern())) {
                    return "\"@matches(" + parameter.getPattern() + ")@\"";
                } else if (!CollectionUtils.isEmpty(parameter.getEnum())) {
                    return "\"@matches(" + (parameter.getEnum().stream().collect(Collectors.joining("|"))) + ")@\"";
                } else {
                    return "@notEmpty()@";
                }
            case "boolean":
                return "@matches(true|false)@";
            default:
                return "@ignore@";
        }
    }

    /**
     * Gets the operation.
     *
     * @return
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * Gets the path.
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the method.
     *
     * @return
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * Gets the response.
     *
     * @return
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Sets the response.
     *
     * @param response
     */
    public void setResponse(Response response) {
        this.response = response;
    }

    /**
     * Gets the statusCode.
     *
     * @return
     */
    public HttpStatus getStatusCode() {
        return statusCode;
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
     * Gets the inboundDataDictionary.
     *
     * @return
     */
    public JsonPathMappingDataDictionary getInboundDataDictionary() {
        return inboundDataDictionary;
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
     * Gets the outboundDataDictionary.
     *
     * @return
     */
    public JsonPathMappingDataDictionary getOutboundDataDictionary() {
        return outboundDataDictionary;
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
