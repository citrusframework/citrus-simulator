package org.citrusframework.simulator.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestResultTest {

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
