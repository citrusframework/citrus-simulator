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

class MessageHeaderTest {

    @Test
    void equalsVerifier() throws Exception {
        EntityTestUtils.equalsVerifier(MessageHeader.class);

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
