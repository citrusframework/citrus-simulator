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

import org.citrusframework.context.TestContext;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.message.Message;
import org.citrusframework.simulator.endpoint.EndpointMessageHandler;
import org.citrusframework.simulator.endpoint.SimulationFailedUnexpectedlyException;
import org.citrusframework.simulator.exception.SimulatorException;
import org.citrusframework.spi.ReferenceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

import static java.util.Objects.nonNull;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.locks.LockSupport.parkNanos;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentCaptor.captor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith({MockitoExtension.class})
class ScenarioEndpointTest {

    @Mock
    private EndpointMessageHandler endpointMessageHandlerMock;

    @Mock
    private ScenarioEndpointConfiguration scenarioEndpointConfigurationMock;

    private ScenarioEndpoint fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new ScenarioEndpoint(scenarioEndpointConfigurationMock);
    }

    @Nested
    class CreateProducer {

        @Test
        void shouldReturnSelf() {
            assertThat(fixture.createProducer()).isSameAs(fixture);
        }
    }

    @Nested
    class CreateConsumer {

        @Test
        void shouldReturnSelf() {
            assertThat(fixture.createConsumer()).isSameAs(fixture);
        }
    }

    @Nested
    class Fail {

        @Test
        @Disabled
        void shouldThrowException_ifNoResponseFutureIsPresent() {
            var testContextMock = mock(TestContext.class);

            assertThatThrownBy(() -> fixture.fail(new CitrusRuntimeException()))
                .isInstanceOf(SimulatorException.class)
                .hasMessage("Failed to receive scenario inbound message");

            verifyNoInteractions(testContextMock);
        }

        @Test
        void shouldPollNextMessageFromChannel_ifNoneHasBeenReceived() {
            CompletableFuture<Message> responseFuture = mock();
            fixture.add(mock(Message.class), responseFuture);

            var cause = mock(Throwable.class);
            fixture.fail(cause);

            ArgumentCaptor<SimulationFailedUnexpectedlyException> responseMessageArgumentCaptor = captor();
            verify(responseFuture).complete(responseMessageArgumentCaptor.capture());

            assertThat(responseMessageArgumentCaptor.getValue())
                .isNotNull()
                .isInstanceOf(SimulationFailedUnexpectedlyException.class)
                .extracting(Message::getPayload)
                .isEqualTo(cause);
        }

        @Test
        void shouldCompleteSingleResponseFuture_ifOneIsPresent() {
            CompletableFuture<Message> responseFuture = mock();
            fixture.add(mock(Message.class), responseFuture);

            var cause = mock(Throwable.class);
            fixture.fail(cause);

            ArgumentCaptor<SimulationFailedUnexpectedlyException> responseMessageArgumentCaptor = captor();
            verify(responseFuture).complete(responseMessageArgumentCaptor.capture());

            assertThat(responseMessageArgumentCaptor.getValue())
                .isNotNull()
                .isInstanceOf(SimulationFailedUnexpectedlyException.class)
                .extracting(Message::getPayload)
                .isEqualTo(cause);
        }

        @Test
        @Disabled
        void shouldResolveFuturesCorrectlyIn_FIFO_Order() {
            var params = addAndReceiveTwoMessagesInOrder();

            var cause1 = mock(Throwable.class);
            fixture.fail(cause1);

            verify(params.responseFuture1()).complete(any(SimulationFailedUnexpectedlyException.class));
            verifyNoInteractions(params.responseFuture2());

            var cause2 = mock(Throwable.class);
            fixture.fail(cause2);

            verify(params.responseFuture2()).complete(any(SimulationFailedUnexpectedlyException.class));
            verifyNoMoreInteractions(params.responseFuture1(), params.responseFuture2());
        }

        @Test
        void shouldResolveFuturesCorrectlyIn_FILO_Order() {
            var params = addAndReceiveTwoMessagesInOrder();

            var cause1 = mock(Throwable.class);
            fixture.fail(cause1);

            verify(params.responseFuture2()).complete(any(SimulationFailedUnexpectedlyException.class));
            verifyNoInteractions(params.responseFuture1());

            var cause2 = mock(Throwable.class);
            fixture.fail(cause2);

            verify(params.responseFuture1()).complete(any(SimulationFailedUnexpectedlyException.class));
            verifyNoMoreInteractions(params.responseFuture1(), params.responseFuture2());
        }
    }

    @Nested
    class Send {

        @Test
        void shouldPollNextMessageFromChannel_ifNoneHasBeenReceived() {
            var message = mock(Message.class);
            CompletableFuture<Message> responseFuture = mock();

            fixture.add(message, responseFuture);

            fixture.send(message, mockTestContext());

            verify(responseFuture).complete(message);
        }

        @Test
        @Disabled
        void shouldResolveFuturesCorrectlyIn_FIFO_Order() {
            var params = addAndReceiveTwoMessagesInOrder();

            fixture.send(params.message1(), params.testContext1());

            verify(params.responseFuture1()).complete(params.message1());
            verifyNoInteractions(params.responseFuture2());

            fixture.send(params.message2(), params.testContext2());

            verify(params.responseFuture2()).complete(params.message2());
            verifyNoMoreInteractions(params.responseFuture1(), params.responseFuture2());
        }

        @Test
        void shouldResolveFuturesCorrectlyIn_FILO_Order() {
            var params = addAndReceiveTwoMessagesInOrder();

            fixture.send(params.message2(), params.testContext2());

            verify(params.responseFuture2()).complete(params.message2());
            verifyNoInteractions(params.responseFuture1());

            fixture.send(params.message1(), params.testContext1());

            verify(params.responseFuture1()).complete(params.message1());
            verifyNoMoreInteractions(params.responseFuture1(), params.responseFuture2());
        }

        @Test
        void shouldSkipUnresolvedFutures() {
            var params = addAndReceiveTwoMessagesInOrder();

            fixture.send(params.message2(), params.testContext2());

            verify(params.responseFuture2()).complete(params.message2());
            verifyNoMoreInteractions(params.responseFuture2());
            verifyNoInteractions(params.responseFuture1());
        }

        @Test
        void shouldBeThreadSafe() throws InterruptedException {
            var threadCount = 10;
            ExecutorService executorService = null;

            try {
                executorService = newFixedThreadPool(threadCount);
                spawnAndCompleteScenarioExecutionsForThreads(threadCount, executorService);
            } finally {
                if (nonNull(executorService)) {
                    executorService.shutdownNow();
                }
            }
        }

        @SuppressWarnings({"unchecked"})
        private void spawnAndCompleteScenarioExecutionsForThreads(int threadCount, ExecutorService executorService) throws InterruptedException {
            var latch = new CountDownLatch(threadCount);

            var testContexts = new TestContext[threadCount];
            var responseFutures = new CompletableFuture<?>[threadCount];
            var requests = new Message[threadCount];
            var responses = new Message[threadCount];

            for (int i = 0; i < threadCount; i++) {
                testContexts[i] = mockTestContext();
                responseFutures[i] = new CompletableFuture<Message>();
                requests[i] = mock();
                responses[i] = mock();

                fixture.add(requests[i], (CompletableFuture<Message>) responseFutures[i]);
            }

            for (int i = 0; i < threadCount; i++) {
                final var index = i;
                executorService.submit(() -> {
                    try {
                        executeScenario(testContexts[index], requests[index], responses[index], (CompletableFuture<Message>) responseFutures[index]);
                    } catch (Exception e) {
                        throw new CitrusRuntimeException(e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            if (!latch.await(300, MILLISECONDS)) {
                throw new AssertionError("Not all tasks completed in time!");
            }
        }

        private void executeScenario(TestContext testContext, Message request, Message response, CompletableFuture<Message> responseFuture) throws InterruptedException, ExecutionException, TimeoutException {
            var received = fixture.receive(testContext);
            assertThat(received)
                .isNotNull()
                .isEqualTo(request);

            parkNanos(Duration.ofMillis(ThreadLocalRandom.current().nextInt(10, 100)).toNanos());

            fixture.send(response, testContext);

            var result = responseFuture.get(200, MILLISECONDS);
            assertThat(result)
                .isNotNull()
                .isEqualTo(response);
        }
    }

    private ConcurrentTestExecutionParams addAndReceiveTwoMessagesInOrder() {
        var testContext1 = mockTestContext();
        var message1 = mock(Message.class);
        CompletableFuture<Message> responseFuture1 = mock();

        fixture.add(message1, responseFuture1);

        var testContext2 = mockTestContext();
        var message2 = mock(Message.class);
        CompletableFuture<Message> responseFuture2 = mock();

        fixture.add(message2, responseFuture2);

        var receiveMessage1 = fixture.receive(testContext1);
        assertThat(receiveMessage1)
            .isNotNull()
            .isEqualTo(message1);

        var receiveMessage2 = fixture.receive(testContext2);
        assertThat(receiveMessage2)
            .isNotNull()
            .isEqualTo(message2);

        return new ConcurrentTestExecutionParams(testContext1, message1, responseFuture1, testContext2, message2, responseFuture2);
    }

    private TestContext mockTestContext() {
        var testContextMock = mock(TestContext.class);
        var referenceResolverMock = mock(ReferenceResolver.class);

        doReturn(referenceResolverMock).when(testContextMock).getReferenceResolver();
        doReturn(endpointMessageHandlerMock).when(referenceResolverMock).resolve(EndpointMessageHandler.class);

        return testContextMock;
    }

    private record ConcurrentTestExecutionParams(TestContext testContext1, Message message1,
                                                 CompletableFuture<Message> responseFuture1,
                                                 TestContext testContext2, Message message2,
                                                 CompletableFuture<Message> responseFuture2) {
    }
}
