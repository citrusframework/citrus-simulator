package org.citrusframework.simulator.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestParameterIdTest {

    @Test
    void equalsVerifier() throws Exception {
        EntityTestUtils.equalsVerifier(TestParameter.TestParameterId.class);

        TestResult testResult1 = TestResult.builder().id(1L).build();

        TestParameter.TestParameterId testParameterId1 = new TestParameter.TestParameterId("key", testResult1);

        TestParameter.TestParameterId testParameterId2 = new TestParameter.TestParameterId(testParameterId1.key, testResult1);
        assertThat(testParameterId1).isEqualTo(testParameterId2);

        testParameterId2.key = "key-2";
        assertThat(testParameterId1).isNotEqualTo(testParameterId2);

        testParameterId2.key = testParameterId1.key;
        testParameterId2.testResultId = 2L;
        assertThat(testParameterId1).isNotEqualTo(testParameterId2);

        testParameterId1.testResultId = null;
        assertThat(testParameterId1).isNotEqualTo(testParameterId2);

        testParameterId1.key = null;
        testParameterId1.testResultId = testResult1.getId();
        assertThat(testParameterId1).isNotEqualTo(testParameterId2);
    }
}
