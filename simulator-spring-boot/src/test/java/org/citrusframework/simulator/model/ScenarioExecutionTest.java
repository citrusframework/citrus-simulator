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

class ScenarioExecutionTest {

    @Test
    void equalsVerifier() throws Exception {
        EntityTestUtils.equalsVerifier(ScenarioExecution.class);

        ScenarioExecution execution1 = new ScenarioExecution();
        execution1.setExecutionId(1L);

        ScenarioExecution execution2 = new ScenarioExecution();
        execution2.setExecutionId(execution1.getExecutionId());

        assertThat(execution1).isEqualTo(execution2);

        execution2.setExecutionId(2L);
        assertThat(execution1).isNotEqualTo(execution2);

        execution1.setExecutionId(null);
        assertThat(execution1).isNotEqualTo(execution2);
    }
}
