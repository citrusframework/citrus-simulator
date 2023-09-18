/*
 * Copyright 2006-2017 the original author or authors.
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

package org.citrusframework.simulator.listener;

import org.apache.commons.lang.NotImplementedException;
import org.citrusframework.DefaultTestCase;
import org.citrusframework.TestAction;
import org.citrusframework.TestCase;
import org.citrusframework.TestResult;
import org.citrusframework.actions.SleepAction;
import org.citrusframework.common.Described;
import org.citrusframework.report.AbstractTestListener;
import org.citrusframework.report.TestActionListener;
import org.citrusframework.simulator.service.ActivityService;
import org.citrusframework.simulator.service.TestResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Christoph Deppisch
 */
@Component
public class SimulatorStatusListener extends AbstractTestListener implements TestActionListener {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(SimulatorStatusListener.class);

    /**
     * Currently running test
     */
    private Map<String, TestResult> runningTests = new ConcurrentHashMap<>();

    private final ActivityService executionService;

    private final TestResultService testResultService;

    public SimulatorStatusListener(ActivityService executionService, TestResultService testResultService) {
        this.executionService = executionService;
        this.testResultService = testResultService;
    }

    @Override
    public void onTestStart(TestCase test) {
        if (test instanceof DefaultTestCase) {
            runningTests.put(StringUtils.arrayToCommaDelimitedString(getParameters(test)), TestResult.success(test.getName(), test.getTestClass().getSimpleName(), ((DefaultTestCase)test).getParameters()));
        } else {
            runningTests.put(StringUtils.arrayToCommaDelimitedString(getParameters(test)), TestResult.success(test.getName(), test.getTestClass().getSimpleName()));
        }
    }

    @Override
    public void onTestFinish(TestCase test) {
        runningTests.remove(StringUtils.arrayToCommaDelimitedString(getParameters(test)));
    }

    @Override
    public void onTestSuccess(TestCase test) {
        TestResult result;
        if (test instanceof DefaultTestCase) {
            result = TestResult.success(test.getName(), test.getTestClass().getSimpleName(), ((DefaultTestCase)test).getParameters());
        } else {
            result = TestResult.success(test.getName(), test.getTestClass().getSimpleName());
        }

        testResultService.save(result);
        executionService.completeScenarioExecutionSuccess(test);

        logger.info(result.toString());
    }

    @Override
    public void onTestFailure(TestCase test, Throwable cause) {
        TestResult result;
        if (test instanceof DefaultTestCase) {
            result = TestResult.failed(test.getName(), test.getTestClass().getSimpleName(), cause, ((DefaultTestCase)test).getParameters());
        } else {
            result = TestResult.failed(test.getName(), test.getTestClass().getSimpleName(), cause);
        }

        testResultService.save(result);
        executionService.completeScenarioExecutionFailure(test, cause);

        logger.info(result.toString());
        logger.info(result.getFailureType());
    }

    @Override
    public void onTestActionStart(TestCase testCase, TestAction testAction) {
        if (!ignoreTestAction(testAction)) {
            if (logger.isDebugEnabled()) {
                logger.debug(testCase.getName() + "(" +
                        StringUtils.arrayToCommaDelimitedString(getParameters(testCase)) + ") - " +
                        testAction.getName() + ": " +
                        (testAction instanceof Described && StringUtils.hasText(((Described) testAction).getDescription()) ? ((Described) testAction).getDescription() : ""));
            }

            executionService.createTestAction(testCase, testAction);
        }
    }

    @Override
    public void onTestActionFinish(TestCase testCase, TestAction testAction) {
        if (!ignoreTestAction(testAction)) {
            executionService.completeTestAction(testCase, testAction);
        }
    }

    private boolean ignoreTestAction(TestAction testAction) {
        return testAction.getClass().equals(SleepAction.class);
    }


    @Override
    public void onTestActionSkipped(TestCase testCase, TestAction testAction) {
    }

    private String[] getParameters(TestCase test) {
        List<String> parameterStrings = new ArrayList<String>();

        if (test instanceof DefaultTestCase) {
            for (Map.Entry<String, Object> param : ((DefaultTestCase) test).getParameters().entrySet()) {
                parameterStrings.add(param.getKey() + "=" + param.getValue());
            }
        }

        return parameterStrings.toArray(new String[parameterStrings.size()]);
    }

    /**
     * Gets the value of the testResults property.
     *
     * @return the testResults
     */
    public List<org.citrusframework.simulator.model.TestResult> getTestResultService() {
        return testResultService.findAll();
    }

    /**
     * Gets the value of the runningTests property.
     *
     * @return the runningTests
     */
    public Map<String, TestResult> getRunningTests() {
        return runningTests;
    }

    /**
     * Deletes all test results. Shall be removed, but will be kept for now to ensure backward compatibility.
     *
     * @deprecated will be removed wihin the next breaking release!
     */
    @Deprecated(forRemoval = true)
    public void clearResults() {
        throw new NotImplementedException("This method will be removed within the next breaking release!");
    }

    /**
     * Get the count of active scenarios
     */
    public int getCountActiveScenarios() {
        return runningTests.size();
    }
}
