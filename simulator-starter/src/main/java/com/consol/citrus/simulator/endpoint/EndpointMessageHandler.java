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

package com.consol.citrus.simulator.endpoint;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.Message;
import com.consol.citrus.message.MessageHeaders;
import com.consol.citrus.simulator.model.Message.Direction;
import com.consol.citrus.simulator.model.ScenarioExecution;
import com.consol.citrus.simulator.service.ActivityService;
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

    private static Logger LOG = LoggerFactory.getLogger(EndpointMessageHandler.class);
    private final ActivityService activityService;

    public EndpointMessageHandler(ActivityService activityService) {
        this.activityService = activityService;
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
            activityService.saveScenarioMessage(executionId.get(), direction,
                    message.getPayload(String.class), citrusMessageId.get(), message.getHeaders());
        }
    }

    private Optional<Long> extractExecutionId(TestContext context) {
        final String executionId;
        try {
            executionId = context.getVariable(ScenarioExecution.EXECUTION_ID);
        } catch (CitrusRuntimeException e) {
            // citrus throws a CitrusRuntimeException if you try to access a variable in the
            // test context that does not exist.
            return Optional.empty();
        }

        try {
            if (StringUtils.hasText(executionId)) {
                return Optional.of(Long.parseLong(executionId));
            }
        } catch (NumberFormatException e) {
            LOG.error("Error parsing the execution id. Was expection a Long", e);
        }
        return Optional.empty();
    }

    private Optional<String> extractCitrusMessageId(Message message) {
        Object headerValue = message.getHeader(MessageHeaders.ID);
        if (headerValue != null && headerValue instanceof String) {
            String stringHeaderValue = (String) headerValue;
            if (StringUtils.hasText(stringHeaderValue)) {
                return Optional.of(stringHeaderValue);
            }
        }
        return Optional.empty();
    }
}
