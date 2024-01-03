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
import org.citrusframework.TestCase;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.repository.ScenarioExecutionRepository;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.citrusframework.simulator.service.impl.TestCaseUtil.getScenarioExecutionId;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Service implementation for managing {@link ScenarioExecution}.
 */
@Service
@Transactional
public class ScenarioExecutionServiceImpl implements ScenarioExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioExecutionServiceImpl.class);

    private final TimeProvider timeProvider = new TimeProvider();

    private final ScenarioExecutionRepository scenarioExecutionRepository;

    public ScenarioExecutionServiceImpl(ScenarioExecutionRepository scenarioExecutionRepository) {
        this.scenarioExecutionRepository = scenarioExecutionRepository;
    }

    @Override
    public ScenarioExecution save(ScenarioExecution scenarioExecution) {
        logger.debug("Request to save ScenarioExecution : {}", scenarioExecution);
        return scenarioExecutionRepository.save(scenarioExecution);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScenarioExecution> findAll(Pageable pageable) {
        logger.debug("Request to get all ScenarioExecutions with eager relationships");
        return scenarioExecutionRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScenarioExecution> findOne(Long id) {
        logger.debug("Request to get ScenarioExecution with eager relationships : {}", id);
        return scenarioExecutionRepository.findOneByExecutionId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScenarioExecution> findOneLazy(Long id) {
        logger.debug("Request to get ScenarioExecution : {}", id);
        return scenarioExecutionRepository.findById(id);
    }

    @Override
    public ScenarioExecution createAndSaveExecutionScenario(String scenarioName, @Nullable List<ScenarioParameter> scenarioParameters) {
        logger.debug("Request to create and save ScenarioExecution : {}", scenarioName);

        ScenarioExecution scenarioExecution = new ScenarioExecution();
        scenarioExecution.setScenarioName(scenarioName);
        scenarioExecution.setStartDate(timeProvider.getTimeNow());
        scenarioExecution.setStatus(ScenarioExecution.Status.RUNNING);

        if (!isEmpty(scenarioParameters)) {
            scenarioParameters.forEach(scenarioExecution::addScenarioParameter);
        }

        return save(scenarioExecution);
    }

    @Override
    public ScenarioExecution completeScenarioExecutionSuccess(TestCase testCase) {
        logger.debug("Request to complete ScenarioExecution for successful TestCase : {}", testCase);
        return completeScenarioExecution(ScenarioExecution.Status.SUCCESS, testCase, null);
    }

    @Override
    public ScenarioExecution completeScenarioExecutionFailure(TestCase testCase, Throwable cause) {
        logger.warn("Request to complete ScenarioExecution for failed TestCase : {}", testCase);
        return completeScenarioExecution(ScenarioExecution.Status.FAILED, testCase, cause);
    }

    private ScenarioExecution completeScenarioExecution(ScenarioExecution.Status status, TestCase testCase, @Nullable Throwable cause) {
        return scenarioExecutionRepository.findOneByExecutionId(getScenarioExecutionId(testCase))
            .map(scenarioExecution -> {
                scenarioExecution.setEndDate(timeProvider.getTimeNow());
                scenarioExecution.setStatus(status);

                if (cause != null) {
                    writeCauseToErrorMessage(cause, scenarioExecution);
                }

                return scenarioExecution;
            })
            .map(scenarioExecutionRepository::save)
            .orElseThrow(() -> new CitrusRuntimeException(format("Error while completing ScenarioExecution for test %s", testCase.getName())));
    }

    private static void writeCauseToErrorMessage(Throwable cause, ScenarioExecution scenarioExecution) {
        try (StringWriter stringWriter = new StringWriter(); PrintWriter printWriter = new PrintWriter(stringWriter)) {
            cause.printStackTrace(printWriter);
            scenarioExecution.setErrorMessage(stringWriter.toString());
        } catch (IOException e) {
            logger.warn("Failed to write error message to scenario execution!", e);
        }
    }
}
