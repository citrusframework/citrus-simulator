/*
 * Copyright 2006-2023 the original author or authors.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;

/**
 * @author Christoph Deppisch
 */
@ConfigurationProperties(prefix = "citrus.simulator")
public class SimulatorConfigurationProperties implements InitializingBean {

    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(SimulatorConfigurationProperties.class);

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
    private Long defaultScenarioTimeout = 5000L;

    /**
     * Timeout of pessimistic lock on {@link org.citrusframework.simulator.model.ScenarioExecution}
     * that corresponds to a currently running scenario.
     * <p>
     * Values equal to or less than {@code 0} disable pessimistic locking at all!
     */
    private Long pessimisticLockTimeout = -1L;

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
     * The Spring application context environment auto-injected by environment aware mechanism.
     */
    private Environment env;

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
     * Sets the default timeout property.
     *
     * @param timout
     */
    public void setDefaultScenarioTimeout(Long timout) {
        this.defaultScenarioTimeout = timout;
    }

    /**
     * Gets the default timeout property.
     *
     * @return
     */
    public Long getDefaultScenarioTimeout() {
        return defaultScenarioTimeout;
    }

    public Long getPessimisticLockTimeout() {
        return pessimisticLockTimeout;
    }

    public void setPessimisticLockTimeout(Long pessimisticLockTimeout) {
        this.pessimisticLockTimeout = pessimisticLockTimeout;
    }

    /**
     * Checks System property on template validation setting. Enabled by default (e.g. if system property is not set).
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
    public void afterPropertiesSet() throws Exception {
        logger.debug("Loaded simulator properties: {}", this);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "enabled='" + enabled + '\'' +
                ", templatePath='" + templatePath + '\'' +
                ", defaultScenario='" + defaultScenario + '\'' +
                ", defaultTimeout=" + defaultScenarioTimeout +
                ", pessimisticLockTimeout=" + pessimisticLockTimeout +
                ", templateValidation=" + templateValidation +
                ", exceptionDelay=" + exceptionDelay +
                ", executorThreads=" + executorThreads +
                ", inboundXmlDictionary='" + inboundXmlDictionary + '\'' +
                ", outboundXmlDictionary='" + outboundXmlDictionary + '\'' +
                ", inboundJsonDictionary='" + inboundJsonDictionary + '\'' +
                ", outboundJsonDictionary='" + outboundJsonDictionary + '\'' +
                '}';
    }
}
