package org.citrusframework.simulator.endpoint;

import org.citrusframework.simulator.exception.SimulatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.citrusframework.simulator.endpoint.SimulationFailedUnexpectedlyException.EXCEPTION_TYPE;

class SimulationFailedUnexpectedlyExceptionTest {

    private static final Throwable TEST_THROWABLE = new SimulatorException("Huston, we hav a problem!");

    private SimulationFailedUnexpectedlyException fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new SimulationFailedUnexpectedlyException(TEST_THROWABLE);
    }

    @Test
    void typeIsStatic() {
        assertThat(fixture)
            .extracting(SimulationFailedUnexpectedlyException::getType)
            .isEqualTo(EXCEPTION_TYPE);
    }
}
