/*
 * Copyright 2006-2024 the original author or authors.
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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @author Christoph Deppisch
 */
@ConfigurationProperties(prefix = "citrus.simulator")
public class SimulatorConfigurationProperties implements EnvironmentAware, InitializingBean {

    /** Logger */
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

    /**
     * Gets the enabled.
     *
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled.
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the template path property.
     *
     * @return
     */
    public String getTemplatePath() {
        return templatePath;
    }

    /**
     * Sets the template path property.
     *
     * @param templatePath
     */
    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    /**
     * Gets the default scenario name.
     *
     * @return
     */
    public String getDefaultScenario() {
        return defaultScenario;
    }

    /**
     * Sets the default scenario name.
     *
     * @param defaultScenario
     */
    public void setDefaultScenario(String defaultScenario) {
        this.defaultScenario = defaultScenario;
    }

    /**
     * Checks System property on template validation setting. By default enabled (e.g. if System property is not set).
     *
     * @return
     */
    public boolean isTemplateValidation() {
        return templateValidation;
    }

    /**
     * En- or disables the template validation.
     *
     * @param templateValidation
     */
    public void setTemplateValidation(boolean templateValidation) {
        this.templateValidation = templateValidation;
    }

    /**
     * Sets the default timeout property.
     *
     * @param timout
     */
    public void setDefaultTimeout(Long timout) {
        this.defaultTimeout = timout;
    }

    /**
     * Gets the default timeout property.
     *
     * @return
     */
    public Long getDefaultTimeout() {
        return defaultTimeout;
    }

    /**
     * Gets the exceptionDelay.
     *
     * @return
     */
    public Long getExceptionDelay() {
        return exceptionDelay;
    }

    /**
     * Sets the exceptionDelay.
     *
     * @param exceptionDelay
     */
    public void setExceptionDelay(Long exceptionDelay) {
        this.exceptionDelay = exceptionDelay;
    }

    /**
     * Gets the number of threads available to the scenario executor.
     */
    public int getExecutorThreads() {
        return executorThreads;
    }

    /**
     * Sets the number of threads for parallel scenario execution.
     * @param executorThreads
     */
    public void setExecutorThreads(int executorThreads) {
        this.executorThreads = executorThreads;
    }

    /**
     * Gets the inboundXmlDictionary.
     *
     * @return
     */
    public String getInboundXmlDictionary() {
        return inboundXmlDictionary;
    }

    /**
     * Sets the inboundXmlDictionary.
     *
     * @param inboundXmlDictionary
     */
    public void setInboundXmlDictionary(String inboundXmlDictionary) {
        this.inboundXmlDictionary = inboundXmlDictionary;
    }

    /**
     * Gets the outboundXmlDictionary.
     *
     * @return
     */
    public String getOutboundXmlDictionary() {
        return outboundXmlDictionary;
    }

    /**
     * Sets the outboundXmlDictionary.
     *
     * @param outboundXmlDictionary
     */
    public void setOutboundXmlDictionary(String outboundXmlDictionary) {
        this.outboundXmlDictionary = outboundXmlDictionary;
    }

    /**
     * Gets the inboundJsonDictionary.
     *
     * @return
     */
    public String getInboundJsonDictionary() {
        return inboundJsonDictionary;
    }

    /**
     * Sets the inboundJsonDictionary.
     *
     * @param inboundJsonDictionary
     */
    public void setInboundJsonDictionary(String inboundJsonDictionary) {
        this.inboundJsonDictionary = inboundJsonDictionary;
    }

    /**
     * Gets the outboundJsonDictionary.
     *
     * @return
     */
    public String getOutboundJsonDictionary() {
        return outboundJsonDictionary;
    }

    /**
     * Sets the outboundJsonDictionary.
     *
     * @param outboundJsonDictionary
     */
    public void setOutboundJsonDictionary(String outboundJsonDictionary) {
        this.outboundJsonDictionary = outboundJsonDictionary;
    }

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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append(enabled)
            .append(templatePath)
            .append(defaultScenario)
            .append(defaultTimeout)
            .append(exceptionDelay)
            .append(executorThreads)
            .append(templateValidation)
            .append(inboundXmlDictionary)
            .append(outboundXmlDictionary)
            .append(inboundJsonDictionary)
            .append(outboundJsonDictionary)
            .toString();
    }
}
