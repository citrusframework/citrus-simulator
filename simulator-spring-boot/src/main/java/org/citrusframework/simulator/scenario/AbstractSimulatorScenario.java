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

import org.citrusframework.simulator.correlation.CorrelationHandlerBuilder;
import org.springframework.beans.factory.InitializingBean;

import static java.util.Objects.isNull;

@Deprecated(forRemoval = true)
public abstract class AbstractSimulatorScenario extends SimpleInitializedSimulatorScenario implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        if (isNull(getScenarioEndpoint())) {
            setScenarioEndpoint(new DefaultScenarioEndpoint(new ScenarioEndpointConfiguration()));
        }
    }

    /**
     * Start new message correlation so scenario is provided with additional inbound messages.
     */
    public CorrelationHandlerBuilder correlation() {
        return new CorrelationHandlerBuilder(getScenarioEndpoint());
    }
}
