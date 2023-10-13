package org.citrusframework.simulator.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MessageTest {

    @Test
    void equalsVerifier() throws Exception {
        EntityUtils.equalsVerifier(Message.class);

        Message message1 = new Message();
        message1.setMessageId(1L);

        Message message2 = new Message();
        message2.setMessageId(message1.getMessageId());

        assertThat(message1).isEqualTo(message2);

        message2.setMessageId(2L);
        assertThat(message1).isNotEqualTo(message2);

        message1.setMessageId(null);
        assertThat(message1).isNotEqualTo(message2);
    }
}
