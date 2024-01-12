package org.citrusframework.simulator.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class TestParameterTest {

    @Test
    void equalsVerifier() throws Exception {
        EntityTestUtils.equalsVerifier(TestParameter.class);
        TestParameter testParameter1 = new TestParameter("key", "", TestResult.builder().id(1L).build());

        TestParameter testParameter2 = new TestParameter(testParameter1.getKey(), testParameter1.getValue(), testParameter1.getTestResult());

        assertThat(testParameter1).isEqualTo(testParameter2);

        setField(testParameter2, "testParameterId", new TestParameter.TestParameterId("key-2", testParameter1.getTestResult()), TestParameter.TestParameterId.class);
        assertThat(testParameter1).isNotEqualTo(testParameter2);

        setField(testParameter2, "testParameterId", new TestParameter.TestParameterId(testParameter1.getKey(), TestResult.builder().id(2L).build()), TestParameter.TestParameterId.class);
        assertThat(testParameter1).isNotEqualTo(testParameter2);

        testParameter1.setTestResult(null);
        assertThat(testParameter1).isNotEqualTo(testParameter2);
    }
}
