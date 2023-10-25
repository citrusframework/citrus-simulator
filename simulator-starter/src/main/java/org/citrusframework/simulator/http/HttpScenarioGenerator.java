package org.citrusframework.simulator.http;

import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import org.citrusframework.simulator.exception.SimulatorException;
import org.citrusframework.spi.CitrusResourceWrapper;
import org.citrusframework.spi.Resource;
import org.citrusframework.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.Map;

/**
 * @author Christoph Deppisch
 */
public class HttpScenarioGenerator implements BeanFactoryPostProcessor {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(HttpScenarioGenerator.class);
    /**
     * Optional Swagger api file location system property for auto generated scenarios
     */
    private static final String SIMULATOR_SWAGGER_API_PROPERTY = "citrus.simulator.rest.swagger.api";
    private static final String SIMULATOR_SWAGGER_API_ENV = "CITRUS_SIMULATOR_REST_SWAGGER_API";
    private static final String SIMULATOR_SWAGGER_CONTEXT_PATH_PROPERTY = "citrus.simulator.rest.swagger.contextPath";
    private static final String SIMULATOR_SWAGGER_CONTEXT_PATH_ENV = "CITRUS_SIMULATOR_REST_SWAGGER_CONTEXT_PATH";
    /**
     * Target swagger API to generate scenarios from
     */
    private final Resource swaggerResource;
    /**
     * Optional context path
     */
    private String contextPath = "";

    /**
     * Constructor using Spring environment.
     */
    public HttpScenarioGenerator(Environment environment) {
        org.springframework.core.io.Resource springResource = new PathMatchingResourcePatternResolver().getResource(environment.getProperty(SIMULATOR_SWAGGER_API_PROPERTY, environment.getProperty(SIMULATOR_SWAGGER_API_ENV, "")));
        swaggerResource = new CitrusResourceWrapper(springResource);

        contextPath = environment.getProperty(SIMULATOR_SWAGGER_CONTEXT_PATH_PROPERTY, environment.getProperty(SIMULATOR_SWAGGER_CONTEXT_PATH_ENV, contextPath));
    }

    /**
     * Constructor using swagger API file resource.
     *
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

            Swagger swagger = new SwaggerParser().parse(FileUtils.readToString(swaggerResource));

            for (Map.Entry<String, Path> path : swagger.getPaths().entrySet()) {
                for (Map.Entry<io.swagger.models.HttpMethod, Operation> operation : path.getValue().getOperationMap().entrySet()) {

                    if (beanFactory instanceof BeanDefinitionRegistry) {
                        logger.info("Register auto generated scenario as bean definition: " + operation.getValue().getOperationId());
                        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(HttpOperationScenario.class)
                            .addConstructorArgValue((contextPath + (swagger.getBasePath() != null ? swagger.getBasePath() : "")) + path.getKey())
                            .addConstructorArgValue(RequestMethod.valueOf(operation.getKey().name()))
                            .addConstructorArgValue(operation.getValue())
                            .addConstructorArgValue(swagger.getDefinitions());

                        if (beanFactory.containsBeanDefinition("inboundJsonDataDictionary")) {
                            beanDefinitionBuilder.addPropertyReference("inboundDataDictionary", "inboundJsonDataDictionary");
                        }

                        if (beanFactory.containsBeanDefinition("outboundJsonDataDictionary")) {
                            beanDefinitionBuilder.addPropertyReference("outboundDataDictionary", "outboundJsonDataDictionary");
                        }

                        ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(operation.getValue().getOperationId(), beanDefinitionBuilder.getBeanDefinition());
                    } else {
                        logger.info("Register auto generated scenario as singleton: " + operation.getValue().getOperationId());
                        beanFactory.registerSingleton(operation.getValue().getOperationId(), createScenario((contextPath + (swagger.getBasePath() != null ? swagger.getBasePath() : "")) + path.getKey(), RequestMethod.valueOf(operation.getKey().name()), operation.getValue(), swagger.getDefinitions()));
                    }
                }
            }
        } catch (IOException e) {
            throw new SimulatorException("Failed to read swagger api resource", e);
        }
    }

    /**
     * Creates an HTTP scenario based on the given swagger path and operation information.
     *
     * @param path        Request path
     * @param method      Request method
     * @param operation   Swagger operation
     * @param definitions Additional definitions
     * @return a matching HTTP scenario
     */
    protected HttpOperationScenario createScenario(String path, RequestMethod method, Operation operation, Map<String, Model> definitions) {
        return new HttpOperationScenario(path, method, operation, definitions);
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
