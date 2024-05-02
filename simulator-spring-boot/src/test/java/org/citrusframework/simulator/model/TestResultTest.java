/*
 * Copyright 2024 the original author or authors.
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

import org.citrusframework.exceptions.CitrusRuntimeException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.citrusframework.simulator.model.TestResult.Status.FAILURE;
import static org.citrusframework.simulator.model.TestResult.Status.SUCCESS;

class TestResultTest {

    private final String className = getClass().getSimpleName();

    @Test
    void constructFromSuccessfulResult() {
        var testName = "constructFromSuccessfulResult";
        Map<String, Object> parameters = Map.of("a", "b");
        var failureType = "Just a type of error...";

        var fixture = new TestResult(org.citrusframework.TestResult.success(testName, className, parameters).withFailureType(failureType));

        assertThat(fixture).hasNoNullFieldsOrPropertiesExcept("id", "errorMessage", "stackTrace", "scenarioExecution");
        assertThat(fixture.getStatus()).isEqualTo(SUCCESS);
        assertThat(fixture.getTestName()).isEqualTo(testName);
        assertThat(fixture.getClassName()).isEqualTo(className);
        assertThat(fixture.getFailureType()).isEqualTo(failureType);
        assertThat(fixture.getTestParameters())
            .hasSize(1).first()
            .satisfies(
                t -> assertThat(t.getKey()).isEqualTo("a"),
                t -> assertThat(t.getValue()).isEqualTo("b"));
    }

    @Test
    void constructFromFailedResult() {
        var testName = "constructFromSuccessfulResult";
        Map<String, Object> parameters = Map.of("a", "b");
        var failureType = "Another type of error...";

        var errorMessage = "Something went wrong!";
        var rootErrorMessage = "Just kidding, it's a test.";
        var exception = new CitrusRuntimeException(errorMessage, new CitrusRuntimeException(rootErrorMessage));

        var fixture = new TestResult(org.citrusframework.TestResult.failed(testName, className, exception, parameters).withFailureType(failureType));

        assertThat(fixture).hasNoNullFieldsOrPropertiesExcept("id", "scenarioExecution");
        assertThat(fixture.getStatus()).isEqualTo(FAILURE);
        assertThat(fixture.getTestName()).isEqualTo(testName);
        assertThat(fixture.getClassName()).isEqualTo(className);
        assertThat(fixture.getErrorMessage()).isEqualTo(rootErrorMessage);
        assertThat(fixture.getStackTrace()).isNotNull().contains(errorMessage, rootErrorMessage);
        assertThat(fixture.getFailureType()).isEqualTo(failureType);
        assertThat(fixture.getTestParameters())
            .hasSize(1).first()
            .satisfies(
                t -> assertThat(t.getKey()).isEqualTo("a"),
                t -> assertThat(t.getValue()).isEqualTo("b"));
    }

    @Test
    void equalsVerifier() throws Exception {
        EntityTestUtils.equalsVerifier(TestResult.class);

        TestResult testResult1 = new TestResult();
        testResult1.setId(1L);

        TestResult testResult2 = new TestResult();
        testResult2.setId(testResult1.getId());

        assertThat(testResult1).isEqualTo(testResult2);

        testResult2.setId(2L);
        assertThat(testResult1).isNotEqualTo(testResult2);

        testResult1.setId(null);
        assertThat(testResult1).isNotEqualTo(testResult2);
    }
}
