/*
 * Copyright the original author or authors.
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
