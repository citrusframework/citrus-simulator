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

package org.citrusframework.simulator.service;

import jakarta.transaction.Transactional;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.citrusframework.TestAction;
import org.citrusframework.TestCase;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.ScenarioAction;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioExecution.Status;
import org.citrusframework.simulator.model.ScenarioExecutionFilter;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.repository.ScenarioExecutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for persisting and retrieving {@link ScenarioExecution} data.
 */
@Service
@Transactional
public class ActivityService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);

    @Autowired
    private QueryFilterAdapterFactory queryFilterAdapterFactory;

    private final ScenarioExecutionRepository scenarioExecutionRepository;
    private final MessageService messageService;

    @Autowired
    public ActivityService(ScenarioExecutionRepository scenarioExecutionRepository, MessageService messageService) {
        this.scenarioExecutionRepository = scenarioExecutionRepository;
        this.messageService = messageService;
    }

    /**
     * Creates a new {@link ScenarioExecution}, persisting it within the database.
     *
     * @param scenarioName       the name of the scenario
     * @param scenarioParameters the scenario's start parameters
     * @return the new {@link ScenarioExecution}
     */
    public ScenarioExecution createExecutionScenario(String scenarioName, Collection<ScenarioParameter> scenarioParameters) {
        ScenarioExecution scenarioExecution = new ScenarioExecution();
        scenarioExecution.setScenarioName(scenarioName);
        scenarioExecution.setStartDate(getTimeNow());
        scenarioExecution.setEndDate(getTimeNow());
        scenarioExecution.setStatus(Status.RUNNING);

        if (scenarioParameters != null) {
            for (ScenarioParameter tp : scenarioParameters) {
                scenarioExecution.addScenarioParameter(tp);
            }
        }

        return scenarioExecutionRepository.save(scenarioExecution);
    }

    public void completeScenarioExecutionSuccess(TestCase testCase) {
        completeScenarioExecution(Status.SUCCESS, testCase, null);
    }

    public void completeScenarioExecutionFailure(TestCase testCase, Throwable cause) {
        completeScenarioExecution(Status.FAILED, testCase, cause);
    }

    public Collection<ScenarioExecution> getScenarioExecutionsByName(String testName) {
        return scenarioExecutionRepository.findByScenarioNameOrderByStartDateDesc(testName);
    }

    public Collection<ScenarioExecution> getScenarioExecutions(ScenarioExecutionFilter filter) {
        return scenarioExecutionRepository.find(queryFilterAdapterFactory.getQueryAdapter(filter));
	}

	public Collection<ScenarioExecution> getScenarioExecutionsByStatus(ScenarioExecution.Status status) {
        return scenarioExecutionRepository.findByStatusOrderByStartDateDesc(status);
    }

    public ScenarioExecution getScenarioExecutionById(Long id) {
        return scenarioExecutionRepository.findById(id).orElseThrow(() -> new CitrusRuntimeException(String.format("Failed to find scenario execution for id %s", id)));
    }

    /**
     * Persists the message along with the scenario execution details. With the help of the {@code citrusMessageId}
     * a check is made to determine whether the message has already been persisted. If it has then there's nothing
     * to be done and the persisted message is simply returned.
     *
     * @param executionId     the scenario execution id
     * @param direction       the direction of the message
     * @param payload         the message content
     * @param citrusMessageId the internal citrus message id
     * @param headers         the message headers
     * @return the already or newly persisted message
     */
    public Message saveScenarioMessage(Long executionId, Message.Direction direction, String payload, String citrusMessageId, Map<String, Object> headers) {
        final ScenarioExecution scenarioExecution = getScenarioExecutionById(executionId);
        Collection<Message> messages = scenarioExecution.getScenarioMessages();
        if (messages != null) {
            Optional<Message> message = messages.stream()
                    .filter(scenarioMessage -> scenarioMessage.getCitrusMessageId().equalsIgnoreCase(citrusMessageId))
                    .findFirst();
            if (message.isPresent()) {
                // message is already persisted and attached to execution scenario
                return message.get();
            }
        }
        final Message message = messageService.saveMessage(direction, payload, citrusMessageId, headers);
        scenarioExecution.addScenarioMessage(message);
        return message;
    }

    public void clearScenarioExecutions() {
        scenarioExecutionRepository.deleteAll();
    }

    public Collection<ScenarioExecution> getScenarioExecutionsByStartDate(Instant fromDate, Instant toDate, Integer page, Integer size) {
        Instant calcFromDate = fromDate;
        if (calcFromDate == null) {
            calcFromDate = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
        }
        Instant calcToDate = toDate;
        if (calcToDate == null) {
            calcToDate = LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        }

        Integer calcPage = page;
        if (calcPage == null) {
            calcPage = 0;
        }

        Integer calcSize = size;
        if (calcSize == null) {
            calcSize = 25;
        }

        Pageable pageable = PageRequest.of(calcPage, calcSize);

        return scenarioExecutionRepository.findByStartDateBetweenOrderByStartDateDesc(calcFromDate, calcToDate, pageable);
    }

    private void completeScenarioExecution(ScenarioExecution.Status status, TestCase testCase, Throwable cause) {
        ScenarioExecution scenarioExecution = lookupScenarioExecution(testCase);
        scenarioExecution.setEndDate(getTimeNow());
        scenarioExecution.setStatus(status);
        if (cause != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            cause.printStackTrace(printWriter);

            try {
                scenarioExecution.setErrorMessage(stringWriter.toString());
            } catch (ScenarioExecution.ErrorMessageTruncationException e) {
                logger.error("Error completing scenario execution!", e);
            }
        }
    }

    public void createTestAction(TestCase testCase, TestAction testAction) {
        if (skipTestAction(testAction)) {
            return;
        }

        ScenarioExecution scenarioExecution = lookupScenarioExecution(testCase);
        ScenarioAction scenarioAction = new ScenarioAction();
        scenarioAction.setName(StringUtils.isNotBlank(testAction.getName()) ? testAction.getName() : scenarioExecution.getScenarioName());
        scenarioAction.setStartDate(getTimeNow());
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
            throw new CitrusRuntimeException(String.format("No test action found with name' %s'", testAction.getName()));
        }
        if ((StringUtils.isNotBlank(testAction.getName()) && !lastScenarioAction.getName().equals(testAction.getName()))
            || (StringUtils.isBlank(testAction.getName()) && !lastScenarioAction.getName().equals(scenarioExecution.getScenarioName()))) {
            throw new CitrusRuntimeException(String.format("Expected to find last test action with name '%s' but got '%s'", testAction.getName(), lastScenarioAction.getName()));
        }

        lastScenarioAction.setEndDate(getTimeNow());
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

    private Instant getTimeNow() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
    }
}
