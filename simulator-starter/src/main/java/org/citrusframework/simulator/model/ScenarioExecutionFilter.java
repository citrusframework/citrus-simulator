package org.citrusframework.simulator.model;

import java.util.Arrays;
import org.citrusframework.simulator.model.ScenarioExecution.Status;

/**
 * Filter for filtering {@link Message}s
 */
public class ScenarioExecutionFilter extends MessageFilter {

    private String scenarioName;
    private Integer[] executionStatus;

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public Status[] getExecutionStatus() {
        if (executionStatus == null || executionStatus.length == 0) {
            return new Status[0];
        }

        return Arrays.stream(executionStatus)
            .map(Status::fromId)
            .toList()
            .toArray(new Status[executionStatus.length]);
    }

    public void setExecutionStatus(Status[] executionStatus) {
        this.executionStatus = Arrays.stream(executionStatus)
            .map(Status::getId)
            .toList()
            .toArray(new Integer[0]);
    }
}
