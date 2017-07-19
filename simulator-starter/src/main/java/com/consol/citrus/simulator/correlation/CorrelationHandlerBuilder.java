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

package com.consol.citrus.simulator.correlation;

import com.consol.citrus.TestAction;
import com.consol.citrus.dsl.builder.AbstractTestActionBuilder;
import com.consol.citrus.simulator.scenario.ScenarioEndpoint;

/**
 * @author Christoph Deppisch
 */
public class CorrelationHandlerBuilder extends AbstractTestActionBuilder<StartCorrelationHandlerAction> {

    /** Stop correlation action */
    private final StopCorrelationHandlerAction stopCorrelationAction = new StopCorrelationHandlerAction();

    private ScenarioEndpoint scenarioEndpoint;

    /**
     * Default constructor with correlation handler.
     */
    public CorrelationHandlerBuilder(ScenarioEndpoint scenarioEndpoint) {
        super(new StartCorrelationHandlerAction());
        this.scenarioEndpoint = scenarioEndpoint;
    }

    public CorrelationHandlerBuilder onHeader(String headerName, String value) {
        return withHandler(new HeaderMappingCorrelationHandler(scenarioEndpoint, headerName, value));
    }

    public CorrelationHandlerBuilder onMessageType(String type) {
        return withHandler(new MessageTypeCorrelationHandler(scenarioEndpoint, type));
    }

    public CorrelationHandlerBuilder onPayload(String expression, String value) {
        return withHandler(new XPathPayloadCorrelationHandler(scenarioEndpoint, expression, value));
    }

    public CorrelationHandlerBuilder withHandler(CorrelationHandler handler) {
        action.setCorrelationHandler(handler);
        stopCorrelationAction.setCorrelationHandler(handler);
        return this;
    }

    public TestAction stop() {
        return stopCorrelationAction;
    }
}
