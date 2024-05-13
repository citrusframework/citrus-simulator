package org.citrusframework.simulator.scenario;

import jakarta.annotation.Nullable;
import org.apache.commons.lang3.NotImplementedException;
import org.citrusframework.TestCaseRunner;

public class AbstractSimulatorTestScenario implements SimulatorScenario {

    @Override
    public ScenarioEndpoint getScenarioEndpoint() {
        throw notImplementedException();
    }

    @Nullable
    @Override
    public TestCaseRunner getTestCaseRunner() {
        throw notImplementedException();
    }

    @Override
    public void setTestCaseRunner(TestCaseRunner testCaseRunner) {
        throw notImplementedException();
    }

    private static NotImplementedException notImplementedException() {
        return new NotImplementedException("Not implemented for this testcase!");
    }
}
