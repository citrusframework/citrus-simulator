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

package com.consol.citrus.simulator.service;

import com.consol.citrus.TestAction;
import com.consol.citrus.TestCase;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.simulator.model.*;
import com.consol.citrus.simulator.repository.ScenarioExecutionRepository;
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
 * Service for persisting and retrieving {@link ScenarioExecution} data.
 */
@Service
@Transactional
public class ActivityService {

    @Autowired
    ScenarioExecutionRepository scenarioExecutionRepository;

    /**
     * Creates a new {@link ScenarioExecution}, persisting it within the database.
     *
     * @param scenarioName       the name of the scenario
     * @param scenarioParameters the scenario's start parameters
     * @return the new {@link ScenarioExecution}
     */
    public ScenarioExecution createExecutionScenario(String scenarioName, Collection<ScenarioParameter> scenarioParameters) {
        ScenarioExecution ts = new ScenarioExecution();
        ts.setScenarioName(scenarioName);
        ts.setStartDate(getTimeNow());
        ts.setStatus(ScenarioExecution.Status.ACTIVE);

        if (scenarioParameters != null) {
            for (ScenarioParameter tp : scenarioParameters) {
                ts.addScenarioParameter(tp);
            }
        }

        ts = scenarioExecutionRepository.save(ts);
        return ts;
    }

    public void completeScenarioExecutionSuccess(TestCase testCase) {
        completeScenarioExecution(ScenarioExecution.Status.SUCCESS, testCase, null);
    }

    public void completeScenarioExecutionFailure(TestCase testCase, Throwable cause) {
        completeScenarioExecution(ScenarioExecution.Status.FAILED, testCase, cause);
    }

    public Collection<ScenarioExecution> getScenarioExecutionsByName(String testName) {
        return scenarioExecutionRepository.findByScenarioNameOrderByStartDateDesc(testName);
    }

    public Collection<ScenarioExecution> getScenarioExecutionsByStatus(ScenarioExecution.Status status) {
        return scenarioExecutionRepository.findByStatusOrderByStartDateDesc(status);
    }

    public ScenarioExecution getScenarioExecutionById(Long id) {
        return scenarioExecutionRepository.findOne(id);
    }

    public void clearScenarioExecutions() {
        scenarioExecutionRepository.deleteAll();
    }

    public Collection<ScenarioExecution> getScenarioExecutionsByStartDate(Date fromDate, Date toDate, Integer page, Integer size) {
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

        return scenarioExecutionRepository.findByStartDateBetweenOrderByStartDateDesc(calcFromDate, calcToDate, pageable);
    }

    private void completeScenarioExecution(ScenarioExecution.Status status, TestCase testCase, Throwable cause) {
        ScenarioExecution te = lookupScenarioExecution(testCase);
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

        ScenarioExecution te = lookupScenarioExecution(testCase);
        ScenarioAction ta = new ScenarioAction();
        ta.setName(testAction.getName());
        ta.setStartDate(getTimeNow());
        te.addScenarioAction(ta);
    }

    public void completeTestAction(TestCase testCase, TestAction testAction) {
        if (skipTestAction(testAction)) {
            return;
        }

        ScenarioExecution te = lookupScenarioExecution(testCase);
        Iterator<ScenarioAction> iterator = te.getScenarioActions().iterator();
        ScenarioAction lastScenarioAction = null;
        while (iterator.hasNext()) {
            lastScenarioAction = iterator.next();
        }

        if (lastScenarioAction == null) {
            throw new CitrusRuntimeException(String.format("No test action found with name %s", testAction.getName()));
        }
        if (!lastScenarioAction.getName().equals(testAction.getName())) {
            throw new CitrusRuntimeException(String.format("Expected to find last test action with name %s but got %s", testAction.getName(), lastScenarioAction.getName()));
        }

        lastScenarioAction.setEndDate(getTimeNow());
    }

    private boolean skipTestAction(TestAction testAction) {
        List<String> ignoreList = Arrays.asList("create-variables");
        return ignoreList.contains(testAction.getName());
    }

    private ScenarioExecution lookupScenarioExecution(TestCase testCase) {
        return scenarioExecutionRepository.findOne(lookupScenarioExecutionId(testCase));
    }

    private long lookupScenarioExecutionId(TestCase testCase) {
        return Long.parseLong(testCase.getVariableDefinitions().get(ScenarioExecution.EXECUTION_ID).toString());
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
