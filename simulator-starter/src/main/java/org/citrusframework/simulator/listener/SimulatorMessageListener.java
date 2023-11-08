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

package org.citrusframework.simulator.listener;


import org.citrusframework.context.TestContext;
import org.citrusframework.message.Message;
import org.citrusframework.report.MessageListener;
import org.citrusframework.report.MessageListeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * This listener is called when the simulator sends or receives messages
 */
@Component
public class SimulatorMessageListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(SimulatorMessageListener.class);

    @Autowired
    private MessageListeners messageListeners;

    @PostConstruct
    public void init() {
        messageListeners.addMessageListener(this);
    }

    @Override
    public void onInboundMessage(Message message, TestContext context) {
        String payload = message.getPayload(String.class);
        logger.debug("received: {}", payload);
    }

    @Override
    public void onOutboundMessage(Message message, TestContext context) {
        String payload = message.getPayload(String.class);
        logger.debug("sent: {}", payload);
    }
}
