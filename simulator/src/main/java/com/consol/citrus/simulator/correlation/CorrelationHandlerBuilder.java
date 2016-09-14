/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.simulator.correlation;

import com.consol.citrus.TestCase;
import com.consol.citrus.dsl.builder.AbstractTestActionBuilder;

/**
 * @author Christoph Deppisch
 */
public class CorrelationHandlerBuilder extends AbstractTestActionBuilder<SetCorrelationHandlerAction> {

    /**
     * Default constructor with test.
     * @param test
     * @param handler
     */
    public CorrelationHandlerBuilder(final TestCase test, CorrelationHandler handler) {
        super(new SetCorrelationHandlerAction(test));
        action.setCorrelationHandler(handler);
    }

    public CorrelationHandlerBuilder onHeader(String headerName, String value) {
        action.setCorrelationHandler(new HeaderMappingCorrelationHandler(headerName, value));
        return this;
    }

    public CorrelationHandlerBuilder onMessageType(String type) {
        action.setCorrelationHandler(new MessageTypeCorrelationHandler(type));
        return this;
    }

    public CorrelationHandlerBuilder onPayload(String expression, String value) {
        action.setCorrelationHandler(new XPathPayloadCorrelationHandler(expression, value));
        return this;
    }

    public CorrelationHandlerBuilder withHandler(CorrelationHandler handler) {
        action.setCorrelationHandler(handler);
        return this;
    }
}
