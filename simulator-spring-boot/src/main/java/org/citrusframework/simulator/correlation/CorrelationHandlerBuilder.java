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

package org.citrusframework.simulator.correlation;

import org.citrusframework.AbstractTestActionBuilder;
import org.citrusframework.TestAction;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Christoph Deppisch
 */
public class CorrelationHandlerBuilder
    extends AbstractTestActionBuilder<StartCorrelationHandlerAction, CorrelationHandlerBuilder> implements ApplicationContextAware {

    /**
     * Stop correlation action
     */
    private final StopCorrelationHandlerAction stopCorrelationAction = new StopCorrelationHandlerAction();

    private final ScenarioEndpoint scenarioEndpoint;

    private CorrelationHandler correlationHandler;

    private ApplicationContext applicationContext;

    /**
     * Default constructor with correlation handler.
     */
    public CorrelationHandlerBuilder(ScenarioEndpoint scenarioEndpoint) {
        super();
        this.scenarioEndpoint = scenarioEndpoint;
    }

    @Override
    public StartCorrelationHandlerAction build() {
        if (correlationHandler instanceof XPathPayloadCorrelationHandler) {
            ((XPathPayloadCorrelationHandler) correlationHandler).lookupNamespaceContextBuilder(applicationContext);
        }

        return new StartCorrelationHandlerAction(this);
    }

    public CorrelationHandlerBuilder start() {
        return this;
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
        correlationHandler = handler;
        stopCorrelationAction.setCorrelationHandler(handler);
        return this;
    }

    public CorrelationHandler getCorrelationHandler() {
        return correlationHandler;
    }

    public TestAction stop() {
        return stopCorrelationAction;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
