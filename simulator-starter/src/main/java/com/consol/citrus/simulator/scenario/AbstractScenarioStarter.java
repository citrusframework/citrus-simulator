/*
 * Copyright 2006-2017 the original author or authors.
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

package com.consol.citrus.simulator.scenario;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractScenarioStarter extends AbstractSimulatorScenario implements ScenarioStarter {

    @Autowired
    private SimulatorConfigurationProperties simulatorConfigurationProperties;

    /**
     * Gets a classpath file resource from base template package.
     *
     * @param fileName
     * @param fileExtension
     * @return
     */
    protected Resource getFileResource(String fileName, String fileExtension) {
        return new ClassPathResource(getTemplateBasePath() + fileName + ((StringUtils.hasText(fileExtension) && !fileExtension.startsWith(".")) ? "." + fileExtension : fileExtension));
    }

    /**
     * Locates a message template using the supplied {@code filename} returning the contents as a string. Uses file extension ".xml".
     *
     * @param filename the message template name.
     * @return the contents as a string
     */
    protected String getXmlMessageTemplate(String filename) {
        return getMessageTemplate(filename, filename.endsWith(".xml") ? "": "xml");
    }

    /**
     * Locates a message template using the supplied {@code filename} returning the contents as a string. Uses file extension ".json".
     *
     * @param filename the message template name.
     * @return the contents as a string
     */
    protected String getJsonMessageTemplate(String filename) {
        return getMessageTemplate(filename, filename.endsWith(".json") ? "": "json");
    }

    /**
     * Locates a message template using the supplied {@code filename} returning the contents as a string. Uses given file extension.
     *
     * @param filename the message template name.
     * @param fileExtension template file extension.
     * @return the contents as a string
     */
    protected String getMessageTemplate(String filename, String fileExtension) {
        try {
            return FileUtils.readToString(this.getFileResource(filename, fileExtension));
        } catch (IOException e) {
            throw new CitrusRuntimeException(String.format("Error reading template: %s", filename), e);
        }
    }

    /**
     * Gets the default template base folder path.
     * @return
     */
    protected String getTemplateBasePath() {
        return (StringUtils.hasText(simulatorConfigurationProperties.getTemplatePath()) && simulatorConfigurationProperties.getTemplatePath().endsWith("/")) ?
                simulatorConfigurationProperties.getTemplatePath() : simulatorConfigurationProperties.getTemplatePath() + "/";
    }
}
