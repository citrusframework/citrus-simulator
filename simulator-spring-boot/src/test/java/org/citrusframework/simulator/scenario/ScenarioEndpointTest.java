package org.citrusframework.simulator.scenario;

import org.citrusframework.message.Message;
import org.citrusframework.simulator.endpoint.SimulationFailedUnexpectedlyException;
import org.citrusframework.simulator.exception.SimulatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ScenarioEndpointTest {

    private ScenarioEndpoint fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new ScenarioEndpoint(null);
    }

    @Nested
    class Fail {

        @Test
        void throwsExceptionWhenNoResponseFuturePresent() {
            assertThatThrownBy(() -> fixture.fail(null))
                .isInstanceOf(SimulatorException.class)
                .hasMessage("Failed to process scenario response message - missing response consumer!");
        }

        @Test
        void completesResponseFutureIfOneIsPresent() {
            CompletableFuture<Message> responseFuture = new CompletableFuture<>();
            fixture.add(mock(Message.class), responseFuture);

            var cause = mock(Throwable.class);
            fixture.fail(cause);

            assertThat(responseFuture)
                .isCompleted();
            assertThat(responseFuture.join())
                .isInstanceOf(SimulationFailedUnexpectedlyException.class)
                .extracting(Message::getPayload)
                .isEqualTo(cause);
        }
    }
}
