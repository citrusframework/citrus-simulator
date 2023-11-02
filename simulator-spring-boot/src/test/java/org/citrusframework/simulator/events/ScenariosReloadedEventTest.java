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
