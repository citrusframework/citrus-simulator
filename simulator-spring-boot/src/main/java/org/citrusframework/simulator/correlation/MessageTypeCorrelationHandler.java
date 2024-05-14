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

import org.citrusframework.context.TestContext;
import org.citrusframework.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import org.citrusframework.message.Message;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;

/**
 * @author Christoph Deppisch
 */
public class MessageTypeCorrelationHandler extends AbstractCorrelationHandler {

    private final XPathPayloadMappingKeyExtractor xPathPayloadMappingKeyExtractor = new XPathPayloadMappingKeyExtractor();

    private final String value;

    /**
     * Default constructor.
     *
     * @param scenarioEndpoint
     * @param value
     */
    public MessageTypeCorrelationHandler(ScenarioEndpoint scenarioEndpoint, String value) {
        super(scenarioEndpoint);

        this.value = value;
    }

    @Override
    public boolean isHandlerFor(Message message, TestContext context) {
        return xPathPayloadMappingKeyExtractor.extractMappingKey(message).equals(context.replaceDynamicContentInString(value));
    }
}
