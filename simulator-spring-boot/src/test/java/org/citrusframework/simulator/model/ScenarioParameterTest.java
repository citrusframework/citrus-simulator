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

class ScenarioParameterTest {

    @Test
    void equalsVerifier() throws Exception {
        EntityTestUtils.equalsVerifier(ScenarioParameter.class);

        ScenarioParameter parameter1 = new ScenarioParameter();
        parameter1.setParameterId(1L);

        ScenarioParameter parameter2 = new ScenarioParameter();
        parameter2.setParameterId(parameter1.getParameterId());

        assertThat(parameter1).isEqualTo(parameter2);

        parameter2.setParameterId(2L);
        assertThat(parameter1).isNotEqualTo(parameter2);

        parameter1.setParameterId(null);
        assertThat(parameter1).isNotEqualTo(parameter2);
    }
}
