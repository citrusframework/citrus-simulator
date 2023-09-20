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
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a parameter of a test result, holding a key-value pair of parameter details. It is linked to
 * a {@link org.citrusframework.simulator.model.TestResult} entity through a many-to-one relationship, where
 * a test result can have many parameters.
 * <p>
 * The entity includes a composite primary key represented by {@link TestParameterId}, which includes the
 * parameter key and the ID of the associated test result. This composite key ensures that parameter entries
 * are unique based on both the key and the test result ID.
 * <p>
 * This entity is not updatable, and it is recommended to add {@code @Column(updatable = false)} annotation
 * to all applicable fields to enforce this constraint at the database level.
 * <p>
 * <b>Note:</b> This class contains a default constructor to satisfy Hibernate's requirement for a no-args constructor,
 * as well as constructors for creating new instances based on specified values or existing non-persistent objects.
 *
 * @see org.citrusframework.simulator.model.TestResult
 * @see org.citrusframework.simulator.model.AbstractAuditingEntity
 */
@Entity
@Table(name = "test_parameter")
public class TestParameter extends AbstractAuditingEntity<TestParameter, TestParameter.TestParameterId> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TestParameterId testParameterId;

    @NotEmpty
    @Column(name = "parameter_value", nullable = false, updatable = false)
    private String value;

    @ManyToOne
    @MapsId("testResultId")
    @JoinColumn(name = "test_result_id")
    @JsonIgnoreProperties(value = { "testParameters" }, allowSetters = true)
    private TestResult testResult;

    /**
     * This is an empty constructor, not intended for "manual" usage.
     * The Hibernate framework requires this in order to de- and serialize entities.
     */
    public TestParameter() {
        // Hibernate constructor
    }

    /**
     * Create a parameter entity from existing {@link org.citrusframework.TestResult} parameters.
     *
     * @param key   the parameter key
     * @param value the parameter value
     */
    public TestParameter(String key, String value, TestResult testResult) {
        this.testParameterId = new TestParameterId(key, testResult);
        this.value = value;
        this.testResult = testResult;
    }

    public String getKey() {
        return testParameterId.key;
    }

    public String getValue() {
        return value;
    }

    public TestResult getTestResult() {
        return testResult;
    }

    @Override
    public String toString() {
        return "TestParameter{" +
            "key='" + getKey() + "'" +
            "value='" + getValue() + "'" +
            "}";
    }

    /**
     * Represents a composite key for the {@link TestParameter} entity, encapsulating a parameter key
     * and the ID of the associated {@link TestResult}. This ensures that the parameter entries are
     * uniquely identified by both the key and the test result ID.
     * <p>
     * <b>Note:</b> This class contains a default constructor to satisfy Hibernate's requirements for a
     * no-args constructor for composite keys. It also provides a constructor to facilitate the creation
     * of a new composite key based on specified values.
     * <p>
     * This embedded ID class is not intended to be used directly in application code and exists mainly
     * to satisfy JPA and database requirements.
     *
     * @see org.citrusframework.simulator.model.TestParameter
     * @see org.citrusframework.simulator.model.TestResult
     */
    @Embeddable
    public class TestParameterId implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @Column(name = "parameter_key", nullable = false, updatable = false)
        public String key;

        @Column(nullable = false, updatable = false)
        public Long testResultId;

        /**
         * This is an empty constructor, not intended for "manual" usage.
         * The Hibernate framework requires this in order to de- and serialize composite keys.
         */
        public TestParameterId() {
            // Hibernate constructor
        }

        /**
         * Create a parameter composite key from existing {@link org.citrusframework.TestResult} parameters.
         *
         * @param key        the parameter key
         * @param testResult the test-result this parameter is linked too
         */
        public TestParameterId(String key, TestResult testResult) {
            this.key = key;
            this.testResultId = testResult.getId();
        }
    }
}
