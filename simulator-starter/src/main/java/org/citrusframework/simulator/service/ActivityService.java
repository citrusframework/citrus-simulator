/*
 * Copyright 2006-2023 the original author or authors.
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

import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.citrusframework.TestAction;
import org.citrusframework.TestCase;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.simulator.model.ScenarioAction;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.repository.ScenarioExecutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

/**
 * Service for persisting and retrieving {@link ScenarioExecution} data.
 */
@Service
@Transactional
public class ActivityService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);

    private final TimeProvider timeProvider = new TimeProvider();

    private final ScenarioExecutionRepository scenarioExecutionRepository;

    public ActivityService(ScenarioExecutionRepository scenarioExecutionRepository) {
        this.scenarioExecutionRepository = scenarioExecutionRepository;
    }

    public void createTestAction(TestCase testCase, TestAction testAction) {
        if (skipTestAction(testAction)) {
            return;
        }

        ScenarioExecution scenarioExecution = lookupScenarioExecution(testCase);

        ScenarioAction scenarioAction = new ScenarioAction();
        scenarioAction.setName(StringUtils.isNotBlank(testAction.getName()) ? testAction.getName() : scenarioExecution.getScenarioName());
        scenarioAction.setStartDate(timeProvider.getTimeNow());

        scenarioExecution.addScenarioAction(scenarioAction);
    }

    public void completeTestAction(TestCase testCase, TestAction testAction) {
        if (skipTestAction(testAction)) {
            return;
        }

        ScenarioExecution scenarioExecution = lookupScenarioExecution(testCase);
        Iterator<ScenarioAction> scenarioActions = scenarioExecution.getScenarioActions().iterator();
        ScenarioAction lastScenarioAction = null;
        while (scenarioActions.hasNext()) {
            lastScenarioAction = scenarioActions.next();
        }

        if (lastScenarioAction == null) {
            throw new CitrusRuntimeException(String.format("No test action found with name %s", testAction.getName()));
        } else if ((StringUtils.isNotBlank(testAction.getName()) && !lastScenarioAction.getName().equals(testAction.getName()))
            || (StringUtils.isBlank(testAction.getName()) && !lastScenarioAction.getName().equals(scenarioExecution.getScenarioName()))) {
            throw new CitrusRuntimeException(String.format("Expected to find last test action with name '%s' but got '%s'", testAction.getName(), lastScenarioAction.getName()));
        }

        lastScenarioAction.setEndDate(timeProvider.getTimeNow());
    }

    private boolean skipTestAction(TestAction testAction) {
        List<String> ignoreList = List.of("create-variables");
        return ignoreList.contains(testAction.getName());
    }

    private ScenarioExecution lookupScenarioExecution(TestCase testCase) {
        return scenarioExecutionRepository.findById(lookupScenarioExecutionId(testCase)).orElseThrow(() -> new CitrusRuntimeException(String.format("Failed to look up scenario execution for test %s", testCase.getName())));
    }

    private long lookupScenarioExecutionId(TestCase testCase) {
        return Long.parseLong(testCase.getVariableDefinitions().get(ScenarioExecution.EXECUTION_ID).toString());
    }
}
