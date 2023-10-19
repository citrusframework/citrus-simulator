package org.citrusframework.simulator.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageHeaderTest {

    @Test
    void equalsVerifier() throws Exception {
        EntityUtils.equalsVerifier(MessageHeader.class);

        MessageHeader messageHeader1 = new MessageHeader();
        messageHeader1.setHeaderId(1L);

        MessageHeader messageHeader2 = new MessageHeader();
        messageHeader2.setHeaderId(messageHeader1.getHeaderId());

        assertThat(messageHeader1).isEqualTo(messageHeader2);

        messageHeader2.setHeaderId(2L);
        assertThat(messageHeader1).isNotEqualTo(messageHeader2);

        messageHeader1.setHeaderId(null);
        assertThat(messageHeader1).isNotEqualTo(messageHeader2);
    }
}
