/*
 * Copyright 2006-2017 the original author or authors.
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

package com.consol.citrus.simulator.endpoint;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.Message;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * Intercepts {@link com.consol.citrus.messaging.Consumer#receive(TestContext)} method invocations
 * (and other variants thereof) so that the simulator can process these messages.
 */
@Component
public class EndpointConsumerInterceptor implements MethodInterceptor {
    private final EndpointMessageHandler endpointMessageHandler;

    public EndpointConsumerInterceptor(EndpointMessageHandler endpointMessageHandler) {
        this.endpointMessageHandler = endpointMessageHandler;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object result = methodInvocation.proceed();
        if (isReceiveMethod(methodInvocation)) {
            final Optional<TestContext> testContext = getTestContext(methodInvocation);
            if (testContext.isPresent()) {
                final Optional<Message> message = getMessage(result);
                message.ifPresent(msg -> endpointMessageHandler.handleReceivedMessage(msg, testContext.get()));
            }
        }
        return result;
    }

    private boolean isReceiveMethod(MethodInvocation methodInvocation) {
        return "receive".equals(methodInvocation.getMethod().getName());
    }

    private Optional<TestContext> getTestContext(MethodInvocation methodInvocation) {
        final Object[] arguments = methodInvocation.getArguments();
        if (arguments != null) {
            return Arrays.stream(arguments)
                    .filter(TestContext.class::isInstance)
                    .map(TestContext.class::cast)
                    .findFirst();
        }
        return Optional.empty();
    }

    private Optional<Message> getMessage(Object result) {
        if (result != null && result instanceof Message) {
            return Optional.of((Message) result);
        }
        return Optional.empty();
    }
}
