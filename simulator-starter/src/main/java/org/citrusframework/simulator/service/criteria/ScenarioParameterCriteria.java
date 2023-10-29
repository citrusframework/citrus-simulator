package org.citrusframework.simulator.service.criteria;

import org.citrusframework.simulator.service.filter.InstantFilter;
import org.citrusframework.simulator.service.filter.IntegerFilter;
import org.citrusframework.simulator.service.filter.LongFilter;
import org.citrusframework.simulator.service.filter.StringFilter;
import org.springdoc.core.annotations.ParameterObject;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the {@link org.citrusframework.simulator.model.ScenarioParameter} entity. This class is used
 * in {@link org.citrusframework.simulator.web.rest.ScenarioParameterResource} to receive all the possible filtering
 * options from the Http GET request parameters.
 * <p>
 * For example the following could be a valid request:
 * {@code /scenario-parameters?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * <p>
 * As Spring is unable to properly convert the types, unless
 * specific {@link org.citrusframework.simulator.service.filter.Filter} class are used, we need to use fix type
 * specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScenarioParameterCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter parameterId;

    private StringFilter name;

    private IntegerFilter controlType;

    private StringFilter value;

    private InstantFilter createdDate;

    private InstantFilter lastModifiedDate;

    private LongFilter scenarioExecutionId;

    private Boolean distinct;

    public ScenarioParameterCriteria() {
    }

    public ScenarioParameterCriteria(ScenarioParameterCriteria other) {
        this.parameterId = other.parameterId == null ? null : other.parameterId.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.controlType = other.controlType == null ? null : other.controlType.copy();
        this.value = other.value == null ? null : other.value.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
        this.scenarioExecutionId = other.scenarioExecutionId == null ? null : other.scenarioExecutionId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ScenarioParameterCriteria copy() {
        return new ScenarioParameterCriteria(this);
    }

    public LongFilter getParameterId() {
        return parameterId;
    }

    public LongFilter id() {
        if (parameterId == null) {
            parameterId = new LongFilter();
        }
        return parameterId;
    }

    public void setParameterId(LongFilter parameterId) {
        this.parameterId = parameterId;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public IntegerFilter getControlType() {
        return controlType;
    }

    public IntegerFilter controlType() {
        if (controlType == null) {
            controlType = new IntegerFilter();
        }
        return controlType;
    }

    public void setControlType(IntegerFilter controlType) {
        this.controlType = controlType;
    }

    public StringFilter getValue() {
        return value;
    }

    public StringFilter value() {
        if (value == null) {
            value = new StringFilter();
        }
        return value;
    }

    public void setValue(StringFilter value) {
        this.value = value;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            createdDate = new InstantFilter();
        }
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public InstantFilter getLastModifiedDate() {
        return lastModifiedDate;
    }

    public InstantFilter lastModifiedDate() {
        if (lastModifiedDate == null) {
            lastModifiedDate = new InstantFilter();
        }
        return lastModifiedDate;
    }

    public void setLastModifiedDate(InstantFilter lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public LongFilter getScenarioExecutionId() {
        return scenarioExecutionId;
    }

    public LongFilter scenarioExecutionId() {
        if (scenarioExecutionId == null) {
            scenarioExecutionId = new LongFilter();
        }
        return scenarioExecutionId;
    }

    public void setScenarioExecutionId(LongFilter scenarioExecutionId) {
        this.scenarioExecutionId = scenarioExecutionId;
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
        final ScenarioParameterCriteria that = (ScenarioParameterCriteria) o;
        return (
            Objects.equals(parameterId, that.parameterId) &&
            Objects.equals(name, that.name) &&
            Objects.equals(controlType, that.controlType) &&
            Objects.equals(value, that.value) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(scenarioExecutionId, that.scenarioExecutionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameterId, name, controlType, value, createdDate, lastModifiedDate, scenarioExecutionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScenarioParameterCriteria{" +
            (parameterId != null ? "parameterId=" + parameterId + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (controlType != null ? "controlType=" + controlType + ", " : "") +
            (value != null ? "value=" + value + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
            (scenarioExecutionId != null ? "scenarioExecutionId=" + scenarioExecutionId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
