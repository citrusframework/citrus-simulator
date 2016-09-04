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
import com.consol.citrus.actions.SleepAction;
import com.consol.citrus.TestResult;
import com.consol.citrus.report.TestResults;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Status servlet shows latest simulator use cases executed. Servlet shows list of use cases executed and success/failed
 * state with optional error messages.
 *
 * @author Christoph Deppisch
 */
public class SimulatorStatusServlet extends AbstractSimulatorServlet {

    private static final long serialVersionUID = 6905907827878688440L;

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger("SimStatusLogger");

    /** Accumulated test results */
    private TestResults testResults = new TestResults();

    /** Currently running test */
    private Map<String, TestResult> runningTests = new LinkedHashMap<String, TestResult>();

    /** Handlebars */
    private Template statusTemplate;

    @Override
    public void init() throws ServletException {
        super.init();
        statusTemplate = compileHandlebarsTemplate("status");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (StringUtils.hasText(req.getQueryString()) && req.getQueryString().contains("clear=true")) {
            testResults = new TestResults();
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("running", runningTests);
        model.put("results", testResults);
        model.put("contextPath", req.getContextPath());

        Context context = Context.newContext(model);
        statusTemplate.apply(context, resp.getWriter());
    }

    @Override
    public void onTestStart(TestCase test) {
        runningTests.put(StringUtils.arrayToCommaDelimitedString(getParameters(test)), TestResult.success(test.getName(), test.getParameters()));
    }

    @Override
    public void onTestFinish(TestCase test) {
        runningTests.remove(StringUtils.arrayToCommaDelimitedString(getParameters(test)));
    }

    @Override
    public void onTestSuccess(TestCase test) {
        TestResult result = TestResult.success(test.getName(), test.getParameters());
        testResults.addResult(result);
        LOG.info(result.toString());
    }

    @Override
    public void onTestFailure(TestCase test, Throwable cause) {
        TestResult result = TestResult.failed(test.getName(), cause, test.getParameters());
        testResults.addResult(result);

        LOG.info(result.toString());
        LOG.info(result.getFailureCause());
    }

    @Override
    public void onTestActionStart(TestCase testCase, TestAction testAction) {
        if (!testAction.getClass().equals(SleepAction.class)) {
            LOG.debug(testCase.getName() + "(" +
                    StringUtils.arrayToCommaDelimitedString(getParameters(testCase)) + ") - " +
                    testAction.getName() + ": " +
                    (StringUtils.hasText(testAction.getDescription()) ? testAction.getDescription() : ""));
        }
    }

    private String[] getParameters(TestCase test) {
        List<String> parameterStrings = new ArrayList<String>();
        for (Map.Entry<String, Object> param : test.getParameters().entrySet()) {
            parameterStrings.add(param.getKey() + "=" + param.getValue());
        }

        return parameterStrings.toArray(new String[parameterStrings.size()]);
    }

}

