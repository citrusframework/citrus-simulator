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

package org.citrusframework.simulator.service;

import org.citrusframework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static org.citrusframework.simulator.model.ScenarioExecution.EXECUTION_ID;

public class TestCaseUtil {

    private static final Logger logger = LoggerFactory.getLogger(TestCaseUtil.class);

    private TestCaseUtil() {
        // Static access only
    }

    public static long getScenarioExecutionId(TestCase testCase) {
        logger.trace("Lookup '{}' in TestCaseParameters : {}", EXECUTION_ID, testCase.getVariableDefinitions());

        if (!testCase.getVariableDefinitions().containsKey(EXECUTION_ID)) {
            throw new IllegalArgumentException(format("TestCase does not contain '%s'!", EXECUTION_ID));
        }

        return parseLong(testCase.getVariableDefinitions().get(EXECUTION_ID).toString());
    }
}
