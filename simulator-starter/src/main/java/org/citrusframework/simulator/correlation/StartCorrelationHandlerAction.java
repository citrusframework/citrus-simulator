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

package org.citrusframework.simulator.correlation;

import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import org.citrusframework.simulator.exception.SimulatorException;

/**
 * @author Christoph Deppisch
 */
public class StartCorrelationHandlerAction extends AbstractTestAction {

    /**
     * Correlation handler to register.
     */
    private CorrelationHandler correlationHandler;

    /**
     * Default constructor setting action name.
     */
    public StartCorrelationHandlerAction(CorrelationHandlerBuilder builder) {
        super("start-correlation", builder);

        correlationHandler = builder.getCorrelationHandler();
    }

    @Override
    public void doExecute(TestContext context) {
        CorrelationHandlerRegistry handlerRegistry = context.getReferenceResolver()
            .resolve(CorrelationHandlerRegistry.class);

        if (handlerRegistry != null) {
            handlerRegistry.register(correlationHandler, context);
        } else {
            throw new SimulatorException("Failed to get correlation handler registry in application context");
        }
    }

    /**
     * Sets the correlationHandler property.
     *
     * @param correlationHandler
     */
    public void setCorrelationHandler(CorrelationHandler correlationHandler) {
        this.correlationHandler = correlationHandler;
    }
}
