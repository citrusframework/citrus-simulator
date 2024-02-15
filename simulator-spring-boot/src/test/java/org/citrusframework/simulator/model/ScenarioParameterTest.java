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
