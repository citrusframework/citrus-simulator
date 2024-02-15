package org.citrusframework.simulator.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScenarioActionTest {

    @Test
    void equalsVerifier() throws Exception {
        EntityTestUtils.equalsVerifier(ScenarioAction.class);

        ScenarioAction action1 = new ScenarioAction();
        action1.setActionId(1L);

        ScenarioAction action2 = new ScenarioAction();
        action2.setActionId(action1.getActionId());

        assertThat(action1).isEqualTo(action2);

        action2.setActionId(2L);
        assertThat(action1).isNotEqualTo(action2);

        action1.setActionId(null);
        assertThat(action1).isNotEqualTo(action2);
    }
}
