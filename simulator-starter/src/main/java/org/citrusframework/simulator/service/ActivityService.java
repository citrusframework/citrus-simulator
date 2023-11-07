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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import org.citrusframework.simulator.repository.ScenarioExecutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for persisting and retrieving {@link ScenarioExecution} data.
 */
@Service
@Transactional
public class ActivityService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);

    private final TimeProvider timeProvider = new TimeProvider();

    private final ScenarioExecutionRepository scenarioExecutionRepository;
    private final MessageService messageService;

    public ActivityService(ScenarioExecutionRepository scenarioExecutionRepository, MessageService messageService) {
        this.scenarioExecutionRepository = scenarioExecutionRepository;
        this.messageService = messageService;
    }

    public void completeScenarioExecutionSuccess(TestCase testCase) {
        completeScenarioExecution(ScenarioExecution.Status.SUCCESS, testCase, null);
    }

    public void completeScenarioExecutionFailure(TestCase testCase, Throwable cause) {
        completeScenarioExecution(ScenarioExecution.Status.FAILED, testCase, cause);
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

        Message message = messageService.save(
            Message.builder()
                .direction(direction)
                .payload(payload)
                .citrusMessageId(citrusMessageId)
                .headers(headers)
                .build()
        );
        scenarioExecution.addScenarioMessage(message);
        return message;
    }

    private void completeScenarioExecution(ScenarioExecution.Status status, TestCase testCase, Throwable cause) {
        ScenarioExecution scenarioExecution = lookupScenarioExecution(testCase);
        scenarioExecution.setEndDate(timeProvider.getTimeNow());
        scenarioExecution.setStatus(status);

        if (cause != null) {
            try (StringWriter stringWriter = new StringWriter(); PrintWriter printWriter = new PrintWriter(stringWriter)) {
                cause.printStackTrace(printWriter);
                scenarioExecution.setErrorMessage(stringWriter.toString());
            } catch (IOException e) {
                logger.warn("Failed to write error message to scenario execution!", e);
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
