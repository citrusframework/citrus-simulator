/*
 * Copyright 2006-2014 the original author or authors.
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

package com.consol.citrus.simulator.service;

import com.consol.citrus.dsl.CitrusTestBuilder;
import com.consol.citrus.simulator.model.UseCaseParameter;
import com.consol.citrus.simulator.model.UseCaseTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Default test builder service. Service is called to invoke a test builder manually. The service has to translate
 * normalized parameters to setter on test builder before execution. Service defines a list of supported parameters with default
 * values.
 *
 * @author Christoph Deppisch
 */
public class DefaultTestBuilderService<T extends CitrusTestBuilder> implements TestBuilderService<T> {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(DefaultTestBuilderService.class);

    /** List of available use case triggers */
    @Autowired
    private List<UseCaseTrigger> useCaseTriggers;

    @Override
    public final void run(T testBuilder, Map<String, Object> parameter, ApplicationContext applicationContext) {
        log.info("Executing test builder: " + testBuilder.getClass().getName());

        testBuilder.setApplicationContext(applicationContext);

        prepareTestBuilder(testBuilder);
        addTestBuilderParameters(testBuilder, parameter);

        testBuilder.execute();
    }

    /**
     * Adds test builder parameters to test builder as normal test variables before execution.
     * @param testBuilder
     * @param parameter
     */
    protected void addTestBuilderParameters(T testBuilder, Map<String, Object> parameter) {
        for (Map.Entry<String, Object> paramEntry : parameter.entrySet()) {
            testBuilder.variable(paramEntry.getKey(), paramEntry.getValue());
        }
    }

    /**
     * Prepare test builder instance before execution. Subclasses can add custom preparation steps in here.
     * @param testBuilder
     */
    protected void prepareTestBuilder(T testBuilder) {
    }

    @Override
    public List<UseCaseParameter> getUseCaseParameter() {
        List<UseCaseParameter> allParameters = new ArrayList<UseCaseParameter>();

        for (UseCaseTrigger useCaseTrigger : useCaseTriggers) {
            allParameters.addAll(useCaseTrigger.getUseCaseParameter());
        }

        return allParameters;
    }
}
