/*
 * Copyright 2024 the original author or authors.
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

import static org.citrusframework.util.FileUtils.readToString;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import java.io.IOException;
import java.util.Map;
import org.citrusframework.simulator.exception.SimulatorException;
import org.citrusframework.spi.CitrusResourceWrapper;
import org.citrusframework.spi.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Christoph Deppisch
 */
public class HttpScenarioGenerator implements BeanFactoryPostProcessor {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(HttpScenarioGenerator.class);

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
    public HttpScenarioGenerator(SimulatorRestConfigurationProperties simulatorRestConfigurationProperties) {
        swaggerResource = new CitrusResourceWrapper(
            new PathMatchingResourcePatternResolver()
                .getResource(simulatorRestConfigurationProperties.getSwagger().getApi())
        );

        contextPath = simulatorRestConfigurationProperties.getSwagger().getContextPath();
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

            Swagger swagger = new SwaggerParser().parse(readToString(swaggerResource));

            for (Map.Entry<String, Path> path : swagger.getPaths().entrySet()) {
                for (Map.Entry<io.swagger.models.HttpMethod, Operation> operation : path.getValue().getOperationMap().entrySet()) {

                    if (beanFactory instanceof BeanDefinitionRegistry beanDefinitionRegistry) {
                        logger.info("Register auto generated scenario as bean definition: {}", operation.getValue().getOperationId());

                        BeanDefinitionBuilder beanDefinitionBuilder = genericBeanDefinition(HttpOperationScenario.class)
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

                        beanDefinitionRegistry.registerBeanDefinition(operation.getValue().getOperationId(), beanDefinitionBuilder.getBeanDefinition());
                    } else {
                        logger.info("Register auto generated scenario as singleton: {}", operation.getValue().getOperationId());
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
