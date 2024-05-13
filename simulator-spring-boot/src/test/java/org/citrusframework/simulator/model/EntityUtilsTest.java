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

import jakarta.persistence.Column;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityUtilsTest {

    @Test
    void truncateToColumnSizePerformed() {
        String message = "The quick brown fox jumps over the lazy dog";
        String truncatedMessage = EntityUtils.truncateToColumnSize(TestEntity.class, "message",
            message);
        assertEquals("The quick brown fox jumps over the la", truncatedMessage);
    }

    @Test
    void truncateToColumnSizeNotPerformed() {
        String message = "Waltz, bad nymph, for quick jigs vex";
        String truncatedMessage = EntityUtils.truncateToColumnSize(TestEntity.class, "message",
            message);
        assertEquals(message, truncatedMessage);
    }

    @Test
    void truncateToColumnSizeExceptionOnUnknownProperty() {
        String message = "Sphinx of black quartz, judge my vow.";
        Assertions.assertThrows(CitrusRuntimeException.class, () -> EntityUtils.truncateToColumnSize(TestEntity.class, "otherMessage",
            message));
    }

    public static class TestEntity {

        @Column(length=37)
        String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
