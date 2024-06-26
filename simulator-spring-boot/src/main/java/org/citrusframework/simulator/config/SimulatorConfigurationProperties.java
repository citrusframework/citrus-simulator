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

package org.citrusframework.simulator.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @author Christoph Deppisch
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "citrus.simulator")
public class SimulatorConfigurationProperties implements EnvironmentAware, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(SimulatorConfigurationProperties.class);

    private static final String SIMULATOR_INBOUND_XML_DICTIONARY_PROPERTY = "citrus.simulator.inbound.xml.dictionary.location";
    private static final String SIMULATOR_INBOUND_XML_DICTIONARY_ENV = "CITRUS_SIMULATOR_INBOUND_XML_DICTIONARY_LOCATION";
    private static final String SIMULATOR_OUTBOUND_XML_DICTIONARY_PROPERTY = "citrus.simulator.outbound.xml.dictionary.location";
    private static final String SIMULATOR_OUTBOUND_XML_DICTIONARY_ENV = "CITRUS_SIMULATOR_OUTBOUND_XML_DICTIONARY_LOCATION";
    private static final String SIMULATOR_INBOUND_JSON_DICTIONARY_PROPERTY = "citrus.simulator.inbound.json.dictionary.location";
    private static final String SIMULATOR_INBOUND_JSON_DICTIONARY_ENV = "CITRUS_SIMULATOR_INBOUND_JSON_DICTIONARY_LOCATION";
    private static final String SIMULATOR_OUTBOUND_JSON_DICTIONARY_PROPERTY = "citrus.simulator.outbound.json.dictionary.location";
    private static final String SIMULATOR_OUTBOUND_JSON_DICTIONARY_ENV = "CITRUS_SIMULATOR_OUTBOUND_JSON_DICTIONARY_LOCATION";

    /**
     * Global option to enable/disable simulator support, default is true.
     */
    private boolean enabled = true;

    /**
     * Template path relative to the project root. Used in scenario starters in order to load file content when configuring starter parameters.
     */
    private String templatePath = "org/citrusframework/simulator/templates";

    /**
     * Default test scenario name that applies in case no other scenario could be mapped within scenario mapper.
     */
    private String defaultScenario = "DEFAULT_SCENARIO";

    /**
     * Messaging timeout used as default wait time in all receiving actions within simulator.
     */
    private Long defaultTimeout = 5000L;

    /**
     * Property that en-/disables template validation, default value is true. When enabled incoming requests are automatically verified according to syntax rules (e.g. XML XSD, WSDL).
     */
    private boolean templateValidation = true;

    /**
     * Default delay in milliseconds to wait after uncategorized exceptions were thrown in simulator endpoint poller while constantly polling for incoming requests on a message destination.
     */
    private Long exceptionDelay = 5000L;

    /**
     * Defines how many scenarios the simulator can run in parallel.
     * Defaults to 10.
     */
    private int executorThreads = 10;

    /**
     * Optional inbound XML data dictionary mapping file which gets automatically loaded when default inbound data dictionaries are enabled. Used in generated scenarios in order to manipulate generated test data.
     */
    private String inboundXmlDictionary = "inbound-xml-dictionary.xml";

    /**
     * Optional outbound XML data dictionary mapping file which gets automatically loaded when default outbound data dictionaries are enabled. Used in generated scenarios in order to manipulate generated test data.
     */
    private String outboundXmlDictionary = "outbound-xml-dictionary.xml";

    /**
     * Optional inbound JSON data dictionary mapping file which gets automatically loaded when default inbound data dictionaries are enabled. Used in generated scenarios in order to manipulate generated test data.
     */
    private String inboundJsonDictionary = "inbound-json-dictionary.properties";

    /**
     * Optional outbound JSON data dictionary mapping file which gets automatically loaded when default outbound data dictionaries are enabled. Used in generated scenarios in order to manipulate generated test data.
     */
    private String outboundJsonDictionary = "outbound-json-dictionary.properties";

    @Override
    public void setEnvironment(Environment environment) {
        inboundXmlDictionary = environment.getProperty(SIMULATOR_INBOUND_XML_DICTIONARY_PROPERTY, environment.getProperty(SIMULATOR_INBOUND_XML_DICTIONARY_ENV, inboundXmlDictionary));
        outboundXmlDictionary = environment.getProperty(SIMULATOR_OUTBOUND_XML_DICTIONARY_PROPERTY, environment.getProperty(SIMULATOR_OUTBOUND_XML_DICTIONARY_ENV, outboundXmlDictionary));
        inboundJsonDictionary = environment.getProperty(SIMULATOR_INBOUND_JSON_DICTIONARY_PROPERTY, environment.getProperty(SIMULATOR_INBOUND_JSON_DICTIONARY_ENV, inboundJsonDictionary));
        outboundJsonDictionary = environment.getProperty(SIMULATOR_OUTBOUND_JSON_DICTIONARY_PROPERTY, environment.getProperty(SIMULATOR_OUTBOUND_JSON_DICTIONARY_ENV, outboundJsonDictionary));
  }

    @Override
    public void afterPropertiesSet() {
        logger.info("Using the simulator configuration: {}", this);
    }

}
