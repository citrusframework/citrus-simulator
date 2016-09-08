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

import com.consol.citrus.TestAction;
import com.consol.citrus.TestCase;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.Message;
import com.consol.citrus.report.*;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

/**
 * Abstract base servlet implementation for simulator servlet. Provides access to handlebars template engine, provides access to
 * Spring application context and adds this servlet to list Citrus test listeners.
 *
 * @author Christoph Deppisch
 */
public abstract class AbstractSimulatorServlet extends HttpServlet implements TestListener, TestActionListener, MessageListener {

    private static final long serialVersionUID = 1L;

    private Handlebars handlebars;

    private WebApplicationContext applicationContext;

    @Override
    public void init() throws ServletException {
        super.init();

        ClassPathTemplateLoader loader = new ClassPathTemplateLoader();
        loader.setSuffix(".html");

        handlebars = new Handlebars(loader);

        applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        applicationContext.getBean(TestListeners.class).addTestListener(this);
        applicationContext.getBean(TestActionListeners.class).addTestActionListener(this);
        applicationContext.getBean(MessageListeners.class).addMessageListener(this);
    }

    /**
     * Gets Spring application context from servlet context.
     * @return
     */
    public WebApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Gets the handlebars instance for this servlet.
     * @return
     */
    public Handlebars getHandlebars() {
        return handlebars;
    }

    /**
     * Compiles handlebars template and translates possible exceptions to servlet exception.
     * @param templateName
     * @return
     * @throws ServletException
     */
    protected Template compileHandlebarsTemplate(String templateName) throws ServletException {
        try {
            return getHandlebars().compile(templateName);
        } catch (IOException e) {
            throw new ServletException("Failed to load html handlebars template", e);
        }
    }

    @Override
    public void onTestStart(TestCase test) {
    }

    @Override
    public void onTestFinish(TestCase test) {
    }

    @Override
    public void onTestSuccess(TestCase test) {
    }

    @Override
    public void onTestFailure(TestCase test, Throwable cause) {
    }

    @Override
    public void onTestSkipped(TestCase test) {
    }

    @Override
    public void onTestActionStart(TestCase testCase, TestAction testAction) {
    }

    @Override
    public void onTestActionFinish(TestCase testCase, TestAction testAction) {
    }

    @Override
    public void onTestActionSkipped(TestCase testCase, TestAction testAction) {
    }

    @Override
    public void onInboundMessage(Message message, TestContext context) {
    }

    @Override
    public void onOutboundMessage(Message message, TestContext context) {
    }
}

