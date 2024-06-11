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

import org.citrusframework.CitrusInstanceManager;
import org.citrusframework.context.SpringBeanReferenceResolver;
import org.citrusframework.openapi.OpenApiSpecification;
import org.citrusframework.openapi.OpenApiSpecificationProcessor;
import org.citrusframework.spi.ReferenceResolver;
import org.springframework.context.support.AbstractApplicationContext;

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

        CitrusInstanceManager.get().ifPresent(citrus -> {
            ReferenceResolver referenceResolver = citrus.getCitrusContext().getReferenceResolver();
            if (referenceResolver instanceof SpringBeanReferenceResolver springBeanReferenceResolver
                && springBeanReferenceResolver.getApplicationContext() instanceof  AbstractApplicationContext abstractApplicationContext) {
                    generator.postProcessBeanFactory(abstractApplicationContext.getBeanFactory());
            }
        });
    }
}
