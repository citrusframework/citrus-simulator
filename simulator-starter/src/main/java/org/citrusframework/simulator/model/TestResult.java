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

package org.citrusframework.simulator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the persistent data model for a test result in the system. This class holds all information
 * about a specific test including its status, name, class name, parameters, and any error messages or failure details.
 * <p>
 * The entity is not designed to be updated after creation; all {@code @Column} annotations should be configured with
 * {@code updatable = false} to enforce this at the database level.
 * <p>
 * <b>Note:</b> This class contains a default constructor to satisfy Hibernate's requirement for a no-args constructor.
 * It also provides a constructor that accepts an {@link org.citrusframework.TestResult} object to facilitate
 * the creation of a new entity based on a non-persistent instance.
 *
 * @see org.citrusframework.simulator.model.TestParameter
 * @see org.citrusframework.simulator.model.AbstractAuditingEntity
 */
@Entity
public class TestResult extends AbstractAuditingEntity<TestResult, Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    /**
     * Actual result as a numerical representation of {@link Status}
     */
    @Column(nullable = false, updatable = false)
    private Integer status = Status.UNKNOWN.getId();

    /**
     * Name of the test
     */
    @NotEmpty
    @Column(nullable = false, updatable = false)
    private String testName;

    /**
     * Fully qualified class name of the test
     */
    @NotEmpty
    @Column(nullable = false, updatable = false)
    private String className;

    /**
     * Optional test parameters
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "testResult")
    @JsonIgnoreProperties(value = { "testResult" }, allowSetters = true)
    private final Set<TestParameter> testParameters = new HashSet<>();

    /**
     * Error message
     */
    @Size(max = 1000)
    @Column(length = 1000, updatable = false)
    private String errorMessage;

    /**
     * Failure stack trace
     */
    @Column(updatable = false)
    private String failureStack;

    /**
     * Failure type information
     */
    @Column(updatable = false)
    private String failureType;

    /**
     * This is an empty constructor, not intended for "manual" usage.
     * The Hibernate framework requires this in order to de- and serialize entities.
     */
    public TestResult() {
        // Hibernate constructor
    }

    /**
     * Create a persistent copy of an existing {@link org.citrusframework.TestResult}.
     *
     * @param testResult the original {@link org.citrusframework.TestResult}
     */
    public TestResult(org.citrusframework.TestResult testResult) {
        status = convertToStatus(testResult.getResult());
        testName = testResult.getTestName();
        className = testResult.getClassName();
        testResult.getParameters().forEach((key, value) -> testParameters.add(new TestParameter(key, value.toString(), this)));
        // NOte that the cause will be dropped: testResult.getCause()
        errorMessage = testResult.getErrorMessage();
        failureStack = testResult.getFailureStack();
        failureType = testResult.getFailureType();
    }

    public static TestResultBuilder builder() {
        return new TestResultBuilder();
    }

    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return Status.fromId(status);
    }

    public String getTestName() {
        return testName;
    }

    public String getClassName() {
        return className;
    }

    public Set<TestParameter> getTestParameters() {
        return testParameters;
    }

    public void addTestParameter(TestParameter testParameter) {
        testParameters.add(testParameter);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getFailureStack() {
        return failureStack;
    }

    public String getFailureType() {
        return failureType;
    }

    private int convertToStatus(String resultName) {
        return Arrays.stream(Status.values())
            .filter(result -> result.name().equals(resultName))
            .findFirst()
            .orElse(Status.UNKNOWN)
            .id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof TestResult testResult) {
            return id != null && id.equals(testResult.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "TestResult{" +
            "id=" + getId() +
            ", status=" + getStatus() +
            ", testName='" + getTestName() + "'" +
            ", className='" + getClassName() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", failureStack='" + getFailureStack() + "'" +
            ", failureType='" + getFailureType() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }

    public enum Status {

        UNKNOWN(0), SUCCESS(1), FAILURE(2), SKIP(3);

        private final int id;

        Status(int i) {
            this.id = i;
        }

        public int getId() {
            return id;
        }

        public static Status fromId(int id) {
            return Arrays.stream(values())
                .filter(status -> status.id == id)
                .findFirst()
                .orElse(Status.UNKNOWN);
        }
    }

    public static class TestResultBuilder extends AuditingEntityBuilder<TestResultBuilder, TestResult, Long> {

        private final TestResult testResult = new TestResult();

        private TestResultBuilder(){
            // Static access through entity
        }

        public TestResultBuilder id(Long id) {
            testResult.id = id;
            return this;
        }

        public TestResultBuilder status(Status status) {
            testResult.status = status.id;
            return this;
        }

        public TestResultBuilder testName(String testName) {
            testResult.testName = testName;
            return this;
        }

        public TestResultBuilder className(String className) {
            testResult.className = className;
            return this;
        }

        public TestResultBuilder errorMessage(String errorMessage) {
            testResult.errorMessage = errorMessage;
            return this;
        }

        public TestResultBuilder failureStack(String failureStack) {
            testResult.failureStack = failureStack;
            return this;
        }

        public TestResultBuilder failureType(String failureType) {
            testResult.failureType = failureType;
            return this;
        }

        @Override
        protected TestResult getEntity() {
            return testResult;
        }
    }
}
