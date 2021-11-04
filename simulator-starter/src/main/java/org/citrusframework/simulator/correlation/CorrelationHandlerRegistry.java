package org.citrusframework.simulator.correlation;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Christoph Deppisch
 */
public class CorrelationHandlerRegistry {

    /**
     * Map of active handlers
     */
    private ConcurrentMap<CorrelationHandler, TestContext> registeredHandlers = new ConcurrentHashMap<>();

    /**
     * Maximum capacity of active tests
     */
    private int queueCapacity = 1000;

    /**
     * Add new correlation manager to registry.
     * @param handler
     */
    public void register(CorrelationHandler handler, TestContext context) {
        if (!registeredHandlers.keySet().contains(handler)) {
            registeredHandlers.put(handler, context);
        }

        while (registeredHandlers.size() > queueCapacity) {
            Stream.of(registeredHandlers.keySet()).limit(queueCapacity / 10)
                    .collect(Collectors.toList())
                    .parallelStream()
                    .forEach(toDelete -> registeredHandlers.remove(toDelete));
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
