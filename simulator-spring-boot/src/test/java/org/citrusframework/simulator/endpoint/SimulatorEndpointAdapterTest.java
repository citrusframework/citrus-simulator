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

import org.citrusframework.base.endpoint.adapter.EmptyResponseEndpointAdapter;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.throwable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
        void shouldNotAwaitResponse_ifSynchronousScenarioCompletedWithoutResponse() {
            var fixture = createFixture();

            when(scenarioExecutorServiceMock.isSynchronous()).thenReturn(true);

            assertThat(fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .isNull();

            verify(scenarioEndpointMock).cancel(anyFuture());
            verify(simulatorConfigurationMock, never()).getDefaultTimeout();
        }

        @Test
        void shouldReturnResponse_ifSynchronousScenarioCompletedWithResponse() {
            var fixture = createFixture();
            var responseMessageMock = mock(Message.class);

            when(scenarioExecutorServiceMock.isSynchronous()).thenReturn(true);
            when(simulatorConfigurationMock.getDefaultTimeout()).thenReturn(50L);
            doAnswer(invocation -> {
                invocation.<CompletableFuture<Message>>getArgument(1).complete(responseMessageMock);
                return null;
            }).when(scenarioEndpointMock).add(eq(requestMessageMokc), anyFuture());

            assertThat(fixture.dispatchMessage(requestMessageMokc, SCENARIO_NAME))
                .isSameAs(responseMessageMock);

            verify(scenarioEndpointMock, never()).cancel(anyFuture());
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

        private SimulatorEndpointAdapter createFixture() {
            when(applicationContextMock.containsBean(SCENARIO_NAME)).thenReturn(true);
            when(applicationContextMock.getBean(SCENARIO_NAME, SimulatorScenario.class)).thenReturn(scenarioMock);
            when(scenarioMock.getScenarioEndpoint()).thenReturn(scenarioEndpointMock);

            return new SimulatorEndpointAdapter(applicationContextMock, handlerRegistryMock, scenarioExecutorServiceMock, simulatorConfigurationMock);
        }

        private static CompletableFuture<Message> anyFuture() {
            return any();
        }
    }
}
