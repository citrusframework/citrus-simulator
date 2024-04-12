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

package org.citrusframework.simulator.scenario;

import jakarta.annotation.Nullable;

/**
 * Classes implementing this interface will have the {@link ScenarioEndpoint} injected after instantiation at runtime.
 * Similar to an initialized Spring bean, but controlled by the simulator.
 * <code>
 *
 * @Override public void afterPropertiesSet() {
 * scenarioEndpoint = new ScenarioEndpoint(new ScenarioEndpointConfiguration());
 * }
 * </code>
 * @see org.springframework.beans.factory.InitializingBean
 */
public interface SimulatorScenarioWithEndpoint extends SimulatorScenario {

    void setScenarioEndpoint(@Nullable ScenarioEndpoint scenarioEndpoint);
}
