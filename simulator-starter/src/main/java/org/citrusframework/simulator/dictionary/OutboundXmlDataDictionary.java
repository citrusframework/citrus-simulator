package org.citrusframework.simulator.dictionary;

import static org.citrusframework.simulator.dictionary.XmlUtils.loadXMLMappingFile;

import org.citrusframework.context.TestContext;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.spi.CitrusResourceWrapper;
import org.citrusframework.variable.dictionary.xml.XpathMappingDataDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.Node;

/**
 * @author Christoph Deppisch
 */
public class OutboundXmlDataDictionary extends XpathMappingDataDictionary {

    private static final Logger logger = LoggerFactory.getLogger(OutboundXmlDataDictionary.class);

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
    public void initialize() {
        loadXMLMappingFile(logger, mappingFile, mappings);
    }

    @Override
    public <T> T translate(Node node, T value, TestContext context) {
        if (value instanceof String) {
            String toTranslate;
            if (!mappings.isEmpty()) {
                toTranslate = (String) super.translate(node, value, context);
            } else {
                toTranslate = (String) value;
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
