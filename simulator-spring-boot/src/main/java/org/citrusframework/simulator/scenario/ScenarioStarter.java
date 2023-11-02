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

package org.citrusframework.simulator.scenario;

import org.citrusframework.simulator.model.ScenarioParameter;

import java.util.Collection;
import java.util.Collections;

/**
 * Special interface marking that test executable is able to start a scenario with active role. This is usually the case when
 * a test executable starts to act as an interface partner with an outbound message rather than waiting for inbound actions. So
 * the simulator test executable sends the first starting message.
 * <p>
 * User is able to call these test scenarios manually through web user interfaces.
 *
 * @author Christoph Deppisch
 */
public interface ScenarioStarter extends SimulatorScenario {

    /**
     * Gets list of parameters required to execute this starter.
     *
     * @return
     */
    default Collection<ScenarioParameter> getScenarioParameters() {
        return Collections.emptyList();
    }
}
