package org.citrusframework.simulator.service.criteria;

import org.citrusframework.simulator.service.filter.InstantFilter;
import org.citrusframework.simulator.service.filter.IntegerFilter;
import org.citrusframework.simulator.service.filter.LongFilter;
import org.citrusframework.simulator.service.filter.StringFilter;
import org.springdoc.core.annotations.ParameterObject;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the {@link org.citrusframework.simulator.model.ScenarioExecution} entity. This class is used
 * in {@link org.citrusframework.simulator.web.rest.ScenarioExecutionResource} to receive all the possible filtering
 * options from the Http GET request parameters.
 * <p>
 * For example the following could be a valid request:
 * {@code /scenario-executions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * <p>
 * As Spring is unable to properly convert the types, unless
 * specific {@link org.citrusframework.simulator.service.filter.Filter} class are used, we need to use fix type
 * specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScenarioExecutionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter executionId;

    private InstantFilter startDate;

    private InstantFilter endDate;

    private StringFilter scenarioName;

    private IntegerFilter status;

    private StringFilter errorMessage;

    private LongFilter scenarioActionsId;

    private LongFilter scenarioMessagesId;

    private Boolean distinct;

    public ScenarioExecutionCriteria() {
    }

    public ScenarioExecutionCriteria(ScenarioExecutionCriteria other) {
        this.executionId = other.executionId == null ? null : other.executionId.copy();
        this.startDate = other.startDate == null ? null : other.startDate.copy();
        this.endDate = other.endDate == null ? null : other.endDate.copy();
        this.scenarioName = other.scenarioName == null ? null : other.scenarioName.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.errorMessage = other.errorMessage == null ? null : other.errorMessage.copy();
        this.scenarioActionsId = other.scenarioActionsId == null ? null : other.scenarioActionsId.copy();
        this.scenarioMessagesId = other.scenarioMessagesId == null ? null : other.scenarioMessagesId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ScenarioExecutionCriteria copy() {
        return new ScenarioExecutionCriteria(this);
    }

    public LongFilter getExecutionId() {
        return executionId;
    }

    public LongFilter id() {
        if (executionId == null) {
            executionId = new LongFilter();
        }
        return executionId;
    }

    public void setExecutionId(LongFilter executionId) {
        this.executionId = executionId;
    }

    public InstantFilter getStartDate() {
        return startDate;
    }

    public InstantFilter startDate() {
        if (startDate == null) {
            startDate = new InstantFilter();
        }
        return startDate;
    }

    public void setStartDate(InstantFilter startDate) {
        this.startDate = startDate;
    }

    public InstantFilter getEndDate() {
        return endDate;
    }

    public InstantFilter endDate() {
        if (endDate == null) {
            endDate = new InstantFilter();
        }
        return endDate;
    }

    public void setEndDate(InstantFilter endDate) {
        this.endDate = endDate;
    }

    public StringFilter getScenarioName() {
        return scenarioName;
    }

    public StringFilter scenarioName() {
        if (scenarioName == null) {
            scenarioName = new StringFilter();
        }
        return scenarioName;
    }

    public void setScenarioName(StringFilter scenarioName) {
        this.scenarioName = scenarioName;
    }

    public IntegerFilter getStatus() {
        return status;
    }

    public IntegerFilter status() {
        if (status == null) {
            status = new IntegerFilter();
        }
        return status;
    }

    public void setStatus(IntegerFilter status) {
        this.status = status;
    }

    public StringFilter getErrorMessage() {
        return errorMessage;
    }

    public StringFilter errorMessage() {
        if (errorMessage == null) {
            errorMessage = new StringFilter();
        }
        return errorMessage;
    }

    public void setErrorMessage(StringFilter errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LongFilter getScenarioActionsId() {
        return scenarioActionsId;
    }

    public LongFilter scenarioActionsId() {
        if (scenarioActionsId == null) {
            scenarioActionsId = new LongFilter();
        }
        return scenarioActionsId;
    }

    public void setScenarioActionsId(LongFilter scenarioActionsId) {
        this.scenarioActionsId = scenarioActionsId;
    }

    public LongFilter getScenarioMessagesId() {
        return scenarioMessagesId;
    }

    public LongFilter scenarioMessagesId() {
        if (scenarioMessagesId == null) {
            scenarioMessagesId = new LongFilter();
        }
        return scenarioMessagesId;
    }

    public void setScenarioMessagesId(LongFilter scenarioMessagesId) {
        this.scenarioMessagesId = scenarioMessagesId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ScenarioExecutionCriteria that = (ScenarioExecutionCriteria) o;
        return (
            Objects.equals(executionId, that.executionId) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(scenarioName, that.scenarioName) &&
                Objects.equals(status, that.status) &&
                Objects.equals(errorMessage, that.errorMessage) &&
                Objects.equals(scenarioActionsId, that.scenarioActionsId) &&
                Objects.equals(scenarioMessagesId, that.scenarioMessagesId) &&
                Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(executionId, startDate, endDate, scenarioName, status, errorMessage, scenarioActionsId, scenarioMessagesId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScenarioExecutionCriteria{" +
            (executionId != null ? "id=" + executionId + ", " : "") +
            (startDate != null ? "startDate=" + startDate + ", " : "") +
            (endDate != null ? "endDate=" + endDate + ", " : "") +
            (scenarioName != null ? "scenarioName=" + scenarioName + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (errorMessage != null ? "errorMessage=" + errorMessage + ", " : "") +
            (scenarioActionsId != null ? "scenarioMessagesId=" + scenarioActionsId + ", " : "") +
            (scenarioMessagesId != null ? "scenarioMessagesId=" + scenarioMessagesId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
