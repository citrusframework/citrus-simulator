package com.consol.citrus.simulator.http;

import com.consol.citrus.simulator.exception.SimulatorException;
import io.swagger.models.*;
import io.swagger.parser.SwaggerParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Map;

/**
 * @author Christoph Deppisch
 */
public class HttpScenarioGenerator implements BeanFactoryPostProcessor {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(HttpScenarioGenerator.class);

    /** Target swagger API to generate scenarios from */
    private final Resource swaggerResource;

    /** Optional context path */
    private String contextPath = "";

    /** Optional Swagger api file location system property for auto generated scenarios */
    private static final String SIMULATOR_SWAGGER_API_PROPERTY = "citrus.simulator.rest.swagger.api";
    private static final String SIMULATOR_SWAGGER_API_ENV = "CITRUS_SIMULATOR_REST_SWAGGER_API";

    private static final String SIMULATOR_SWAGGER_CONTEXT_PATH_PROPERTY = "citrus.simulator.rest.swagger.contextPath";
    private static final String SIMULATOR_SWAGGER_CONTEXT_PATH_ENV = "CITRUS_SIMULATOR_REST_SWAGGER_CONTEXT_PATH";

    /**
     * Constructor using Spring environment.
     */
    public HttpScenarioGenerator(Environment environment) {
        swaggerResource = new PathMatchingResourcePatternResolver().getResource(environment.getProperty(SIMULATOR_SWAGGER_API_PROPERTY, environment.getProperty(SIMULATOR_SWAGGER_API_ENV, "")));
        contextPath = environment.getProperty(SIMULATOR_SWAGGER_CONTEXT_PATH_PROPERTY, environment.getProperty(SIMULATOR_SWAGGER_CONTEXT_PATH_ENV, contextPath));
    }

    /**
     * Constructor using swagger API file resource.
     * @param swaggerResource
     */
    public HttpScenarioGenerator(Resource swaggerResource) {
        this.swaggerResource = swaggerResource;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            Assert.notNull(swaggerResource,
                    "Missing either swagger api system property setting or explicit swagger api resource for scenario auto generation");

            Swagger swagger = new SwaggerParser().read(swaggerResource.getURI().toURL().toString());

            for (Map.Entry<String, Path> path : swagger.getPaths().entrySet()) {
                for (Map.Entry<io.swagger.models.HttpMethod, Operation> operation : path.getValue().getOperationMap().entrySet()) {
                    log.info("Register auto generated scenario: " + operation.getValue().getOperationId());

                    beanFactory.registerSingleton(operation.getValue().getOperationId(), createScenario((contextPath + (swagger.getBasePath() != null ? swagger.getBasePath() : "")) + path.getKey(), HttpMethod.valueOf(operation.getKey().name()), operation.getValue(), swagger.getDefinitions()));
                }
            }
        } catch (IOException e) {
            throw new SimulatorException("Failed to read swagger api resource", e);
        }
    }

    /**
     * Creates the scenario with given swagger path and operation information.
     * @param path
     * @param method
     * @param operation
     * @param definitions
     * @return
     */
    protected HttpOperationScenario createScenario(String path, HttpMethod method, Operation operation, Map<String, Model> definitions) {
        return new HttpOperationScenario(path, method, operation, definitions);
    }

    /**
     * Gets the contextPath.
     *
     * @return
     */
    public String getContextPath() {
        return contextPath;
    }

    /**
     * Sets the contextPath.
     *
     * @param contextPath
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
