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
    private final SimulatorConfigurationProperties simulatorConfigurationProperties;
    private TemplateHelper templateHelper;

    public TemplateService(SimulatorConfigurationProperties simulatorConfigurationProperties) {
        this.simulatorConfigurationProperties = simulatorConfigurationProperties;
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
