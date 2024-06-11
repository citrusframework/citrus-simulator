package org.citrusframework.simulator.http;

import org.citrusframework.openapi.OpenApiSpecification;
import org.citrusframework.openapi.OpenApiSpecificationProcessor;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Registrar for HTTP operation scenarios based on an OpenAPI specification.
 * <p>
 * This class implements the {@link OpenApiSpecificationProcessor} interface and processes an OpenAPI specification
 * to register HTTP operation scenarios.
 * </p>
 */
public class HttpOperationScenarioRegistrar implements OpenApiSpecificationProcessor {

    @Override
    public void process(OpenApiSpecification openApiSpecification) {

        HttpScenarioGenerator generator = new HttpScenarioGenerator(openApiSpecification);
        ApplicationContext applicationContext = SimulatorConfigurationProperties.getApplicationContext();

        if (applicationContext instanceof ConfigurableApplicationContext configurableApplicationContext) {
            generator.postProcessBeanFactory(configurableApplicationContext.getBeanFactory());
        }
    }
}
