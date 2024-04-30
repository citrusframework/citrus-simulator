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

import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
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

    private LongFilter scenarioParametersId;

    private String headers;

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
        this.scenarioParametersId = other.scenarioParametersId == null ? null : other.scenarioParametersId.copy();
        this.headers = other.headers;
        this.distinct = other.distinct;
    }

    @Override
    public ScenarioExecutionCriteria copy() {
        return new ScenarioExecutionCriteria(this);
    }

    public LongFilter id() {
        if (executionId == null) {
            executionId = new LongFilter();
        }
        return executionId;
    }

    public InstantFilter startDate() {
        if (startDate == null) {
            startDate = new InstantFilter();
        }
        return startDate;
    }

    public InstantFilter endDate() {
        if (endDate == null) {
            endDate = new InstantFilter();
        }
        return endDate;
    }

    public StringFilter scenarioName() {
        if (scenarioName == null) {
            scenarioName = new StringFilter();
        }
        return scenarioName;
    }

    public IntegerFilter status() {
        if (status == null) {
            status = new IntegerFilter();
        }
        return status;
    }

    public StringFilter errorMessage() {
        if (errorMessage == null) {
            errorMessage = new StringFilter();
        }
        return errorMessage;
    }

    public LongFilter scenarioActionsId() {
        if (scenarioActionsId == null) {
            scenarioActionsId = new LongFilter();
        }
        return scenarioActionsId;
    }

    public LongFilter scenarioMessagesId() {
        if (scenarioMessagesId == null) {
            scenarioMessagesId = new LongFilter();
        }
        return scenarioMessagesId;
    }

    public LongFilter scenarioParametersId() {
        if (scenarioParametersId == null) {
            scenarioParametersId = new LongFilter();
        }
        return scenarioParametersId;
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
                Objects.equals(scenarioParametersId, that.scenarioParametersId) &&
                Objects.equals(headers, that.headers) &&
                Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(executionId, startDate, endDate, scenarioName, status, errorMessage, scenarioActionsId, scenarioMessagesId, scenarioParametersId, headers, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScenarioExecutionCriteria{" +
            (executionId != null ? "executionId=" + executionId + ", " : "") +
            (startDate != null ? "startDate=" + startDate + ", " : "") +
            (endDate != null ? "endDate=" + endDate + ", " : "") +
            (scenarioName != null ? "scenarioName=" + scenarioName + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (errorMessage != null ? "errorMessage=" + errorMessage + ", " : "") +
            (scenarioActionsId != null ? "scenarioActionsId=" + scenarioActionsId + ", " : "") +
            (scenarioMessagesId != null ? "scenarioMessagesId=" + scenarioMessagesId + ", " : "") +
            (scenarioMessagesId != null ? "scenarioParametersId=" + scenarioParametersId + ", " : "") +
            (headers != null ? "headers=" + headers + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
