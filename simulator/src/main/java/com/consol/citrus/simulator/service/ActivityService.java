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

package com.consol.citrus.simulator.service;

import com.consol.citrus.TestAction;
import com.consol.citrus.TestCase;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.simulator.model.TestExecution;
import com.consol.citrus.simulator.model.TestParameter;
import com.consol.citrus.simulator.repository.TestExecutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Service for persisting and retrieving {@link TestExecution} data.
 */
@Service
@Transactional
public class ActivityService {

    @Autowired
    TestExecutionRepository testExecutionRepository;

    /**
     * Creates a new {@link TestExecution}, persisting it within the database.
     *
     * @param scenarioName       the name of the scenario
     * @param scenarioParameters the scenario's start parameters
     * @return the new {@link TestExecution}
     */
    public TestExecution createExecutionScenario(String scenarioName, Collection<TestParameter> scenarioParameters) {
        TestExecution ts = new TestExecution();
        ts.setTestName(scenarioName);
        ts.setStartDate(getTimeNow());
        ts.setStatus(TestExecution.Status.ACTIVE);

        if (scenarioParameters != null) {
            for (TestParameter tp : scenarioParameters) {
                ts.addTestParameter(tp);
            }
        }

        ts = testExecutionRepository.save(ts);
        return ts;
    }

    public void completeTestExecutionSuccess(TestCase testCase) {
        completeTestExecution(TestExecution.Status.SUCCESS, testCase, null);
    }

    public void completeTestExecutionFailure(TestCase testCase, Throwable cause) {
        completeTestExecution(TestExecution.Status.FAILED, testCase, cause);
    }

    public Collection<TestExecution> getTestExecutionsByName(String testName) {
        return testExecutionRepository.findByTestNameOrderByStartDateDesc(testName);
    }

    public Collection<TestExecution> getTestExecutionsByStatus(TestExecution.Status status) {
        return testExecutionRepository.findByStatusOrderByStartDateDesc(status);
    }

    public TestExecution getTestExecutionById(Long id) {
        return testExecutionRepository.findOne(id);
    }

    public void clearTestExecutions() {
        testExecutionRepository.deleteAll();
    }

    public Collection<TestExecution> getTestExecutionsByStartDate(Date fromDate, Date toDate, Integer page, Integer size) {
        Date calcFromDate = fromDate;
        if (calcFromDate == null) {
            calcFromDate = Date.from(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC));
        }
        Date calcToDate = toDate;
        if (calcToDate == null) {
            calcToDate = Date.from(LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC));
        }

        Integer calcPage = page;
        if (calcPage == null) {
            calcPage = 0;
        }

        Integer calcSize = size;
        if (calcSize == null) {
            calcSize = 10;
        }

        Pageable pageable = new PageRequest(calcPage, calcSize);

        return testExecutionRepository.findByStartDateBetweenOrderByStartDateDesc(calcFromDate, calcToDate, pageable);
    }

    private void completeTestExecution(TestExecution.Status status, TestCase testCase, Throwable cause) {
        TestExecution te = lookupTestExecution(testCase);
        te.setEndDate(getTimeNow());
        te.setStatus(status);
        if (cause != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            cause.printStackTrace(pw);
            te.setErrorMessage(sw.toString());
        }
    }

    public void createTestAction(TestCase testCase, TestAction testAction) {
        if (skipTestAction(testAction)) {
            return;
        }

        TestExecution te = lookupTestExecution(testCase);
        com.consol.citrus.simulator.model.TestAction ta = new com.consol.citrus.simulator.model.TestAction();
        ta.setName(testAction.getName());
        ta.setStartDate(getTimeNow());
        te.addTestAction(ta);
    }

    public void completeTestAction(TestCase testCase, TestAction testAction) {
        if (skipTestAction(testAction)) {
            return;
        }

        TestExecution te = lookupTestExecution(testCase);
        Iterator<com.consol.citrus.simulator.model.TestAction> iterator = te.getTestActions().iterator();
        com.consol.citrus.simulator.model.TestAction lastTestAction = null;
        while (iterator.hasNext()) {
            lastTestAction = iterator.next();
        }

        if (lastTestAction == null) {
            throw new CitrusRuntimeException(String.format("No test action found with name %s", testAction.getName()));
        }
        if (!lastTestAction.getName().equals(testAction.getName())) {
            throw new CitrusRuntimeException(String.format("Expected to find last test action with name %s but got %s", testAction.getName(), lastTestAction.getName()));
        }

        lastTestAction.setEndDate(getTimeNow());
    }

    private boolean skipTestAction(TestAction testAction) {
        List<String> ignoreList = Arrays.asList("create-variables");
        return ignoreList.contains(testAction.getName());
    }

    private TestExecution lookupTestExecution(TestCase testCase) {
        return testExecutionRepository.findOne(lookupTestExecutionId(testCase));
    }

    private long lookupTestExecutionId(TestCase testCase) {
        return Long.parseLong(testCase.getVariableDefinitions().get(TestExecution.EXECUTION_ID).toString());
    }

    private Date getTimeNow() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date getDateAtStartOfDay() {
        return Date.from(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC));
    }

    private Date getDateAtEndOfDay() {
        return Date.from(LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC));
    }

}
