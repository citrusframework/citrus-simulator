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

import com.consol.citrus.dsl.endpoint.Executable;
import com.consol.citrus.simulator.model.UseCaseParameter;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;

/**
 * Service interface capable of executing test designers. The service takes care on setting up test designer before execution. Service
 * gets a list of normalized parameters which has to be translated to setters on the test designer instance before execution.
 *
 * @author Christoph Deppisch
 */
public interface UseCaseService<T extends Executable> {

    /**
     * Executes a test designer instance with given test designer parameters which get translated into setters on test designer
     * implementation. It is ensured that test parameters do have necessary keys set. Application context is necessary to
     * create proper test context for execution.
     * @param testExecutable
     * @param parameter
     * @param applicationContext
     */
    void run(T testExecutable, Map<String, Object> parameter, ApplicationContext applicationContext);

    /**
     * Builds a list of required test designer parameters. Values in this list represent default values. These
     * values may be set by outside logic then.
     * @return
     */
    List<UseCaseParameter> getUseCaseParameter();
}
