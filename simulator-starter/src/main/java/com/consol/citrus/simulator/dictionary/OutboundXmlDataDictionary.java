package com.consol.citrus.simulator.dictionary;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.variable.dictionary.xml.XpathMappingDataDictionary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Node;

/**
 * @author Christoph Deppisch
 */
public class OutboundXmlDataDictionary extends XpathMappingDataDictionary {

    /**
     * Default constructor setting default mappings and mappings file.
     */
    public OutboundXmlDataDictionary(SimulatorConfigurationProperties simulatorConfiguration) {
        if (simulatorConfiguration != null) {
            Resource outboundMappingFile = new ClassPathResource(simulatorConfiguration.getOutboundXmlDictionaryMappings());
            if (!outboundMappingFile.exists()) {
                mappingFile = outboundMappingFile;
            }
        }
    }

    @Override
    public <T> T translate(Node node, T value, TestContext context) {
        if (value instanceof String) {
            String toTranslate = (String) value;

            if (toTranslate.equals("true") || toTranslate.equals("false")) {
                return (T) toTranslate;
            } else if (Character.isDigit(toTranslate.charAt(0))) {
                return (T) ("citrus:randomNumber(" + toTranslate.length() + ")");
            } else if (toTranslate.startsWith("string")) {
                return (T) ("citrus:randomString(" + toTranslate.length() + ")");
            }
        }

        return super.translate(node, value, context);
    }
}
