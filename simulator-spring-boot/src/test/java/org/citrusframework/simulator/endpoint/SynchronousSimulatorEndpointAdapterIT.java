package org.citrusframework.simulator.endpoint;

import org.citrusframework.exceptions.TestCaseFailedException;
import org.citrusframework.simulator.exception.SimulatorException;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.throwable;

class SynchronousSimulatorEndpointAdapterIT extends SimulatorEndpointAdapterIT {

    @Test
    void dispatchMessage_returnsExceptionMessage_ifUnderlyingScenarioExecutionFails() {
        verifyFailingScenarioThrowsResponseStatusException(
            e -> assertThat(e).extracting(ResponseStatusException::getCause)
                .asInstanceOf(throwable(TestCaseFailedException.class))
                .rootCause()
                .asInstanceOf(throwable(SimulatorException.class))
                .hasMessage(FAIL_WITH_PURPOSE)
        );
    }
}
