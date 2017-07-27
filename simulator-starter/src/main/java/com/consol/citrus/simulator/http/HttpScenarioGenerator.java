package com.consol.citrus.simulator.http;

import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.exception.SimulatorException;
import io.swagger.models.*;
import io.swagger.parser.SwaggerParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;

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

    /** Simulator configuration */
    private SimulatorConfigurationProperties simulatorConfiguration;

    /** Optional context path */
    private String contextPath = "";

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
     * Gets the simulatorConfiguration.
     *
     * @return
     */
    public SimulatorConfigurationProperties getSimulatorConfiguration() {
        return simulatorConfiguration;
    }

    /**
     * Sets the simulatorConfiguration.
     *
     * @param simulatorConfiguration
     */
    public void setSimulatorConfiguration(SimulatorConfigurationProperties simulatorConfiguration) {
        this.simulatorConfiguration = simulatorConfiguration;
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
