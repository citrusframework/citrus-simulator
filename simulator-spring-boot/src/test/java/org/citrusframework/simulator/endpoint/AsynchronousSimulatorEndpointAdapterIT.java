package org.citrusframework.simulator.endpoint;

import org.citrusframework.simulator.exception.SimulatorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.throwable;

@Isolated
@DirtiesContext
@TestPropertySource(properties = {"citrus.simulator.mode=async"})
class AsynchronousSimulatorEndpointAdapterIT extends SimulatorEndpointAdapterIT {

    @Test
    void dispatchMessage_returnsExceptionMessage_ifUnderlyingScenarioExecutionFails() {
        verifyFailingScenarioThrowsResponseStatusException(
            e -> assertThat(e).extracting(ResponseStatusException::getCause)
                .asInstanceOf(throwable(SimulatorException.class))
                .hasMessage(FAIL_WITH_PURPOSE)
        );
    }
}
