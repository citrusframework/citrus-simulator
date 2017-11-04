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
 * Intercepts {@link com.consol.citrus.messaging.Producer#send(Message, TestContext)} method invocations
 * so that the simulator can process the messages.
 */
@Component
public class EndpointProducerInterceptor implements MethodInterceptor {
    private final EndpointMessageHandler endpointMessageHandler;

    public EndpointProducerInterceptor(EndpointMessageHandler endpointMessageHandler) {
        this.endpointMessageHandler = endpointMessageHandler;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object result = methodInvocation.proceed();
        if (isSendMethod(methodInvocation)) {
            final Optional<TestContext> testContext = getArgument(methodInvocation, TestContext.class);
            final Optional<Message> message = getArgument(methodInvocation, Message.class);
            if (testContext.isPresent() && message.isPresent()) {
                endpointMessageHandler.handleSentMessage(message.get(), testContext.get());
            }
        }
        return result;
    }

    private boolean isSendMethod(MethodInvocation methodInvocation) {
        return "send".equals(methodInvocation.getMethod().getName());
    }

    private <T> Optional<T> getArgument(MethodInvocation methodInvocation, Class<T> clazz) {
        final Object[] arguments = methodInvocation.getArguments();
        if (arguments != null) {
            return Arrays.stream(arguments)
                    .filter(clazz::isInstance)
                    .map(clazz::cast)
                    .findFirst();
        }
        return Optional.empty();
    }
}
