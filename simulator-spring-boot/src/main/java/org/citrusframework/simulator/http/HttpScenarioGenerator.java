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

import static org.citrusframework.util.StringUtils.appendSegmentToUrlPath;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

import io.apicurio.datamodels.combined.visitors.CombinedVisitorAdapter;
import io.apicurio.datamodels.openapi.models.OasDocument;
import io.apicurio.datamodels.openapi.models.OasOperation;
import io.apicurio.datamodels.openapi.models.OasPathItem;
import io.apicurio.datamodels.openapi.models.OasPaths;
import jakarta.annotation.Nonnull;
import java.util.Map;
import lombok.Getter;
import org.citrusframework.context.TestContext;
import org.citrusframework.openapi.OpenApiSpecification;
import org.citrusframework.openapi.model.OasModelHelper;
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

/**
 * @author Christoph Deppisch
 */
public class HttpScenarioGenerator implements BeanFactoryPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HttpScenarioGenerator.class);

    /**
     * Target Open API to generate scenarios from
     */
    private final Resource openApiResource;

    private OpenApiSpecification openApiSpecification;

    /**
     * Optional context path
     */
    @Getter
    private String contextPath = "";

    @Getter
    private boolean requestValidationEnabled = true;

    @Getter
    private boolean responseValidationEnabled = true;

    /**
     * Constructor using Spring environment.
     */
    public HttpScenarioGenerator(SimulatorRestConfigurationProperties simulatorRestConfigurationProperties) {
        openApiResource = new CitrusResourceWrapper(
            new PathMatchingResourcePatternResolver()
                .getResource(simulatorRestConfigurationProperties.getOpenApi().getApi())
        );

        contextPath = simulatorRestConfigurationProperties.getOpenApi().getContextPath();
    }

    /**
     * Constructor using swagger API file resource.
     *
     * @param openApiResource
     */
    public HttpScenarioGenerator(Resource openApiResource) {
        this.openApiResource = openApiResource;
    }

    public HttpScenarioGenerator(OpenApiSpecification openApiSpecification) {
        this.openApiResource = null;
        this.openApiSpecification = openApiSpecification;
    }

    public void setRequestValidationEnabled(boolean enabled) {
        this.requestValidationEnabled = enabled;
        if (openApiSpecification != null) {
            openApiSpecification.setRequestValidationEnabled(enabled);
        }
    }

    public void setResponseValidationEnabled(boolean enabled) {
        this.responseValidationEnabled = enabled;
        if (openApiSpecification != null) {
            openApiSpecification.setResponseValidationEnabled(enabled);
        }
    }

    @Override
    public void postProcessBeanFactory(@Nonnull ConfigurableListableBeanFactory beanFactory) throws BeansException {

        if (openApiSpecification == null) {
            initOpenApiSpecification();
        }

        TestContext testContext = new TestContext();
        OasDocument openApiDocument = openApiSpecification.getOpenApiDoc(testContext);
        if (openApiDocument != null && openApiDocument.paths != null) {
            openApiDocument.paths.accept(new ScenarioRegistrar(beanFactory));
        }
    }

    private void initOpenApiSpecification() {
        Assert.notNull(openApiResource,
            """
                Failed to load OpenAPI specification. No OpenAPI specification was provided.
                To load a specification, ensure that either the 'openApiResource' property is set
                or the 'swagger.api' system property is configured to specify the location of the OpenAPI resource.""");
        openApiSpecification = OpenApiSpecification.from(openApiResource);
        openApiSpecification.setRootContextPath(contextPath);
        openApiSpecification.setResponseValidationEnabled(responseValidationEnabled);
        openApiSpecification.setRequestValidationEnabled(requestValidationEnabled);
    }

    private static HttpResponseActionBuilderProvider retrieveOptionalBuilderProvider(
        ConfigurableListableBeanFactory beanFactory) {
        HttpResponseActionBuilderProvider httpResponseActionBuilderProvider = null;
        try {
            httpResponseActionBuilderProvider = beanFactory.getBean(
                HttpResponseActionBuilderProvider.class);
        } catch (BeansException e) {
            // Ignore non existing optional provider
        }
        return httpResponseActionBuilderProvider;
    }

    /**
     * Creates an HTTP scenario based on the given swagger path and operation information.
     *
     * @param path        Full request path, including the context
     * @param scenarioId      Request method
     * @param openApiSpecification   OpenApiSpecification
     * @param operation OpenApi operation
     * @return a matching HTTP scenario
     */
    protected HttpOperationScenario createScenario(String path, String scenarioId, OpenApiSpecification openApiSpecification, OasOperation operation, HttpResponseActionBuilderProvider httpResponseActionBuilderProvider) {
        return new HttpOperationScenario(path, scenarioId, openApiSpecification, operation, httpResponseActionBuilderProvider);
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;

        if (openApiSpecification != null) {
            openApiSpecification.setRootContextPath(contextPath);
        }
    }

    private class ScenarioRegistrar extends CombinedVisitorAdapter {

        private final ConfigurableListableBeanFactory beanFactory;

        private ScenarioRegistrar(ConfigurableListableBeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        public void visitPaths(OasPaths oasPaths) {
            oasPaths.getPathItems().forEach(oasPathItem -> oasPathItem.accept(this));
        }

        @Override
        public void visitPathItem(OasPathItem oasPathItem) {

            HttpResponseActionBuilderProvider httpResponseActionBuilderProvider = retrieveOptionalBuilderProvider(
                beanFactory);

            OasDocument oasDocument = openApiSpecification.getOpenApiDoc(null);
            String path = oasPathItem.getPath();
            for (Map.Entry<String, OasOperation> operationEntry : OasModelHelper.getOperationMap(
                oasPathItem).entrySet()) {

                String fullPath = appendSegmentToUrlPath(appendSegmentToUrlPath(openApiSpecification.getRootContextPath(), OasModelHelper.getBasePath(oasDocument)), path);
                OasOperation oasOperation = operationEntry.getValue();

                String scenarioId = openApiSpecification.getUniqueId(oasOperation);

                if (beanFactory instanceof BeanDefinitionRegistry beanDefinitionRegistry) {
                    logger.info("Register auto generated scenario as bean definition: {}", fullPath);

                    BeanDefinitionBuilder beanDefinitionBuilder = genericBeanDefinition(HttpOperationScenario.class)
                        .addConstructorArgValue(fullPath)
                        .addConstructorArgValue(scenarioId)
                        .addConstructorArgValue(openApiSpecification)
                        .addConstructorArgValue(oasOperation)
                        .addConstructorArgValue(httpResponseActionBuilderProvider);

                    if (beanFactory.containsBeanDefinition("inboundJsonDataDictionary")) {
                        beanDefinitionBuilder.addPropertyReference("inboundDataDictionary", "inboundJsonDataDictionary");
                    }

                    if (beanFactory.containsBeanDefinition("outboundJsonDataDictionary")) {
                        beanDefinitionBuilder.addPropertyReference("outboundDataDictionary", "outboundJsonDataDictionary");
                    }

                    beanDefinitionRegistry.registerBeanDefinition(scenarioId, beanDefinitionBuilder.getBeanDefinition());
                } else {
                    logger.info("Register auto generated scenario as singleton: {}", scenarioId);
                    beanFactory.registerSingleton(scenarioId, createScenario(fullPath, scenarioId, openApiSpecification, oasOperation, httpResponseActionBuilderProvider));
                }
            }
        }
    }
}
