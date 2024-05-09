/*
 * Copyright 2024 the original author or authors.
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

package org.citrusframework.simulator.correlation;

import org.citrusframework.context.TestContext;
import org.citrusframework.message.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

/**
 * @author Christoph Deppisch
 */
public class CorrelationHandlerRegistry {

    /**
     * Map of active handlers
     */
    private final ConcurrentMap<CorrelationHandler, TestContext> registeredHandlers = new ConcurrentHashMap<>();

    /**
     * Add new correlation manager to registry.
     * @param handler
     */
    public void register(CorrelationHandler handler, TestContext context) {
        register(handler, context, 1000);
    }

    public void register(CorrelationHandler handler, TestContext context, int queueCapacity) {
        if (!registeredHandlers.containsKey(handler)) {
            registeredHandlers.put(handler, context);
        }

        while (registeredHandlers.size() > queueCapacity) {
            Stream.of(registeredHandlers.keySet()).limit(queueCapacity / 10)
                .toList()
                .parallelStream()
                .forEach(registeredHandlers::remove);
        }
    }

    /**
     * Remove registered handler from registry.
     * @param handler
     */
    public void remove(CorrelationHandler handler) {
        registeredHandlers.remove(handler);
    }

    /**
     * Finds handler in registered handlers that is able to handle given message. When handler is found return true
     * otherwise false.
     * @param request
     * @return
     */
    public CorrelationHandler findHandlerFor(Message request) {
        for (Map.Entry<CorrelationHandler, TestContext> handlerEntry : registeredHandlers.entrySet()) {
            if (handlerEntry.getKey().isHandlerFor(request, handlerEntry.getValue())) {
                return handlerEntry.getKey();
            }
        }

        return null;
    }
}
