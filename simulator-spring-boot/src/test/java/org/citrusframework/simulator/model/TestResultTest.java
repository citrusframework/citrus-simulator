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
