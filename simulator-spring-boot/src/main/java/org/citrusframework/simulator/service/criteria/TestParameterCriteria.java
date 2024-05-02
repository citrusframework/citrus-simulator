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
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.citrusframework.simulator.service.filter.InstantFilter;
import org.citrusframework.simulator.service.filter.LongFilter;
import org.citrusframework.simulator.service.filter.StringFilter;
import org.springdoc.core.annotations.ParameterObject;

import java.io.Serial;
import java.io.Serializable;

/**
 * Criteria class for the {@link org.citrusframework.simulator.model.TestParameter} entity. This class is used
 * in {@link org.citrusframework.simulator.web.rest.TestParameterResource} to receive all the possible filtering options
 * from the Http GET request parameters.
 * <p>
 * For example the following could be a valid request:
 * {@code /test-parameters?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * <p>
 * As Spring is unable to properly convert the types, unless
 * specific {@link org.citrusframework.simulator.service.filter.Filter} class are used, we need to use fix type specific
 * filters.
 */
@Getter
@Setter
@ToString
@ParameterObject
public class TestParameterCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private StringFilter key;

    private StringFilter value;

    private InstantFilter createdDate;

    private InstantFilter lastModifiedDate;

    private LongFilter testResultId;

    private Boolean distinct;

    public TestParameterCriteria() {
    }

    public TestParameterCriteria(TestParameterCriteria other) {
        this.key = other.key == null ? null : other.key.copy();
        this.value = other.value == null ? null : other.value.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
        this.testResultId = other.testResultId == null ? null : other.testResultId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TestParameterCriteria copy() {
        return new TestParameterCriteria(this);
    }

    public StringFilter key() {
        if (key == null) {
            key = new StringFilter();
        }
        return key;
    }

    public StringFilter value() {
        if (value == null) {
            value = new StringFilter();
        }
        return value;
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            createdDate = new InstantFilter();
        }
        return createdDate;
    }

    public InstantFilter lastModifiedDate() {
        if (lastModifiedDate == null) {
            lastModifiedDate = new InstantFilter();
        }
        return lastModifiedDate;
    }

    public LongFilter testResultId() {
        if (testResultId == null) {
            testResultId = new LongFilter();
        }
        return testResultId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TestParameterCriteria testParameterCriteria)) {
            return false;
        }

        return new EqualsBuilder()
            .append(key, testParameterCriteria.key)
            .append(value, testParameterCriteria.value)
            .append(createdDate, testParameterCriteria.createdDate)
            .append(lastModifiedDate, testParameterCriteria.lastModifiedDate)
            .append(testResultId, testParameterCriteria.testResultId)
            .append(distinct, testParameterCriteria.distinct)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(key)
            .append(value)
            .append(createdDate)
            .append(lastModifiedDate)
            .append(testResultId)
            .append(distinct)
            .toHashCode();
    }
}
