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

package org.citrusframework.simulator.scenario;

import org.citrusframework.message.Message;
import org.citrusframework.simulator.endpoint.SimulationFailedUnexpectedlyException;
import org.citrusframework.simulator.exception.SimulatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
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
        void completesResponseFutureIfOneIsPresent() throws ExecutionException, InterruptedException, TimeoutException {
            CompletableFuture<Message> responseFuture = new CompletableFuture<>();
            fixture.add(mock(Message.class), responseFuture);

            var cause = mock(Throwable.class);
            fixture.fail(cause);

            assertThat(responseFuture)
                .isCompleted();
            assertThat(responseFuture.get(1, MILLISECONDS))
                .isInstanceOf(SimulationFailedUnexpectedlyException.class)
                .extracting(Message::getPayload)
                .isEqualTo(cause);
        }

        @Test
        void completesResponseFuturesInFIFOOrder() throws ExecutionException, InterruptedException, TimeoutException {
            CompletableFuture<Message> responseFuture1 = new CompletableFuture<>();
            fixture.add(mock(Message.class), responseFuture1);

            CompletableFuture<Message> responseFuture2 = new CompletableFuture<>();
            fixture.add(mock(Message.class), responseFuture2);

            var cause1 = mock(Throwable.class);
            fixture.fail(cause1);

            assertThat(responseFuture1)
                .isCompleted();

            assertThat(responseFuture2)
                .isNotCompleted();

            assertThat(responseFuture1.get(1, MILLISECONDS))
                .isInstanceOf(SimulationFailedUnexpectedlyException.class)
                .extracting(Message::getPayload)
                .isEqualTo(cause1);

            var cause2 = mock(Throwable.class);
            fixture.fail(cause2);

            assertThat(responseFuture2)
                .isCompleted();

            assertThat(responseFuture2.get(1, MILLISECONDS))
                .isInstanceOf(SimulationFailedUnexpectedlyException.class)
                .extracting(Message::getPayload)
                .isEqualTo(cause2);
        }
    }
}
