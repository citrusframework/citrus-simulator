/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.simulator.web;

import com.consol.citrus.TestResult;
import com.consol.citrus.dsl.design.ExecutableTestDesignerComponent;
import com.consol.citrus.dsl.endpoint.Executable;
import com.consol.citrus.exceptions.TestCaseFailedException;
import com.consol.citrus.simulator.config.SimulatorConfiguration;
import com.consol.citrus.simulator.model.*;
import com.consol.citrus.simulator.service.UseCaseService;
import com.consol.citrus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("/run")
public class RunController implements ApplicationContextAware {

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(RunController.class);

    @Autowired
    /** Service for executing test builders */
    private UseCaseService<Executable> useCaseService;

    @Autowired
    private SimulatorConfiguration simulatorConfiguration;

    private ApplicationContext applicationContext;

    @RequestMapping(method = RequestMethod.GET)
    public String get(Model model) {
        buildViewModel(model);
        return "run";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String run(Model model, @RequestParam(name = "useCase") String useCaseName, HttpEntity req) {
        ExecutableTestDesignerComponent testDesigner = applicationContext.getBean(useCaseName, ExecutableTestDesignerComponent.class);

        Map<String, Object> formParameters = getFormParameter(req);

        TestResult testResult;
        try {
            useCaseService.run(testDesigner, formParameters, applicationContext);
            testResult = TestResult.success(testDesigner.getClass().getSimpleName(), testDesigner.getTestCase().getParameters());
        } catch (TestCaseFailedException e) {
            testResult = TestResult.failed(testDesigner.getClass().getSimpleName(), e.getCause(), testDesigner.getTestCase().getParameters());
        }

        buildViewModel(model);
        model.addAttribute("result", testResult);

        return "run";
    }

    /**
     * Reads form parameters from http request and transforms these into map of test builder parameters. Master for parameter names
     * is the default list of test parameters provided by test builder service. Method tries to read all parameters from http
     * request accordingly.
     * @param req
     * @return
     */
    private Map<String, Object> getFormParameter(HttpEntity req) {
        List<UseCaseParameter> defaultParameters = useCaseService.getUseCaseParameter();
        Map<String, Object> formParameters = new LinkedHashMap<String, Object>(defaultParameters.size());
        for (UseCaseParameter parameterEntry : defaultParameters) {
            List<String> formParameter = req.getHeaders().get(parameterEntry.getId());
            if (!CollectionUtils.isEmpty(formParameter)) {
                formParameters.put(parameterEntry.getId(), formParameter.get(0));
            } else {
                formParameters.put(parameterEntry.getId(), parameterEntry.getValue());
            }
        }

        if (req.getHeaders().containsKey("payload")) {
            formParameters.put("payload", req.getHeaders().get("payload").get(0));
        }

        return formParameters;
    }

    private List<MessageTemplate> getMessageTemplates(Collection<UseCaseTrigger> useCaseTriggers) {
        List<MessageTemplate> templates = new ArrayList<MessageTemplate>();

        for (UseCaseTrigger trigger : useCaseTriggers) {
            for (String templateName : trigger.getMessageTemplates()) {

                try {
                    templates.add(new MessageTemplate(templateName,
                            FileUtils.readToString(getFileResource(templateName)),
                            trigger.getClass()));
                } catch (IOException e) {
                    LOG.warn("Failed to load message template", e);
                }
            }
        }

        return templates;
    }

    /**
     * Gets a classpath file resource from base template package.
     * @param fileName
     * @return
     */
    protected Resource getFileResource(String fileName) {
        return new ClassPathResource(simulatorConfiguration.getTemplatePath() + "/" + fileName + ".xml");
    }

    /**
     * Builds default view model for this controllers view template.
     * @param model
     * @return
     */
    private void buildViewModel(Model model) {
        Map<String, UseCaseTrigger> useCaseTriggers = applicationContext.getBeansOfType(UseCaseTrigger.class);

        model.addAttribute("useCaseList", useCaseTriggers.values());
        model.addAttribute("messageTemplates", getMessageTemplates(useCaseTriggers.values()));
        model.addAttribute("parameter", useCaseService.getUseCaseParameter());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
