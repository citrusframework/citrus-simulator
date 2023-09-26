package org.citrusframework.simulator.service.criteria;

import org.citrusframework.simulator.service.filter.InstantFilter;
import org.citrusframework.simulator.service.filter.IntegerFilter;
import org.citrusframework.simulator.service.filter.LongFilter;
import org.citrusframework.simulator.service.filter.StringFilter;
import org.springdoc.core.annotations.ParameterObject;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the {@link org.citrusframework.simulator.model.TestResult} entity. This class is used
 * in {@link org.citrusframework.simulator.web.rest.TestResultResource} to receive all the possible filtering options
 * from the Http GET request parameters.
 * <p>
 * For example the following could be a valid request:
 * {@code /test-results?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * <p>
 * As Spring is unable to properly convert the types, unless
 * specific {@link org.citrusframework.simulator.service.filter.Filter} class are used, we need to use fix type specific
 * filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TestResultCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter status;

    private StringFilter testName;

    private StringFilter className;

    private StringFilter errorMessage;

    private StringFilter failureStack;

    private StringFilter failureType;

    private InstantFilter createdDate;

    private InstantFilter lastModifiedDate;

    private StringFilter testParameterKey;

    private Boolean distinct;

    public TestResultCriteria() {}

    public TestResultCriteria(TestResultCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.testName = other.testName == null ? null : other.testName.copy();
        this.className = other.className == null ? null : other.className.copy();
        this.errorMessage = other.errorMessage == null ? null : other.errorMessage.copy();
        this.failureStack = other.failureStack == null ? null : other.failureStack.copy();
        this.failureType = other.failureType == null ? null : other.failureType.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
        this.testParameterKey = other.testParameterKey == null ? null : other.testParameterKey.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TestResultCriteria copy() {
        return new TestResultCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
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

    public StringFilter getTestName() {
        return testName;
    }

    public StringFilter testName() {
        if (testName == null) {
            testName = new StringFilter();
        }
        return testName;
    }

    public void setTestName(StringFilter testName) {
        this.testName = testName;
    }

    public StringFilter getClassName() {
        return className;
    }

    public StringFilter className() {
        if (className == null) {
            className = new StringFilter();
        }
        return className;
    }

    public void setClassName(StringFilter className) {
        this.className = className;
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

    public StringFilter getFailureStack() {
        return failureStack;
    }

    public StringFilter failureStack() {
        if (failureStack == null) {
            failureStack = new StringFilter();
        }
        return failureStack;
    }

    public void setFailureStack(StringFilter failureStack) {
        this.failureStack = failureStack;
    }

    public StringFilter getFailureType() {
        return failureType;
    }

    public StringFilter failureType() {
        if (failureType == null) {
            failureType = new StringFilter();
        }
        return failureType;
    }

    public void setFailureType(StringFilter failureType) {
        this.failureType = failureType;
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

    public StringFilter getTestParameterKey() {
        return testParameterKey;
    }

    public StringFilter testParameterId() {
        if (testParameterKey == null) {
            testParameterKey = new StringFilter();
        }
        return testParameterKey;
    }

    public void setTestParameterKey(StringFilter testParameterKey) {
        this.testParameterKey = testParameterKey;
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
        final TestResultCriteria that = (TestResultCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(status, that.status) &&
            Objects.equals(testName, that.testName) &&
            Objects.equals(className, that.className) &&
            Objects.equals(errorMessage, that.errorMessage) &&
            Objects.equals(failureStack, that.failureStack) &&
            Objects.equals(failureType, that.failureType) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(testParameterKey, that.testParameterKey) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            status,
            testName,
            className,
            errorMessage,
            failureStack,
            failureType,
            createdDate,
            lastModifiedDate,
            testParameterKey,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TestResultCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (testName != null ? "testName=" + testName + ", " : "") +
            (className != null ? "className=" + className + ", " : "") +
            (errorMessage != null ? "errorMessage=" + errorMessage + ", " : "") +
            (failureStack != null ? "failureStack=" + failureStack + ", " : "") +
            (failureType != null ? "failureType=" + failureType + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
            (testParameterKey != null ? "testParameterId=" + testParameterKey + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
