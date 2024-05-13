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

package org.citrusframework.simulator.events;

import org.citrusframework.simulator.service.ScenarioLookupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ScenariosReloadedEventTest {

    private static final Set<String> MOCK_SCENARIO_NAMES = Set.of("Scenario1", "Scenario2");
    private static final Set<String> MOCK_STARTER_NAMES = Set.of("Starter1", "Starter2");

    @Mock
    private ScenarioLookupService scenarioLookupServiceMock;

    private ScenariosReloadedEvent fixture;

    @BeforeEach
    void beforeEachSetup() {
        doReturn(MOCK_SCENARIO_NAMES).when(scenarioLookupServiceMock).getScenarioNames();
        doReturn(MOCK_STARTER_NAMES).when(scenarioLookupServiceMock).getStarterNames();

        fixture = new ScenariosReloadedEvent(scenarioLookupServiceMock);
    }

    @Test
    void extractInformationFromSource() {
        assertEquals(MOCK_SCENARIO_NAMES, fixture.getScenarioNames());
        assertEquals(MOCK_STARTER_NAMES, fixture.getScenarioStarterNames());
    }
}
