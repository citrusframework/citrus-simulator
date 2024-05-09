/*
 * Copyright 2023-2024 the original author or authors.
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

package org.citrusframework.simulator.service.impl;

import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.citrusframework.TestAction;
import org.citrusframework.TestCase;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.simulator.model.ScenarioAction;
import org.citrusframework.simulator.repository.ScenarioActionRepository;
import org.citrusframework.simulator.service.ScenarioActionService;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Collections.singletonList;
import static org.citrusframework.simulator.service.TestCaseUtil.getScenarioExecutionId;

/**
 * Service Implementation for managing {@link ScenarioAction}.
 */
@Service
@Transactional
public class ScenarioActionServiceImpl implements ScenarioActionService {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioActionServiceImpl.class);

    private static final List<String> IGNORE_TEST_ACTION_NAMES = singletonList("create-variables");

    private final TimeProvider timeProvider = new TimeProvider();

    private final ScenarioActionRepository scenarioActionRepository;
    private final ScenarioExecutionService scenarioExecutionService;

    public ScenarioActionServiceImpl(ScenarioActionRepository scenarioActionRepository, ScenarioExecutionService scenarioExecutionService) {
        this.scenarioActionRepository = scenarioActionRepository;
        this.scenarioExecutionService = scenarioExecutionService;
    }

    private static boolean skipTestAction(TestAction testAction) {
        return IGNORE_TEST_ACTION_NAMES.contains(testAction.getName());
    }

    @Override
    public ScenarioAction save(ScenarioAction scenarioAction) {
        logger.debug("Request to save ScenarioAction : {}", scenarioAction);
        return scenarioActionRepository.save(scenarioAction);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScenarioAction> findAll(Pageable pageable) {
        logger.debug("Request to get all ScenarioActions with eager relationships");
        return scenarioActionRepository.findAllWithToOneRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScenarioAction> findOne(Long id) {
        logger.debug("Request to get ScenarioAction with eager relationships: {}", id);
        return scenarioActionRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public @Nullable ScenarioAction createForScenarioExecutionAndSave(TestCase testCase, TestAction testAction) {
        logger.debug("Request to save ScenarioAction from TestCase {} and TestAction {}", testCase, testAction);

        if (skipTestAction(testAction)) {
            logger.trace("TestAction marked as 'to skip', not persisting!");
            return null;
        }

        AtomicReference<ScenarioAction> newScenarioAction = new AtomicReference<>(null);

        scenarioExecutionService.findOneLazy(getScenarioExecutionId(testCase))
            .map(scenarioExecution -> {
                ScenarioAction scenarioAction = new ScenarioAction();
                scenarioAction.setName(StringUtils.isNotBlank(testAction.getName()) ? testAction.getName() : scenarioExecution.getScenarioName());
                scenarioAction.setStartDate(timeProvider.getTimeNow());

                scenarioExecution.addScenarioAction(scenarioAction);
                newScenarioAction.set(scenarioAction);

                return scenarioExecution;
            })
            .map(scenarioExecutionService::save);

        return newScenarioAction.get();
    }

    @Override
    public void completeTestAction(TestCase testCase, TestAction testAction) {
        logger.debug("Request to complete ScenarioAction from TestCase {} and TestAction {}", testCase, testAction);

        if (skipTestAction(testAction)) {
            logger.trace("TestAction marked as 'to skip', not persisting!");
            return;
        }

        scenarioExecutionService.findOneLazy(getScenarioExecutionId(testCase))
            .map(scenarioExecution -> {
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

                return lastScenarioAction;
            })
            .map(scenarioActionRepository::save);
    }
}
