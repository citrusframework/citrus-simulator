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
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.nonNull;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.locks.LockSupport.parkNanos;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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
        void shouldRemoveActiveFutureAfterReceive() {
            var testContext = mockTestContext();
            var request = mock(Message.class);
            CompletableFuture<Message> responseFuture = mock();

            fixture.add(request, responseFuture);
            fixture.receive(testContext);

            fixture.fail(mock(Throwable.class));

            assertThatThrownBy(() -> fixture.send(mock(Message.class), testContext))
                .isInstanceOf(SimulatorException.class)
                .hasMessage("Failed to process scenario response message - missing response consumer!");
        }
    }

    @Nested
    class CancelTest {

        @Test
        void shouldRemovePendingFutureFromCollections() {
            var request = mock(Message.class);
            CompletableFuture<Message> responseFuture = mock();
            fixture.add(request, responseFuture);

            fixture.cancel(responseFuture);

            assertThatThrownBy(() -> fixture.fail(new CitrusRuntimeException()))
                .isInstanceOf(SimulatorException.class)
                .hasMessage("Failed to receive scenario inbound message");
        }
    }

    @Nested
    class FailWithContext {

        @Test
        void shouldFailResponseFutureOfReceivedRequest() throws Exception {
            var testContext = mockTestContext();
            var cause = new CitrusRuntimeException("boom");
            var responseFuture = new CompletableFuture<Message>();

            fixture.add(mock(Message.class), responseFuture);
            fixture.receive(testContext);

            assertThat(fixture.fail(testContext, cause))
                .isTrue();

            assertThat(responseFuture.get(0, MILLISECONDS))
                .isInstanceOf(SimulationFailedUnexpectedlyException.class)
                .extracting(message -> message.getPayload(Throwable.class))
                .isSameAs(cause);
        }

        @Test
        void shouldNotTouchResponseFuturesOfOtherContexts() {
            var params = addAndReceiveTwoMessagesInOrder();

            fixture.fail(params.testContext2(), new CitrusRuntimeException());

            verify(params.responseFuture2()).complete(any(SimulationFailedUnexpectedlyException.class));
            verifyNoInteractions(params.responseFuture1());
        }

        @Test
        void shouldDoNothing_ifResponseHasAlreadyBeenSent() {
            var testContext = mockTestContext();
            var response = mock(Message.class);
            CompletableFuture<Message> responseFuture = mock();

            fixture.add(mock(Message.class), responseFuture);
            fixture.receive(testContext);
            fixture.send(response, testContext);

            fixture.fail(testContext, new CitrusRuntimeException());

            verify(responseFuture).complete(response);
            verifyNoMoreInteractions(responseFuture);
        }

        @Test
        void shouldDoNothing_ifNoRequestHasBeenReceivedInContext() {
            CompletableFuture<Message> responseFuture = mock();

            fixture.add(mock(Message.class), responseFuture);

            assertThat(fixture.fail(mock(TestContext.class), new CitrusRuntimeException()))
                .isFalse();

            verifyNoInteractions(responseFuture);
        }
    }

    @Nested
    class BoundRequest {

        @Test
        void shouldReceiveBoundRequest_withoutMessageChannel() {
            var testContext = mockTestContext();
            var request = mock(Message.class);

            fixture.bind(testContext, request, new CompletableFuture<>());

            assertThat(fixture.receive(testContext, 0))
                .isSameAs(request);

            verify(endpointMessageHandlerMock).handleReceivedMessage(request, testContext);
        }

        @Test
        void shouldReceiveBoundRequestOnlyOnce() {
            var testContext = mockTestContext();

            fixture.bind(testContext, mock(Message.class), new CompletableFuture<>());
            fixture.receive(testContext, 0);

            assertThatThrownBy(() -> fixture.receive(testContext, 0))
                .isInstanceOf(SimulatorException.class)
                .hasMessage("Failed to receive scenario inbound message");
        }

        @Test
        void shouldNotReceiveRequestBoundToOtherContext() {
            fixture.bind(mock(TestContext.class), mock(Message.class), new CompletableFuture<>());

            assertThatThrownBy(() -> fixture.receive(mock(TestContext.class), 0))
                .isInstanceOf(SimulatorException.class)
                .hasMessage("Failed to receive scenario inbound message");
        }

        @Test
        void shouldCompleteResponseFuture_onSend() {
            var testContext = mockTestContext();
            var response = mock(Message.class);
            var responseFuture = new CompletableFuture<Message>();

            fixture.bind(testContext, mock(Message.class), responseFuture);
            fixture.receive(testContext, 0);
            fixture.send(response, testContext);

            assertThat(responseFuture)
                .isCompletedWithValue(response);

            verify(endpointMessageHandlerMock).handleSentMessage(response, testContext);
        }

        @Test
        void shouldKeepFirstResponse() {
            var testContext = mockTestContext();
            var response = mock(Message.class);
            var responseFuture = new CompletableFuture<Message>();

            fixture.bind(testContext, mock(Message.class), responseFuture);
            fixture.send(response, testContext);
            fixture.send(mock(Message.class), testContext);

            assertThat(responseFuture)
                .isCompletedWithValue(response);
        }

        @Test
        void shouldNotAnswerQueuedRequests() {
            var testContext = mockTestContext();
            CompletableFuture<Message> queuedResponseFuture = mock();

            fixture.add(mock(Message.class), queuedResponseFuture);
            fixture.bind(testContext, mock(Message.class), new CompletableFuture<>());

            fixture.send(mock(Message.class), testContext);

            verifyNoInteractions(queuedResponseFuture);
        }

        @Test
        void shouldAnswerIntermediateRequestBeforeBoundRequest() {
            var testContext = mockTestContext();
            var boundResponse = mock(Message.class);
            var intermediateResponse = mock(Message.class);
            var boundResponseFuture = new CompletableFuture<Message>();
            CompletableFuture<Message> intermediateResponseFuture = mock();

            fixture.bind(testContext, mock(Message.class), boundResponseFuture);
            fixture.receive(testContext, 0);
            fixture.send(boundResponse, testContext);

            fixture.add(mock(Message.class), intermediateResponseFuture);
            fixture.receive(testContext, 0);
            fixture.send(intermediateResponse, testContext);

            verify(intermediateResponseFuture).complete(intermediateResponse);
            assertThat(boundResponseFuture)
                .isCompletedWithValue(boundResponse);
        }

        @Test
        void shouldFailBoundRequest() throws Exception {
            var testContext = mockTestContext();
            var cause = new CitrusRuntimeException("boom");
            var responseFuture = new CompletableFuture<Message>();

            fixture.bind(testContext, mock(Message.class), responseFuture);
            fixture.receive(testContext, 0);

            assertThat(fixture.fail(testContext, cause))
                .isTrue();

            assertThat(responseFuture.get(0, MILLISECONDS))
                .isInstanceOf(SimulationFailedUnexpectedlyException.class)
                .extracting(message -> message.getPayload(Throwable.class))
                .isSameAs(cause);
        }

        @Test
        void shouldNotOverrideResponse_onFail() {
            var testContext = mockTestContext();
            var response = mock(Message.class);
            var responseFuture = new CompletableFuture<Message>();

            fixture.bind(testContext, mock(Message.class), responseFuture);
            fixture.send(response, testContext);

            assertThat(fixture.fail(testContext, new CitrusRuntimeException()))
                .isTrue();

            assertThat(responseFuture)
                .isCompletedWithValue(response);
        }

        @Test
        void shouldFailBoundAndIntermediateRequest() {
            var testContext = mockTestContext();
            var boundResponseFuture = new CompletableFuture<Message>();
            CompletableFuture<Message> intermediateResponseFuture = mock();

            fixture.bind(testContext, mock(Message.class), boundResponseFuture);
            fixture.receive(testContext, 0);

            fixture.add(mock(Message.class), intermediateResponseFuture);
            fixture.receive(testContext, 0);

            fixture.fail(testContext, new CitrusRuntimeException());

            verify(intermediateResponseFuture).complete(any(SimulationFailedUnexpectedlyException.class));
            assertThat(boundResponseFuture.getNow(null))
                .isInstanceOf(SimulationFailedUnexpectedlyException.class);
        }

        @Test
        void shouldIsolateConcurrentExecutions() throws InterruptedException {
            var threadCount = 10;
            var executorService = newFixedThreadPool(threadCount);

            try {
                var latch = new CountDownLatch(threadCount);
                var mismatches = new AtomicInteger();

                for (int i = 0; i < threadCount; i++) {
                    var testContext = mockTestContext();
                    var request = mock(Message.class);
                    var response = mock(Message.class);
                    var responseFuture = new CompletableFuture<Message>();

                    executorService.submit(() -> {
                        try {
                            fixture.bind(testContext, request, responseFuture);

                            if (fixture.receive(testContext, 0) != request) {
                                mismatches.incrementAndGet();
                            }

                            parkNanos(Duration.ofMillis(ThreadLocalRandom.current().nextInt(10, 50)).toNanos());
                            fixture.send(response, testContext);
                            fixture.release(testContext);

                            if (responseFuture.getNow(null) != response) {
                                mismatches.incrementAndGet();
                            }
                        } finally {
                            latch.countDown();
                        }
                    });
                }

                assertThat(latch.await(500, MILLISECONDS))
                    .isTrue();
                assertThat(mismatches)
                    .hasValue(0);
            } finally {
                executorService.shutdownNow();
            }
        }
    }

    @Nested
    class Release {

        @Test
        void shouldAnswerUnansweredBoundRequestWithoutResponse() {
            var testContext = mockTestContext();
            var responseFuture = new CompletableFuture<Message>();

            fixture.bind(testContext, mock(Message.class), responseFuture);
            fixture.receive(testContext, 0);

            fixture.release(testContext);

            assertThat(responseFuture)
                .isCompletedWithValue(null);
        }

        @Test
        void shouldAnswerBoundRequestThatHasNeverBeenReceivedWithoutResponse() {
            var testContext = mock(TestContext.class);
            var responseFuture = new CompletableFuture<Message>();

            fixture.bind(testContext, mock(Message.class), responseFuture);

            fixture.release(testContext);

            assertThat(responseFuture)
                .isCompletedWithValue(null);
        }

        @Test
        void shouldNotOverrideResponse() {
            var testContext = mockTestContext();
            var response = mock(Message.class);
            var responseFuture = new CompletableFuture<Message>();

            fixture.bind(testContext, mock(Message.class), responseFuture);
            fixture.send(response, testContext);

            fixture.release(testContext);

            assertThat(responseFuture)
                .isCompletedWithValue(response);
        }

        @Test
        void shouldRemoveBoundRequest() {
            var testContext = mock(TestContext.class);
            var responseFuture = new CompletableFuture<Message>();

            fixture.bind(testContext, mock(Message.class), responseFuture);
            fixture.release(testContext);

            assertThatThrownBy(() -> fixture.receive(testContext, 0))
                .isInstanceOf(SimulatorException.class)
                .hasMessage("Failed to receive scenario inbound message");
        }

        @Test
        void shouldAnswerUnansweredIntermediateRequestWithoutResponse() {
            var testContext = mockTestContext();
            CompletableFuture<Message> intermediateResponseFuture = mock();

            fixture.add(mock(Message.class), intermediateResponseFuture);
            fixture.receive(testContext, 0);

            fixture.release(testContext);

            verify(intermediateResponseFuture).complete(null);
        }

        @Test
        void shouldNotTouchRequestsOfOtherContexts() {
            var responseFuture = new CompletableFuture<Message>();
            CompletableFuture<Message> queuedResponseFuture = mock();

            fixture.bind(mock(TestContext.class), mock(Message.class), responseFuture);
            fixture.add(mock(Message.class), queuedResponseFuture);

            fixture.release(mock(TestContext.class));

            assertThat(responseFuture)
                .isNotDone();
            verifyNoInteractions(queuedResponseFuture);
        }

        @Test
        void shouldIgnoreMissingContext() {
            assertThatCode(() -> fixture.release(null))
                .doesNotThrowAnyException();
        }
    }

    @Nested
    class Send {

        @Test
        void shouldCompleteResponseFuture_whenReceiveThenSend() {
            var testContext = mockTestContext();
            var message = mock(Message.class);
            CompletableFuture<Message> responseFuture = mock();

            fixture.add(message, responseFuture);
            fixture.receive(testContext);

            fixture.send(message, testContext);

            verify(responseFuture).complete(message);
        }

        @Test
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
