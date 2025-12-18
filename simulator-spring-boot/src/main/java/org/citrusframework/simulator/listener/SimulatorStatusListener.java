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

package org.citrusframework.simulator.listener;

import org.citrusframework.DefaultTestCase;
import org.citrusframework.TestAction;
import org.citrusframework.TestCase;
import org.citrusframework.TestResult;
import org.citrusframework.actions.SleepAction;
import org.citrusframework.common.Described;
import org.citrusframework.report.AbstractTestListener;
import org.citrusframework.report.TestActionListener;
import org.citrusframework.simulator.service.ScenarioActionService;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.citrusframework.TestResult.failed;
import static org.citrusframework.TestResult.success;
import static org.citrusframework.simulator.service.TestCaseUtil.getScenarioExecutionId;
import static org.citrusframework.util.StringUtils.hasText;
import static org.springframework.util.StringUtils.arrayToCommaDelimitedString;

@Component
public class SimulatorStatusListener extends AbstractTestListener implements TestActionListener {

    private static final Logger logger = LoggerFactory.getLogger(SimulatorStatusListener.class);

    /**
     * Currently running test.
     * <p>
     * TODO: Replace with metric.
     */
    private final Map<String, TestResult> runningTests = new ConcurrentHashMap<>();

    private final ScenarioActionService scenarioActionService;
    private final ScenarioExecutionService scenarioExecutionService;

    public SimulatorStatusListener(ScenarioActionService scenarioActionService, ScenarioExecutionService scenarioExecutionService) {
        this.scenarioActionService = scenarioActionService;
        this.scenarioExecutionService = scenarioExecutionService;
    }

    @Override
    public void onTestStart(TestCase test) {
        if (test instanceof DefaultTestCase defaultTestCase) {
            runningTests.put(arrayToCommaDelimitedString(getParameters(test)), success(test.getName(), test.getTestClass().getSimpleName(), defaultTestCase.getParameters()));
        } else {
            runningTests.put(arrayToCommaDelimitedString(getParameters(test)), success(test.getName(), test.getTestClass().getSimpleName()));
        }
    }

    @Override
    public void onTestFinish(TestCase test) {
        runningTests.remove(arrayToCommaDelimitedString(getParameters(test)));
    }

    @Override
    public void onTestSuccess(TestCase testCase) {
        TestResult testResult;
        if (testCase instanceof DefaultTestCase defaultTestCase) {
            testResult = success(testCase.getName(), testCase.getTestClass().getSimpleName(), defaultTestCase.getParameters());
        } else {
            testResult = success(testCase.getName(), testCase.getTestClass().getSimpleName());
        }

        scenarioExecutionService.completeScenarioExecution(getScenarioExecutionId(testCase), new org.citrusframework.simulator.model.TestResult(testResult));

        logger.info("Scenario succeeded: {}", testResult);
    }

    @Override
    public void onTestFailure(TestCase testCase, Throwable cause) {
        TestResult testResult;
        if (testCase instanceof DefaultTestCase defaultTestCase) {
            testResult = failed(testCase.getName(), testCase.getTestClass().getSimpleName(), cause, defaultTestCase.getParameters());
        } else {
            testResult = failed(testCase.getName(), testCase.getTestClass().getSimpleName(), cause);
        }

        scenarioExecutionService.completeScenarioExecution(getScenarioExecutionId(testCase), new org.citrusframework.simulator.model.TestResult(testResult));

        logger.info("Scenario failed: {}", testResult);
    }

    @Override
    public void onTestActionStart(TestCase testCase, TestAction testAction) {
        if (!ignoreTestAction(testAction)) {
            logger.debug("{} ({}) - {}{}",
                testCase.getName(),
                arrayToCommaDelimitedString(getParameters(testCase)),
                testAction.getName(),
                (testAction instanceof Described described && hasText(described.getDescription()) ? ": " + described.getDescription() : "")
            );

            scenarioActionService.createForScenarioExecutionAndSave(testCase, testAction);
        }
    }

    @Override
    public void onTestActionFinish(TestCase testCase, TestAction testAction) {
        if (!ignoreTestAction(testAction)) {
            scenarioActionService.completeTestAction(testCase, testAction);
        }
    }

    @Override
    public void onTestActionFailed(TestCase testCase, TestAction testAction, Throwable throwable) {
    }

    @Override
    public void onTestActionSkipped(TestCase testCase, TestAction testAction) {
    }

    private String[] getParameters(TestCase test) {
        List<String> parameterStrings = new ArrayList<>();

        if (test instanceof DefaultTestCase defaultTestCase) {
            for (Map.Entry<String, Object> param : defaultTestCase.getParameters().entrySet()) {
                parameterStrings.add(param.getKey() + "=" + param.getValue());
            }
        }

        return parameterStrings.toArray(new String[0]);
    }

    private boolean ignoreTestAction(TestAction testAction) {
        return testAction.getClass().equals(SleepAction.class);
    }
}
