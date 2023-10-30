/*
 * Copyright 2023 the original author or authors.
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

package org.citrusframework.simulator.service;

import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.template.TemplateHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Service for working with XML, JSON and other file templates.
 */
@Service
public class TemplateService {
    private final TemplateHelper templateHelper;

    public TemplateService(SimulatorConfigurationProperties simulatorConfigurationProperties) {
        templateHelper = createTemplateHelper(simulatorConfigurationProperties);
    }

    public String getMessageTemplate(String templatePath, String templateExtension) {
        return templateHelper.getMessageTemplate(templatePath, templateExtension);
    }

    public String getXmlMessageTemplate(String templatePath) {
        return templateHelper.getXmlMessageTemplate(templatePath);
    }

    public String getJsonMessageTemplate(String templatePath) {
        return templateHelper.getJsonMessageTemplate(templatePath);
    }

    private static TemplateHelper createTemplateHelper(SimulatorConfigurationProperties simulatorConfigurationProperties) {
        String basePath = simulatorConfigurationProperties.getTemplatePath();
        if (StringUtils.hasLength(basePath) && !StringUtils.endsWithIgnoreCase(basePath, "/")) {
            basePath = simulatorConfigurationProperties.getTemplatePath() + "/";
        }
        return TemplateHelper.instance(basePath);
    }
}
