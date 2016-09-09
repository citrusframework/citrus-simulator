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
 * Special interface marking that test executable is able to start a scenario with active role. This is usually the case when
 * a test executable starts to act as an interface partner with an outbound message rather than waiting for inbound actions. So
 * the simulator test executable sends the first starting message.
 *
 * User is able to call these test scenarios manually through web user interfaces.
 *
 * @author Christoph Deppisch
 */
public interface ScenarioStarter extends BeanNameAware {

    /**
     * Get name of starter usually the Spring bean name.
     * @return
     */
    String getName();

    /**
     * Get name of starter for display in GUI.
     * @return
     */
    String getDisplayName();

    /**
     * Marks starter as default.
     * @return
     */
    boolean isDefault();

    /**
     * Gets available message templates for this scenario starter.
     * @return
     */
    List<String> getMessageTemplates();

    /**
     * Gets list of parameters required to execute this starter.
     * @return
     */
    List<ScenarioParameter> getScenarioParameter();

}
