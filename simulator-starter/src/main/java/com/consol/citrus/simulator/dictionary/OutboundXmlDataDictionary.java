package com.consol.citrus.simulator.dictionary;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.variable.dictionary.xml.XpathMappingDataDictionary;
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
        Resource outboundMappingFile = new PathMatchingResourcePatternResolver().getResource(simulatorConfiguration.getOutboundXmlDictionaryMappings());
        if (outboundMappingFile.exists()) {
            mappingFile = outboundMappingFile;
        }
    }

    @Override
    public <T> T translate(Node node, T value, TestContext context) {
        if (value instanceof String) {
            String toTranslate = (String) value;

            if (toTranslate.equals("true") || toTranslate.equals("false")) {
                return (T) toTranslate;
            } else if (Character.isDigit(toTranslate.charAt(0))) {
                return (T) (context.replaceDynamicContentInString("citrus:randomNumber(" + toTranslate.length() + ")"));
            } else if (toTranslate.startsWith("string")) {
                return (T) (context.replaceDynamicContentInString("citrus:randomString(" + toTranslate.length() + ")"));
            }
        }

        return super.translate(node, value, context);
    }
}
