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

package org.citrusframework.simulator.endpoint;

import jakarta.annotation.Nullable;
import org.citrusframework.base.endpoint.adapter.EmptyResponseEndpointAdapter;
import org.citrusframework.context.TestContext;
import org.citrusframework.endpoint.EndpointAdapter;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.exceptions.TestCaseFailedException;
import org.citrusframework.message.Message;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.correlation.CorrelationHandlerRegistry;
import org.citrusframework.simulator.exception.SimulatorException;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.throwable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class SimulatorEndpointAdapterTest {

    private static final String SCENARIO_NAME = "testScenario";

    @Mock
    private ApplicationContext applicationContextMock;

    @Mock
    private CorrelationHandlerRegistry handlerRegistryMock;

    @Mock
    private ScenarioExecutorService scenarioExecutorServiceMock;

    @Mock
    private SimulatorConfigurationProperties simulatorConfigurationMock;

    @Mock
    private SimulatorScenario scenarioMock;

    @Mock
    private ScenarioEndpoint scenarioEndpointMock;

    @Mock
    private Message requestMessageMokc;

    @Mock
    private EndpointAdapter customFallbackEndpointAdapterMock;

    private static Message failedTestActionResponse() {
        return new SimulationFailedUnexpectedlyException(new TestCaseFailedException(new CitrusRuntimeException("Fail with purpose!")));
    }

    @AfterEach
    void clearInterruptedStatus() {
        if (Thread.interrupted()) {
            // Clear interrupted status to avoid spilling over between tests.
        }
    }

    @Nested
    class DispatchMessageTest {
        @Test
        void shouldCancelFuture_onTimeout() {
            var fixture = createFixture();

            when(simulatorConfigurationMock.getDefaultTimeout()).thenReturn(1L);

            fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME);

            verify(scenarioEndpointMock).cancel(anyFuture());
        }

        @Test
        void shouldCancelFuture_onExecutionException() {
            var fixture = createFixture();
            var responseFutureRef = new AtomicReference<CompletableFuture<Message>>();

            when(simulatorConfigurationMock.getDefaultTimeout()).thenReturn(50L);
            doAnswer(invocation -> {
                responseFutureRef.set(invocation.getArgument(1));
                return null;
            }).when(scenarioEndpointMock).add(eq(requestMessageMokc), anyFuture());
            doAnswer(invocation -> {
                responseFutureRef.get().completeExceptionally(new IllegalStateException("boom"));
                return null;
            }).when(scenarioExecutorServiceMock).run(eq(scenarioMock), eq(SCENARIO_NAME), anyList());

            assertThatThrownBy(() -> fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .isInstanceOf(SimulatorException.class);

            verify(scenarioEndpointMock).cancel(responseFutureRef.get());
        }

        @Test
        void shouldCancelFuture_onInterruptedException() {
            var fixture = createFixture();

            when(simulatorConfigurationMock.getDefaultTimeout()).thenReturn(50L);
            doAnswer(invocation -> {
                Thread.currentThread().interrupt();
                return null;
            }).when(scenarioExecutorServiceMock).run(eq(scenarioMock), eq(SCENARIO_NAME), anyList());

            assertThatThrownBy(() -> fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .isInstanceOf(SimulatorException.class);

            verify(scenarioEndpointMock).cancel(anyFuture());
        }

        @Test
        void shouldCancelFuture_onSynchronousRunFailure() {
            var fixture = createFixture();

            doThrow(new IllegalStateException("sync-run-failed"))
                .when(scenarioExecutorServiceMock).run(eq(scenarioMock), eq(SCENARIO_NAME), anyList());

            assertThatThrownBy(() -> fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .isInstanceOf(ResponseStatusException.class);

            verify(scenarioEndpointMock).cancel(anyFuture());
        }

        @Test
        void shouldDelegateFailedTestActionToCustomFallbackEndpointAdapter() {
            var fixture = createFixture();
            fixture.setFallbackEndpointAdapter(customFallbackEndpointAdapterMock);

            when(simulatorConfigurationMock.getDefaultTimeout()).thenReturn(50L);
            doAnswer(invocation -> {
                invocation.<CompletableFuture<Message>>getArgument(1).complete(failedTestActionResponse());
                return null;
            }).when(scenarioEndpointMock).add(eq(requestMessageMokc), anyFuture());

            assertThat(fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .isNull();
        }

        @Test
        void shouldThrowResponseStatusException_ifTestActionFailed_withoutCustomFallbackEndpointAdapter() {
            var fixture = createFixture();
            fixture.setFallbackEndpointAdapter(new EmptyResponseEndpointAdapter());

            when(simulatorConfigurationMock.getDefaultTimeout()).thenReturn(50L);
            doAnswer(invocation -> {
                invocation.<CompletableFuture<Message>>getArgument(1).complete(failedTestActionResponse());
                return null;
            }).when(scenarioEndpointMock).add(eq(requestMessageMokc), anyFuture());

            assertThatThrownBy(() -> fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .asInstanceOf(throwable(ResponseStatusException.class))
                .extracting(ResponseStatusException::getStatusCode)
                .isEqualTo(HttpStatusCode.valueOf(555));
        }

        @Test
        void shouldNotAwaitResponse_ifSynchronousExecutorCompletedWithoutResponse() {
            var fixture = createFixture();

            when(scenarioExecutorServiceMock.isSynchronous()).thenReturn(true);

            assertThat(fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .isNull();

            verify(scenarioEndpointMock).cancel(anyFuture());
            verify(simulatorConfigurationMock, never()).getDefaultTimeout();
        }

        @Test
        void shouldThrowServiceUnavailable_andCancelFuture_ifExecutionIsRejected() {
            var fixture = createFixture();

            doThrow(new RejectedExecutionException("queue is full"))
                .when(scenarioExecutorServiceMock).run(eq(scenarioMock), eq(SCENARIO_NAME), anyList());

            assertThatThrownBy(() -> fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .asInstanceOf(throwable(ResponseStatusException.class))
                .extracting(ResponseStatusException::getStatusCode)
                .isEqualTo(HttpStatusCode.valueOf(503));

            verify(scenarioEndpointMock).cancel(anyFuture());
        }

        private SimulatorEndpointAdapter createFixture() {
            when(applicationContextMock.containsBean(SCENARIO_NAME)).thenReturn(true);
            when(applicationContextMock.getBean(SCENARIO_NAME, SimulatorScenario.class)).thenReturn(scenarioMock);
            when(scenarioMock.getScenarioEndpoint()).thenReturn(scenarioEndpointMock);

            return new SimulatorEndpointAdapter(applicationContextMock, handlerRegistryMock, scenarioExecutorServiceMock, simulatorConfigurationMock);
        }
    }

    @Nested
    class DispatchMessageToExecutionTest {

        @Mock
        private TestContext testContextMock;

        @Mock
        private Message responseMessageMock;

        private SimulatorEndpointAdapter fixture;

        @BeforeEach
        void beforeEachSetup() {
            when(applicationContextMock.containsBean(SCENARIO_NAME)).thenReturn(true);
            when(applicationContextMock.getBean(SCENARIO_NAME, SimulatorScenario.class)).thenReturn(scenarioMock);
            when(scenarioMock.getScenarioEndpoint()).thenReturn(scenarioEndpointMock);
            when(scenarioExecutorServiceMock.supportsTestContextInitialization()).thenReturn(true);
            lenient().when(simulatorConfigurationMock.getDefaultTimeout()).thenReturn(50L);

            fixture = new SimulatorEndpointAdapter(applicationContextMock, handlerRegistryMock, scenarioExecutorServiceMock, simulatorConfigurationMock);
        }

        @Test
        void shouldBindRequestToExecutionContext_andReturnResponse() {
            mockScenarioExecutionAnsweringWith(responseMessageMock);

            assertThat(fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .isSameAs(responseMessageMock);

            verify(scenarioEndpointMock).bind(eq(testContextMock), eq(requestMessageMokc), anyFuture());
            verifyNoChannelHandOff();
        }

        @Test
        void shouldReturnNull_ifExecutionEndedWithoutResponse() {
            mockScenarioExecutionAnsweringWith(null);

            assertThat(fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .isNull();

            verifyNoChannelHandOff();
        }

        @Test
        void shouldReturnNull_afterDefaultTimeout_ifExecutionIsStillRunning() {
            mockScenarioExecution();

            assertThat(fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .isNull();

            verify(simulatorConfigurationMock).getDefaultTimeout();
            verifyNoChannelHandOff();
        }

        @Test
        void shouldReturnNull_ifResponseShouldNotBeHandled() {
            mockScenarioExecutionAnsweringWith(responseMessageMock);
            fixture.setHandleResponse(false);

            assertThat(fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .isNull();
        }

        @Test
        void shouldThrowResponseStatusException_ifExecutionFailed() {
            var cause = new IllegalStateException("thrown");
            mockScenarioExecutionAnsweringWith(new SimulationFailedUnexpectedlyException(cause));

            assertThatThrownBy(() -> fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .asInstanceOf(throwable(ResponseStatusException.class))
                .satisfies(
                    e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(555)),
                    e -> assertThat(e.getCause()).isSameAs(cause)
                );
        }

        @Test
        void shouldThrowResponseStatusException_ifTestActionFailed_withoutFallbackEndpointAdapter() {
            mockScenarioExecutionAnsweringWith(failedTestActionResponse());

            assertThatThrownBy(() -> fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .asInstanceOf(throwable(ResponseStatusException.class))
                .satisfies(
                    e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(555)),
                    e -> assertThat(e.getCause()).isInstanceOf(TestCaseFailedException.class)
                );
        }

        @Test
        void shouldThrowResponseStatusException_ifTestActionFailed_withDefaultFallbackEndpointAdapter() {
            fixture.setFallbackEndpointAdapter(new EmptyResponseEndpointAdapter());
            mockScenarioExecutionAnsweringWith(failedTestActionResponse());

            assertThatThrownBy(() -> fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .asInstanceOf(throwable(ResponseStatusException.class))
                .extracting(ResponseStatusException::getStatusCode)
                .isEqualTo(HttpStatusCode.valueOf(555));
        }

        @Test
        void shouldDelegateFailedTestActionToCustomFallbackEndpointAdapter() {
            var fallbackResponseMock = mock(Message.class);
            when(customFallbackEndpointAdapterMock.handleMessage(requestMessageMokc)).thenReturn(fallbackResponseMock);

            fixture.setFallbackEndpointAdapter(customFallbackEndpointAdapterMock);
            fixture.setMappingKeyExtractor(request -> SCENARIO_NAME);
            mockScenarioExecutionAnsweringWith(failedTestActionResponse());

            assertThat(fixture.handleMessage(requestMessageMokc))
                .isSameAs(fallbackResponseMock);
        }

        @Test
        void shouldThrowResponseStatusException_ifScenarioThrows_evenWithCustomFallbackEndpointAdapter() {
            fixture.setFallbackEndpointAdapter(customFallbackEndpointAdapterMock);
            mockScenarioExecutionAnsweringWith(new SimulationFailedUnexpectedlyException(new IllegalStateException("thrown")));

            assertThatThrownBy(() -> fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .asInstanceOf(throwable(ResponseStatusException.class))
                .extracting(ResponseStatusException::getStatusCode)
                .isEqualTo(HttpStatusCode.valueOf(555));

            verifyNoInteractions(customFallbackEndpointAdapterMock);
        }

        @Test
        void shouldThrowResponseStatusException_andReleaseRequest_ifRunThrows() {
            doAnswer(invocation -> {
                invocation.<Consumer<TestContext>>getArgument(3).accept(testContextMock);
                throw new IllegalStateException("run-failed");
            }).when(scenarioExecutorServiceMock).run(eq(scenarioMock), eq(SCENARIO_NAME), anyList(), anyTestContextInitializer());

            assertThatThrownBy(() -> fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .asInstanceOf(throwable(ResponseStatusException.class))
                .extracting(ResponseStatusException::getStatusCode)
                .isEqualTo(HttpStatusCode.valueOf(555));

            verify(scenarioEndpointMock).release(testContextMock);
            verifyNoChannelHandOff();
        }

        @Test
        void shouldThrowServiceUnavailable_ifExecutionIsRejected() {
            doThrow(new RejectedExecutionException("queue is full"))
                .when(scenarioExecutorServiceMock).run(eq(scenarioMock), eq(SCENARIO_NAME), anyList(), anyTestContextInitializer());

            assertThatThrownBy(() -> fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .asInstanceOf(throwable(ResponseStatusException.class))
                .extracting(ResponseStatusException::getStatusCode)
                .isEqualTo(HttpStatusCode.valueOf(503));

            verifyNoChannelHandOff();
        }

        /**
         * Simulates an execution that has started, but not answered the request yet.
         */
        private void mockScenarioExecution() {
            doAnswer(invocation -> {
                invocation.<Consumer<TestContext>>getArgument(3).accept(testContextMock);
                return 1L;
            }).when(scenarioExecutorServiceMock).run(eq(scenarioMock), eq(SCENARIO_NAME), anyList(), anyTestContextInitializer());
        }

        /**
         * Simulates an execution that has answered the request with the given response, or {@code null} if it has been
         * released without response.
         */
        private void mockScenarioExecutionAnsweringWith(@Nullable Message response) {
            mockScenarioExecution();

            doAnswer(invocation -> {
                invocation.<CompletableFuture<Message>>getArgument(2).complete(response);
                return null;
            }).when(scenarioEndpointMock).bind(eq(testContextMock), eq(requestMessageMokc), anyFuture());
        }

        private void verifyNoChannelHandOff() {
            verify(scenarioEndpointMock, never()).add(any(), any());
            verify(scenarioEndpointMock, never()).cancel(any());
            verify(scenarioExecutorServiceMock, never()).run(any(SimulatorScenario.class), any(), anyList());
        }
    }

    private static CompletableFuture<Message> anyFuture() {
        return any();
    }

    private static Consumer<TestContext> anyTestContextInitializer() {
        return any();
    }
}
