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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;

import com.consol.citrus.AbstractTestActionBuilder;
import com.consol.citrus.TestAction;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import com.consol.citrus.xml.namespace.NamespaceContextBuilder;

/**
 * @author Christoph Deppisch
 */
public class CorrelationHandlerBuilder
    extends AbstractTestActionBuilder<StartCorrelationHandlerAction, CorrelationHandlerBuilder> {
    private static Logger log = LoggerFactory.getLogger(CorrelationHandlerBuilder.class);

    /**
     * Stop correlation action
     */
    private final StopCorrelationHandlerAction stopCorrelationAction = new StopCorrelationHandlerAction();

    private final ApplicationContext applicationContext;

    private ScenarioEndpoint scenarioEndpoint;

    private CorrelationHandler correlationHandler;

    /**
     * Default constructor with correlation handler.
     */
    public CorrelationHandlerBuilder(ScenarioEndpoint scenarioEndpoint, ApplicationContext applicationContext) {
        super();
        this.scenarioEndpoint = scenarioEndpoint;
        this.applicationContext = applicationContext;
    }

    @Override
    public StartCorrelationHandlerAction build() {
        return new StartCorrelationHandlerAction(this);
    }

    public CorrelationHandlerBuilder onHeader(String headerName, String value) {
        return withHandler(new HeaderMappingCorrelationHandler(scenarioEndpoint, headerName, value));
    }

    public CorrelationHandlerBuilder onMessageType(String type) {
        return withHandler(new MessageTypeCorrelationHandler(scenarioEndpoint, type));
    }

    public CorrelationHandlerBuilder onPayload(String expression, String value) {
        return withHandler(new XPathPayloadCorrelationHandler(lookupNamespaceContextBuilder(), scenarioEndpoint, expression, value));
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

    private NamespaceContextBuilder lookupNamespaceContextBuilder() {
        String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext, NamespaceContextBuilder.class);
        if (beanNames.length > 0) {
            if (beanNames.length > 1) {
                log.warn("Expected to find 1 beans of type {} but found {} instead: {}",
                    NamespaceContextBuilder.class.getCanonicalName(),
                    beanNames.length,
                    beanNames
                );
            }
            log.debug("Using NamespaceContextBuilder - {}", beanNames[0]);
            return applicationContext.getBean(beanNames[0], NamespaceContextBuilder.class);
        } else {
            log.debug("Using NamespaceContextBuilder - default");
            return new NamespaceContextBuilder();
        }
    }
}
