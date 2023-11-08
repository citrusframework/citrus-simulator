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

import org.citrusframework.context.TestContext;
import org.citrusframework.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import org.citrusframework.message.Message;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.xml.namespace.NamespaceContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;

/**
 * @author Christoph Deppisch
 */
public class XPathPayloadCorrelationHandler extends AbstractCorrelationHandler {

    private static final Logger logger = LoggerFactory.getLogger(XPathPayloadCorrelationHandler.class);

    private final XPathPayloadMappingKeyExtractor xPathPayloadMappingKeyExtractor = new XPathPayloadMappingKeyExtractor();

    private final String value;

    /**
     * Default constructor using expression value to match.
     *
     * @param scenarioEndpoint
     * @param expression
     * @param value
     */
    public XPathPayloadCorrelationHandler(ScenarioEndpoint scenarioEndpoint, String expression, String value) {
        super(scenarioEndpoint);
        this.xPathPayloadMappingKeyExtractor.setXpathExpression(expression);
        this.value = value;
    }

    @Override
    public boolean isHandlerFor(Message message, TestContext context) {
        boolean isIntermediateMessage;
        try {
            isIntermediateMessage = xPathPayloadMappingKeyExtractor.extractMappingKey(message).equals(context.replaceDynamicContentInString(value));
        } catch (RuntimeException e) {
            logger.debug("Error checking whether message({}) is an intermediate message: {}", message.getId(), e.getMessage());
            isIntermediateMessage = false;
        }
        logger.debug("Intermediate message({}): {}", message.getId(), isIntermediateMessage);
        return isIntermediateMessage;
    }

    public void lookupNamespaceContextBuilder(ApplicationContext applicationContext) {
        NamespaceContextBuilder namespaceContextBuilder;
        String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext, NamespaceContextBuilder.class);
        if (beanNames.length > 0) {
            if (beanNames.length > 1) {
                logger.warn("Expected to find 1 beans of type {} but found {} instead: {}",
                        NamespaceContextBuilder.class.getCanonicalName(),
                        beanNames.length,
                        beanNames
                );
            }
            logger.debug("Using NamespaceContextBuilder - {}", beanNames[0]);
            namespaceContextBuilder = applicationContext.getBean(beanNames[0], NamespaceContextBuilder.class);
        } else {
            logger.debug("Using NamespaceContextBuilder - default");
            namespaceContextBuilder = new NamespaceContextBuilder();
        }

        this.xPathPayloadMappingKeyExtractor.setNamespaceContextBuilder(namespaceContextBuilder);
    }
}
