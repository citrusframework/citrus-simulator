/*
 * Copyright 2006-2014 the original author or authors.
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

package com.consol.citrus.simulator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christoph Deppisch
 */
public class SimulatorConfiguration {

    /** Logger */
    protected Logger log = LoggerFactory.getLogger(getClass());

    private String templatePath = "com/consol/citrus/simulator/templates";

    /** Default test builder chosen in case of unknown use case */
    private String defaultBuilder = "DEFAULT_BUILDER";

    /** Default timeout when waiting for incoming messages */
    private Long defaultTimeout = 5000L;

    /** property that en/disables template validation, default value is true */
    private boolean templateValidation = true;

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
     * Gets the default builder name.
     *
     * @return
     */
    public String getDefaultBuilder() {
        return defaultBuilder;
    }

    /**
     * Sets the default builder name.
     *
     * @param defaultBuilder
     */
    public void setDefaultBuilder(String defaultBuilder) {
        this.defaultBuilder = defaultBuilder;
    }

    /**
     * Checks System property on template validation setting. By default enabled (e.g. if System property is not set).
     *
     * @return
     */
    public boolean isTemplateValidationActive() {
        return templateValidation;
    }

    /**
     * En- or disables the template validation.
     *
     * @param templateValidation
     */
    public void setTemplateValidation(boolean templateValidation) {
        log.debug("set template validation to " + String.valueOf(templateValidation).toUpperCase());
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
}
