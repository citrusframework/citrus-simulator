/*
 * Copyright the original author or authors.
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

package org.citrusframework.simulator.endpoint;

import org.citrusframework.context.TestContext;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.message.Message;
import org.citrusframework.message.MessageHeaders;
import org.citrusframework.simulator.model.Message.Direction;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Endpoint message handler for handling messages that were sent or received on a endpoint.
 */
@Component
public class EndpointMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(EndpointMessageHandler.class);

    private final MessageService messageService;

    public EndpointMessageHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    public void handleSentMessage(Message message, TestContext context) {
        saveScenarioMessage(message, context, Direction.OUTBOUND);
    }

    public void handleReceivedMessage(Message message, TestContext context) {
        saveScenarioMessage(message, context, Direction.INBOUND);
    }

    private void saveScenarioMessage(Message message, TestContext context, Direction direction) {
        Optional<Long> executionId = extractExecutionId(context);
        Optional<String> citrusMessageId = extractCitrusMessageId(message);

        if (executionId.isPresent() && citrusMessageId.isPresent()) {
            messageService.attachMessageToScenarioExecutionAndSave(
                executionId.get(),
                direction,
                message.getPayload(String.class),
                citrusMessageId.get(),
                message.getHeaders()
            );
        }
    }

    private Optional<Long> extractExecutionId(TestContext context) {
        final String executionId;

        try {
            executionId = context.getVariable(ScenarioExecution.EXECUTION_ID);
        } catch (CitrusRuntimeException e) {
            // citrus throws a CitrusRuntimeException if you try to access a variable in the TextContext that does not
            // exist.
            if (logger.isDebugEnabled()) {
                logger.warn("Tried to save Message in TestContext without execution id! Did you correctly configure the Scenario?", e);
            }

            return Optional.empty();
        }

        try {
            if (StringUtils.hasText(executionId)) {
                return Optional.of(Long.parseLong(executionId));
            }
        } catch (NumberFormatException e) {
            logger.error("Error parsing the execution id: Was expecting a Long!", e);
        }

        return Optional.empty();
    }

    private Optional<String> extractCitrusMessageId(Message message) {
        if (message.getHeader(MessageHeaders.ID) instanceof String stringHeaderValue && StringUtils.hasText(stringHeaderValue)) {
            return Optional.of(stringHeaderValue);
        }

        return Optional.empty();
    }
}
