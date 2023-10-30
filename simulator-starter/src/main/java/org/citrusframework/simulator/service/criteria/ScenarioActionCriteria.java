/*
 * Copyright 2023 the original author or authors.
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

package org.citrusframework.simulator.service.criteria;

import org.citrusframework.simulator.service.filter.InstantFilter;
import org.citrusframework.simulator.service.filter.LongFilter;
import org.citrusframework.simulator.service.filter.StringFilter;
import org.springdoc.core.annotations.ParameterObject;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the {@link org.citrusframework.simulator.model.ScenarioAction} entity. This class is used
 * in {@link org.citrusframework.simulator.web.rest.ScenarioActionResource} to receive all the possible filtering
 * options from the Http GET request parameters.
 * <p>
 * For example the following could be a valid request:
 * {@code /scenario-actions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * <p>
 * As Spring is unable to properly convert the types, unless
 * specific {@link org.citrusframework.simulator.service.filter.Filter} class are used, we need to use fix type specific
 * filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScenarioActionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter actionId;

    private StringFilter name;

    private InstantFilter startDate;

    private InstantFilter endDate;

    private LongFilter scenarioExecutionId;

    private Boolean distinct;

    public ScenarioActionCriteria() {}

    public ScenarioActionCriteria(ScenarioActionCriteria other) {
        this.actionId = other.actionId == null ? null : other.actionId.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.startDate = other.startDate == null ? null : other.startDate.copy();
        this.endDate = other.endDate == null ? null : other.endDate.copy();
        this.scenarioExecutionId = other.scenarioExecutionId == null ? null : other.scenarioExecutionId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ScenarioActionCriteria copy() {
        return new ScenarioActionCriteria(this);
    }

    public LongFilter getActionId() {
        return actionId;
    }

    public LongFilter id() {
        if (actionId == null) {
            actionId = new LongFilter();
        }
        return actionId;
    }

    public void setActionId(LongFilter actionId) {
        this.actionId = actionId;
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
        final ScenarioActionCriteria that = (ScenarioActionCriteria) o;
        return (
            Objects.equals(actionId, that.actionId) &&
            Objects.equals(name, that.name) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(scenarioExecutionId, that.scenarioExecutionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionId, name, startDate, endDate, scenarioExecutionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScenarioActionCriteria{" +
            (actionId != null ? "actionId=" + actionId + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (startDate != null ? "startDate=" + startDate + ", " : "") +
            (endDate != null ? "endDate=" + endDate + ", " : "") +
            (scenarioExecutionId != null ? "scenarioExecutionId=" + scenarioExecutionId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
