package com.consol.citrus.simulator.dictionary;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.variable.dictionary.xml.XpathMappingDataDictionary;
import com.consol.citrus.xml.xpath.XPathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
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
        mappings.put("//*[string-length(normalize-space(text())) > 0]", "@ignore@");
        mappings.put("//@*", "@ignore@");

        Resource inboundMappingFile = new PathMatchingResourcePatternResolver().getResource(simulatorConfiguration.getInboundXmlDictionaryMappings());
        if (inboundMappingFile.exists()) {
            mappingFile = inboundMappingFile;
        }
    }

    @Override
    public <T> T translate(Node node, T value, TestContext context) {
        for (Map.Entry<String, String> expressionEntry : mappings.entrySet()) {
            String expression = expressionEntry.getKey();

            NodeList findings = (NodeList) XPathUtils.evaluateExpression(node.getOwnerDocument(), expression, null, XPathConstants.NODESET);

            if (findings != null && containsNode(findings, node)) {
                return convertIfNecessary(context.replaceDynamicContentInString(expressionEntry.getValue()), value);
            }
        }

        return value;
    }

    /**
     * Checks if given node set contains node.
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
