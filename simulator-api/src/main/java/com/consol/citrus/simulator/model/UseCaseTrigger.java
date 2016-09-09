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

import org.springframework.beans.factory.BeanNameAware;

import java.util.List;

/**
 * Special interface marking that test builder is able to trigger a use case test with active role. This is usually the case when
 * a test builder starts to act as an interface partner with an outbound message rather than waiting for inbound actions. So
 * the simulator test builder sends the first triggering message.
 *
 * User is able to call these test builders manually through servlet user interfaces.
 * @author Christoph Deppisch
 */
public interface UseCaseTrigger extends BeanNameAware {

    /**
     * Get name of trigger usually the Spring bean name.
     * @return
     */
    String getName();

    /**
     * Get name of trigger for display in GUI.
     * @return
     */
    String getDisplayName();

    /**
     * Marks trigger as default.
     * @return
     */
    boolean isDefault();

    /**
     * Gets available message templates for this use case trigger.
     * @return
     */
    List<String> getMessageTemplates();

    /**
     * Gets list of parameters required to execute this trigger.
     * @return
     */
    List<UseCaseParameter> getUseCaseParameter();

}
