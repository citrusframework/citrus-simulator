package org.citrusframework.simulator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.persistence.Column;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
