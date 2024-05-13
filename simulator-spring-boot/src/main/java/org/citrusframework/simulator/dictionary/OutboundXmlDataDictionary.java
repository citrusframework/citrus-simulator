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

package org.citrusframework.simulator.dictionary;

import org.citrusframework.context.TestContext;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.spi.CitrusResourceWrapper;
import org.citrusframework.variable.dictionary.xml.XpathMappingDataDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.Node;

/**
 * @author Christoph Deppisch
 */
public class OutboundXmlDataDictionary extends XpathMappingDataDictionary {

    /**
     * Default constructor setting default mappings and mappings file.
     */
    @Autowired
    public OutboundXmlDataDictionary(SimulatorConfigurationProperties simulatorConfiguration) {
        Resource outboundMappingFile = new PathMatchingResourcePatternResolver().getResource(simulatorConfiguration.getOutboundXmlDictionary());
        if (outboundMappingFile.exists()) {
            mappingFile = new CitrusResourceWrapper(outboundMappingFile);
        }
    }

    @Override
    public <T> T translate(Node node, T value, TestContext context) {
        if (value instanceof String stringValue) {
            String toTranslate = stringValue;
            if (!mappings.isEmpty()) {
                toTranslate = (String) super.translate(node, value, context);
            }

            if (toTranslate.equals(value)) {
                if (toTranslate.equals("true") || toTranslate.equals("false")) {
                    return (T) toTranslate;
                } else if (Character.isDigit(toTranslate.charAt(0))) {
                    return (T) (context.replaceDynamicContentInString("citrus:randomNumber(" + toTranslate.length() + ")"));
                } else if (toTranslate.startsWith("string")) {
                    return (T) (context.replaceDynamicContentInString("citrus:randomString(" + toTranslate.length() + ")"));
                }
            } else {
                return (T) toTranslate;
            }
        }

        return super.translate(node, value, context);
    }
}
