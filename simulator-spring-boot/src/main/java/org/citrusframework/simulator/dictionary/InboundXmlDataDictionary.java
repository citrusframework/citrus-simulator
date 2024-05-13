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
import org.citrusframework.xml.xpath.XPathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.xml.SimpleNamespaceContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Christoph Deppisch
 */
public class InboundXmlDataDictionary extends XpathMappingDataDictionary {

    /**
     * Default constructor setting default mappings and mappings file.
     */
    @Autowired
    public InboundXmlDataDictionary(SimulatorConfigurationProperties simulatorConfiguration) {
        setMappings(new LinkedHashMap<>());

        Resource inboundMappingFile = new PathMatchingResourcePatternResolver().getResource(simulatorConfiguration.getInboundXmlDictionary());
        if (inboundMappingFile.exists()) {
            mappingFile = new CitrusResourceWrapper(inboundMappingFile);
        }
    }

    @Override
    public void initialize() {
        super.initialize();

        mappings.put("//*[string-length(normalize-space(text())) > 0]", "@ignore@");
        mappings.put("//@*", "@ignore@");
    }

    @Override
    public <T> T translate(Node node, T value, TestContext context) {
        for (Map.Entry<String, String> expressionEntry : mappings.entrySet()) {
            String expression = expressionEntry.getKey();

            SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext();
            namespaceContext.setBindings(context.getNamespaceContextBuilder().getNamespaceMappings());

            NodeList findings = (NodeList) XPathUtils.evaluateExpression(node.getOwnerDocument(), expression, namespaceContext, XPathConstants.NODESET);

            if (findings != null && containsNode(findings, node)) {
                return convertIfNecessary(expressionEntry.getValue(), value, context);
            }
        }

        return value;
    }

    /**
     * Checks if given node set contains node.
     *
     * @param findings
     * @param node
     * @return
     */
    private boolean containsNode(NodeList findings, Node node) {
        for (int i = 0; i < findings.getLength(); i++) {
            if (findings.item(i).equals(node)) {
                return true;
            }
        }

        return false;
    }
}
