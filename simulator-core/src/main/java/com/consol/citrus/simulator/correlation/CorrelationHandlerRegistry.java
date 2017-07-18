package com.consol.citrus.simulator.correlation;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Christoph Deppisch
 */
public class CorrelationHandlerRegistry {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(CorrelationHandlerRegistry.class);

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
    public boolean findHandlerFor(Message request) {
        for (Map.Entry<CorrelationHandler, TestContext> handlerEntry : registeredHandlers.entrySet()) {
            if (handlerEntry.getKey().isHandlerFor(request, handlerEntry.getValue())) {
                return true;
            }
        }

        return false;
    }
}
