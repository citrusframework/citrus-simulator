package org.citrusframework.simulator.service.impl;

import org.citrusframework.TestCase;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TestCaseUtil {

    private static final Logger logger = LoggerFactory.getLogger(TestCaseUtil.class);

    private TestCaseUtil() {
        // Static access only
    }

    static long getScenarioExecutionId(TestCase testCase) {
        logger.trace("Lookup '{}' in TestCaseParameters : {}", ScenarioExecution.EXECUTION_ID, testCase.getVariableDefinitions());
        return Long.parseLong(testCase.getVariableDefinitions().get(ScenarioExecution.EXECUTION_ID).toString());
    }
}
