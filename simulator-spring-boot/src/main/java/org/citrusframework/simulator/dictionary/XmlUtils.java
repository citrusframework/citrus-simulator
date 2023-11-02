package org.citrusframework.simulator.dictionary;

import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.spi.Resource;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

class XmlUtils {

    private XmlUtils(){
        // Temporary utility class
    }

    // TODO: Remove if PR has been resolved and released: https://github.com/citrusframework/citrus/pull/1044
    static void loadXMLMappingFile(Logger logger, Resource mappingFile, Map<String, String> mappings) {
        if (mappingFile != null) {
            logger.debug("Reading data dictionary mapping: {}", mappingFile.getLocation());

            Properties props = new Properties();
            try (InputStream inputStream = mappingFile.getInputStream()) {
                props.loadFromXML(inputStream);
            } catch (IOException e) {
                throw new CitrusRuntimeException(e);
            }

            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                String key = entry.getKey().toString();

                logger.debug("Loading data dictionary mapping: {}={}", key, props.getProperty(key));

                if (logger.isDebugEnabled() && mappings.containsKey(key)) {
                    logger.warn("Overwriting data dictionary mapping '{}'; old value: {} new value: {}", key, mappings.get(key), props.getProperty(key));
                }

                mappings.put(key, props.getProperty(key));
            }

            logger.info("Loaded data dictionary mapping: {}", mappingFile.getLocation());
        }
    }
}
