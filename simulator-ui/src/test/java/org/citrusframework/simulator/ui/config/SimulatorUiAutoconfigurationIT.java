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

package org.citrusframework.simulator.ui.config;

import org.citrusframework.simulator.ui.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@IntegrationTest
public class SimulatorUiAutoconfigurationIT {

    @Autowired
    private SimulatorUiAutoconfiguration simulatorUiAutoconfiguration;

    @Test
    void isEnabledByDefault() {
        assertNotNull(simulatorUiAutoconfiguration, "Simulator UI autoconfiguration is enabled by default, whenever simulator-ui is on the classpath.");
    }
}
