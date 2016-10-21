/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.simulator.sample.scenario;

import com.consol.citrus.simulator.scenario.Scenario;
import com.consol.citrus.simulator.jms.SimulatorJmsScenario;

/**
 * @author Christoph Deppisch
 */
@Scenario("DEFAULT_SCENARIO")
public class DefaultScenario extends SimulatorJmsScenario {

    @Override
    protected void configure() {
        scenario()
            .receive();

        scenario()
            .send()
            .payload("<DefaultResponse>This is a default response!</DefaultResponse>");
    }
}
