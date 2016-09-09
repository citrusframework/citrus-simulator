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

import com.consol.citrus.dsl.design.TestDesigner;
import com.consol.citrus.dsl.endpoint.Executable;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.simulator.model.UseCaseParameter;
import com.consol.citrus.simulator.model.UseCaseTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Default test builder service. Service is called to invoke a test executable manually. The service has to translate
 * normalized parameters to setter on test executable before execution. Service defines a list of supported parameters with default
 * values.
 *
 * @author Christoph Deppisch
 */
@Service
public class DefaultUseCaseService<T extends Executable> implements UseCaseService<T> {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(DefaultUseCaseService.class);

    /** List of available use case triggers */
    @Autowired(required = false)
    private List<UseCaseTrigger> useCaseTriggers = new ArrayList<>();

    @Override
    public final void run(T testExecutable, Map<String, Object> parameter, ApplicationContext applicationContext) {
        log.info("Executing test executable: " + testExecutable.getClass().getName());

        if (testExecutable instanceof ApplicationContextAware) {
            ((ApplicationContextAware) testExecutable).setApplicationContext(applicationContext);
        }

        prepareTestExecutable(testExecutable);
        addParameters(testExecutable, parameter);

        testExecutable.execute();
    }

    /**
     * Adds test executable parameters to test executable as normal test variables before execution.
     * @param testExecutable
     * @param parameter
     */
    protected void addParameters(T testExecutable, Map<String, Object> parameter) {
        for (Map.Entry<String, Object> paramEntry : parameter.entrySet()) {
            if (testExecutable instanceof TestDesigner) {
                ((TestDesigner) testExecutable).variable(paramEntry.getKey(), paramEntry.getValue());
            }

            if (testExecutable instanceof TestRunner) {
                ((TestRunner) testExecutable).variable(paramEntry.getKey(), paramEntry.getValue());
            }
        }
    }

    /**
     * Prepare test executable instance before execution. Subclasses can add custom preparation steps in here.
     * @param testExecutable
     */
    protected void prepareTestExecutable(T testExecutable) {
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
