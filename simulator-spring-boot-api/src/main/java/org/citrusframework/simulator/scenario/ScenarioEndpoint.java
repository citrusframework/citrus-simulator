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

import org.citrusframework.endpoint.AbstractEndpoint;
import org.citrusframework.endpoint.EndpointConfiguration;
import org.citrusframework.message.Message;
import org.citrusframework.messaging.Consumer;
import org.citrusframework.messaging.Producer;

import java.util.concurrent.CompletableFuture;

public abstract class ScenarioEndpoint extends AbstractEndpoint implements Producer, Consumer {

    public ScenarioEndpoint(EndpointConfiguration endpointConfiguration) {
        super(endpointConfiguration);
    }

    public abstract void add(Message request, CompletableFuture<Message> responseFuture);

    abstract void fail(Throwable error);
}
