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

package com.consol.citrus.simulator.servlet;

import com.consol.citrus.TestResult;
import com.consol.citrus.dsl.design.ExecutableTestDesignerComponent;
import com.consol.citrus.dsl.endpoint.Executable;
import com.consol.citrus.exceptions.TestCaseFailedException;
import com.consol.citrus.simulator.config.SimulatorConfiguration;
import com.consol.citrus.simulator.model.*;
import com.consol.citrus.simulator.service.UseCaseService;
import com.consol.citrus.util.FileUtils;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Servlet implementation enables user to run a simulator use case and wait for messages to be exchanged.
 * @author Christoph Deppisch
 */
public class SimulatorRunServlet extends AbstractSimulatorServlet {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(SimulatorRunServlet.class);

    /** Handlebars */
    private Template runTemplate;

    /** Service for executing test builders */
    private UseCaseService<Executable> useCaseService;
    private SimulatorConfiguration simulatorConfiguration;

    @Override
    public void init() throws ServletException {
        super.init();

        useCaseService = getApplicationContext().getBean(UseCaseService.class);
        simulatorConfiguration = getApplicationContext().getBean(SimulatorConfiguration.class);
        runTemplate = compileHandlebarsTemplate("run");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Context context = Context.newContext(buildViewModel(req));
        runTemplate.apply(context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String useCaseName = req.getParameter("useCase");
        ExecutableTestDesignerComponent testDesigner = getApplicationContext().getBean(useCaseName, ExecutableTestDesignerComponent.class);

        Map<String, Object> formParameters = getFormParameter(req);

        TestResult testResult;
        try {
            useCaseService.run(testDesigner, formParameters, getApplicationContext());
            testResult = TestResult.success(testDesigner.getClass().getSimpleName(), testDesigner.getTestCase().getParameters());
        } catch (TestCaseFailedException e) {
            testResult = TestResult.failed(testDesigner.getClass().getSimpleName(), e.getCause(), testDesigner.getTestCase().getParameters());
        }

        Map<String, Object> model = buildViewModel(req);
        model.put("result", testResult);

        Context context = Context.newContext(model);
        runTemplate.apply(context, resp.getWriter());
    }

    /**
     * Reads form parameters from http request and transforms these into map of test builder parameters. Master for parameter names
     * is the default list of test parameters provided by test builder service. Method tries to read all parameters from http
     * request accordingly.
     * @param req
     * @return
     */
    private Map<String, Object> getFormParameter(HttpServletRequest req) {
        List<UseCaseParameter> defaultParameters = useCaseService.getUseCaseParameter();
        Map<String, Object> formParameters = new LinkedHashMap<String, Object>(defaultParameters.size());
        for (UseCaseParameter parameterEntry : defaultParameters) {
            String formParameter = req.getParameter(parameterEntry.getId());
            if (formParameter != null) {
                formParameters.put(parameterEntry.getId(), formParameter);
            } else {
                formParameters.put(parameterEntry.getId(), parameterEntry.getValue());
            }
        }

        formParameters.put("payload", req.getParameter("payload"));

        return formParameters;
    }

    /**
     * Builds default view model for this servlet's handlebars view template.
     * @param req
     * @return
     */
    private Map<String, Object> buildViewModel(HttpServletRequest req) {
        Map<String, Object> model = new HashMap<String, Object>();

        Map<String, UseCaseTrigger> useCaseTriggers = getApplicationContext().getBeansOfType(UseCaseTrigger.class);

        model.put("useCaseList", useCaseTriggers);
        model.put("messageTemplates", getMessageTemplates(useCaseTriggers.values()));
        model.put("parameter", useCaseService.getUseCaseParameter());
        model.put("contextPath", req.getContextPath());

        return model;
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
                    log.warn("Failed to load message template", e);
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
}
