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
import org.citrusframework.endpoint.EndpointConfiguration;
import org.citrusframework.message.Message;
import org.citrusframework.messaging.Consumer;
import org.citrusframework.messaging.Producer;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ScenarioEndpointTest {

    @Test
    void constructorConfiguresEndpointConfiguration() {
        var endpointConfigurationMock = mock(EndpointConfiguration.class);

        var fixture = new ScenarioEndpoint(endpointConfigurationMock) {
            @Override
            public void add(Message request, CompletableFuture<Message> responseFuture) {

            }

            @Override
            void fail(Throwable error) {

            }

            @Override
            public Producer createProducer() {
                return null;
            }

            @Override
            public Consumer createConsumer() {
                return null;
            }

            @Override
            public Message receive(TestContext testContext) {
                return null;
            }

            @Override
            public Message receive(TestContext testContext, long l) {
                return null;
            }

            @Override
            public void send(Message message, TestContext testContext) {

            }
        };

        assertThat(fixture.getEndpointConfiguration())
            .isEqualTo(endpointConfigurationMock);
    }
}
