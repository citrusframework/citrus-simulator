/*
 * Copyright 2006-2014 the original author or authors.
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

package com.consol.citrus.simulator.model;

import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
public class MessageTemplate {
    private String name;
    private String payload;
    private Class<? extends UseCaseTrigger> triggerType;

    /**
     * Default constructor using all fields.
     * @param name
     * @param payload
     * @param triggerType
     */
    public MessageTemplate(String name, String payload, Class<? extends UseCaseTrigger> triggerType) {
        this.name = name;
        this.payload = payload;
        this.triggerType = triggerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getTriggerType() {
        return triggerType.getAnnotation(Component.class).value();
    }
}
