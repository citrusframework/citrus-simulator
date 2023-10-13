package org.citrusframework.simulator.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

class TimeProvider {

    TimeProvider() {
        // Separate class that allows mocking
    }

    Instant getTimeNow() {
        return LocalDateTime.now().toInstant(ZoneOffset.UTC);
    }
}
