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

import org.assertj.core.api.ThrowingConsumer;
import org.citrusframework.TestAction;
import org.citrusframework.context.TestContextFactory;
import org.citrusframework.message.DefaultMessage;
import org.citrusframework.message.Message;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.exception.SimulatorException;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.citrusframework.actions.FailAction.Builder.fail;
import static org.assertj.core.api.InstanceOfAssertFactories.throwable;

@IntegrationTest
@ExtendWith({MockitoExtension.class})
abstract class SimulatorEndpointAdapterIT {

    protected static final String FAIL_WITH_PURPOSE = "Fail with purpose!";

    private static final String NO_RESPONSE_SCENARIO_NAME = "SimulatorEndpointAdapterIT:no-response-scenario";
    private static final String SUCCESS_SCENARIO_NAME = "SimulatorEndpointAdapterIT:success-scenario";
    private static final String FAIL_SCENARIO_NAME = "SimulatorEndpointAdapterIT:fail-scenario";
    private static final String FAILING_ACTION_SCENARIO_NAME = "SimulatorEndpointAdapterIT:failing-action-scenario";

    @Mock
    private Message messageMock;

    @Autowired
    private SimulatorEndpointAdapter fixture;

    @Test
    void dispatchMessage_returnsNull_withoutResponse() {
        var result = fixture.dispatchMessage(messageMock, NO_RESPONSE_SCENARIO_NAME);
        assertThat(result)
            .isNull();
    }

    @Test
    void dispatchMessage_returnsResponse() {
        var result = fixture.dispatchMessage(messageMock, SUCCESS_SCENARIO_NAME);
        assertThat(result)
            .isInstanceOf(DefaultMessage.class);
    }

    /**
     * A failing test action must answer immediately, instead of blocking until the default timeout (5 seconds).
     */
    @Test
    @Timeout(value = 2, unit = SECONDS)
    void dispatchMessage_throwsResponseStatusExceptionImmediately_ifTestActionFails() {
        assertThatThrownBy(() -> fixture.dispatchMessage(messageMock, FAILING_ACTION_SCENARIO_NAME))
            .asInstanceOf(throwable(ResponseStatusException.class))
            .satisfies(
                e -> assertThat(e).extracting(ResponseStatusException::getStatusCode)
                    .isEqualTo(HttpStatusCode.valueOf(555)),
                e -> assertThat(e).rootCause()
                    .hasMessageContaining(FAIL_WITH_PURPOSE)
            );
    }

    void verifyFailingScenarioThrowsResponseStatusException(ThrowingConsumer<ResponseStatusException> exceptionAssert) {
        assertThatThrownBy(() -> fixture.dispatchMessage(messageMock, FAIL_SCENARIO_NAME))
            .asInstanceOf(throwable(ResponseStatusException.class))
            .satisfies(
                e -> assertThat(e).extracting(ResponseStatusException::getStatusCode)
                    .isEqualTo(HttpStatusCode.valueOf(555)),
                e -> assertThat(e).extracting(ResponseStatusException::getReason)
                    .isEqualTo("Simulation failed with an Exception!"),
                exceptionAssert
            );
    }

    @Scenario(NO_RESPONSE_SCENARIO_NAME)
    private static class NoResponseScenario extends AbstractSimulatorScenario {
    }

    @Scenario(SUCCESS_SCENARIO_NAME)
    private static class SuccessScenario extends AbstractSimulatorScenario {

        private final TestContextFactory testContextFactory;

        private SuccessScenario(TestContextFactory testContextFactory) {
            this.testContextFactory = testContextFactory;
        }

        @Override
        public void run(ScenarioRunner runner) {
            var context = testContextFactory.getObject();
            getScenarioEndpoint().send(new DefaultMessage(), context);
        }
    }

    @Scenario(FAILING_ACTION_SCENARIO_NAME)
    private static class FailingActionScenario extends AbstractSimulatorScenario {

        @Override
        public void run(ScenarioRunner runner) {
            runner.$((TestAction) context -> getScenarioEndpoint().receive(context));
            runner.$(fail(FAIL_WITH_PURPOSE));
        }
    }

    @Scenario(FAIL_SCENARIO_NAME)
    private static class FailScenario extends AbstractSimulatorScenario {

        @Override
        public void run(ScenarioRunner runner) {
            throw new SimulatorException(FAIL_WITH_PURPOSE);
        }
    }
}
