package com.consol.citrus.simulator.listener;

import com.consol.citrus.*;
import com.consol.citrus.actions.SleepAction;
import com.consol.citrus.report.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;


/**
 * @author Christoph Deppisch
 */
@Component
public class SimulatorStatusListener extends AbstractTestListener implements TestActionListener {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger("SimStatusLogger");

    /** Currently running test */
    private Map<String, TestResult> runningTests = new LinkedHashMap<>();

    /** Accumulated test results */
    private TestResults testResults = new TestResults();

    @Override
    public void onTestStart(TestCase test) {
        runningTests.put(StringUtils.arrayToCommaDelimitedString(getParameters(test)), TestResult.success(test.getName(), test.getParameters()));
    }

    @Override
    public void onTestFinish(TestCase test) {
        runningTests.remove(StringUtils.arrayToCommaDelimitedString(getParameters(test)));
    }

    @Override
    public void onTestSuccess(TestCase test) {
        TestResult result = TestResult.success(test.getName(), test.getParameters());
        testResults.addResult(result);
        LOG.info(result.toString());
    }

    @Override
    public void onTestFailure(TestCase test, Throwable cause) {
        TestResult result = TestResult.failed(test.getName(), cause, test.getParameters());
        testResults.addResult(result);

        LOG.info(result.toString());
        LOG.info(result.getFailureCause());
    }

    @Override
    public void onTestActionStart(TestCase testCase, TestAction testAction) {
        if (!testAction.getClass().equals(SleepAction.class)) {
            LOG.debug(testCase.getName() + "(" +
                    StringUtils.arrayToCommaDelimitedString(getParameters(testCase)) + ") - " +
                    testAction.getName() + ": " +
                    (StringUtils.hasText(testAction.getDescription()) ? testAction.getDescription() : ""));
        }
    }

    @Override
    public void onTestActionFinish(TestCase testCase, TestAction testAction) {
    }

    @Override
    public void onTestActionSkipped(TestCase testCase, TestAction testAction) {
    }

    private String[] getParameters(TestCase test) {
        List<String> parameterStrings = new ArrayList<String>();
        for (Map.Entry<String, Object> param : test.getParameters().entrySet()) {
            parameterStrings.add(param.getKey() + "=" + param.getValue());
        }

        return parameterStrings.toArray(new String[parameterStrings.size()]);
    }

    /**
     * Gets the value of the testResults property.
     *
     * @return the testResults
     */
    public TestResults getTestResults() {
        return testResults;
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
     * Clear test results.
     */
    public void clearResults() {
        testResults = new TestResults();
    }
}
