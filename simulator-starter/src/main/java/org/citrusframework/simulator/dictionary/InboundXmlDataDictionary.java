package org.citrusframework.simulator.dictionary;

import com.consol.citrus.context.TestContext;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.variable.dictionary.xml.XpathMappingDataDictionary;
import com.consol.citrus.xml.xpath.XPathUtils;
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
            mappingFile = inboundMappingFile;
        }
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

    @Override
    public void initialize() {
        super.initialize();

        mappings.put("//*[string-length(normalize-space(text())) > 0]", "@ignore@");
        mappings.put("//@*", "@ignore@");
    }
}
