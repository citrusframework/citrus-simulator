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

import com.consol.citrus.context.TestContext;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.message.Message;
import com.consol.citrus.simulator.scenario.ScenarioEndpoint;
import com.consol.citrus.xml.namespace.NamespaceContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christoph Deppisch
 */
public class XPathPayloadCorrelationHandler extends AbstractCorrelationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(XPathPayloadCorrelationHandler.class);
    private XPathPayloadMappingKeyExtractor xPathPayloadMappingKeyExtractor = new XPathPayloadMappingKeyExtractor();
    private final String value;

    /**
     * Default constructor using expression value to match.
     *
     * @param namespaceContextBuilder
     * @param scenarioEndpoint
     * @param expression
     * @param value
     */
    public XPathPayloadCorrelationHandler(NamespaceContextBuilder namespaceContextBuilder, ScenarioEndpoint scenarioEndpoint, String expression, String value) {
        super(scenarioEndpoint);
        this.xPathPayloadMappingKeyExtractor.setNamespaceContextBuilder(namespaceContextBuilder);
        this.xPathPayloadMappingKeyExtractor.setXpathExpression(expression);
        this.value = value;
    }

    @Override
    public boolean isHandlerFor(Message message, TestContext context) {
        boolean isIntermediateMessage;
        try {
            isIntermediateMessage = xPathPayloadMappingKeyExtractor.extractMappingKey(message).equals(context.replaceDynamicContentInString(value));
        } catch (RuntimeException e) {
            LOG.debug("Error checking whether message({}) is an intermediate message: {}", message.getId(), e.getMessage());
            isIntermediateMessage = false;
        }
        LOG.debug("Intermediate message({}): {}", message.getId(), isIntermediateMessage);
        return isIntermediateMessage;
    }
}
