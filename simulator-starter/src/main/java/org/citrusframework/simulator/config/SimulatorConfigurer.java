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

package org.citrusframework.simulator.config;

import com.consol.citrus.endpoint.EndpointAdapter;
import org.citrusframework.simulator.scenario.mapper.ScenarioMapper;

/**
 * @author Christoph Deppisch
 */
public interface SimulatorConfigurer {

    /**
     * Gets the scenario mapper.
     *
     * @return
     */
    ScenarioMapper scenarioMapper();

    /**
     * Gets the fallback endpoint adapter that is used when no response has been generated from scenario.
     *
     * @return
     */
    EndpointAdapter fallbackEndpointAdapter();

    /**
     * Gets the delay in milliseconds to wait after uncategorized exceptions.
     * @param simulatorConfiguration
     *
     * @return
     */
    Long exceptionDelay(SimulatorConfigurationProperties simulatorConfiguration);
}
