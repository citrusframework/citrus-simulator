package org.citrusframework.simulator.config;

/**
 * Enumeration representing the modes for generating scenario IDs in an OpenAPI context.
 * This enumeration defines two modes:
 * <ul>
 *     <li>{@link #OPERATION_ID}: Uses the operation ID defined in the OpenAPI specification.</li>
 *     <li>{@link #FULL_PATH}: Uses the full path of the API endpoint.</li>
 * </ul>
 * The choice of mode affects how scenario IDs are generated, with important implications:
 *  <ul>
 *      <li><b>OPERATION_ID:</b> This mode relies on the {@code operationId} field in the OpenAPI specification, which
 *      provides a unique identifier for each operation. However, the {@code operationId} is not mandatory in the OpenAPI
 *      specification. If an {@code operationId} is not specified, this mode cannot be used effectively.</li>
 *      <li><b>FULL_PATH:</b> This mode constructs scenario IDs based on the entire URL path of the API endpoint, including
 *      path parameters. This is particularly useful when simulating multiple versions of the same API, as it allows for
 *      differentiation based on the endpoint path. This mode ensures unique scenario IDs even when {@code operationId}
 *      is not available or when versioning of APIs needs to be distinguished.</li>
 *  </ul>
 *  </p>
 */
public enum OpenApiScenarioIdGenerationMode {
    FULL_PATH,
    OPERATION_ID
}
