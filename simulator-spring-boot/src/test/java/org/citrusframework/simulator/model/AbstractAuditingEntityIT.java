package org.citrusframework.simulator.model;

import org.assertj.core.data.TemporalUnitLessThanOffset;
import org.citrusframework.simulator.common.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class AbstractAuditingEntityIT {

    public static final TemporalUnitLessThanOffset LESS_THAN_1_SECOND = new TemporalUnitLessThanOffset(1, SECONDS);

    AuditedEntity fixture;

    @BeforeEach
    void setup() {
        fixture = new AuditedEntity();
    }

    @Test
    void shouldUseCorrectTime() {
        var timeProvider = new TimeProvider();
        assertThat(fixture.getCreatedDate()).isCloseTo(timeProvider.getTimeNow(), LESS_THAN_1_SECOND);
        assertThat(fixture.getLastModifiedDate()).isCloseTo(timeProvider.getTimeNow(), LESS_THAN_1_SECOND);
    }

    private static class AuditedEntity extends AbstractAuditingEntity<AuditedEntity, Long> {
    }
}
