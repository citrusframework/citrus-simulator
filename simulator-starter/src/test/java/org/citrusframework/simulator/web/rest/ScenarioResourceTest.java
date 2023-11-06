package org.citrusframework.simulator.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.util.List;
import java.util.Set;
import org.citrusframework.simulator.events.ScenariosReloadedEvent;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.citrusframework.simulator.service.ScenarioLookupService;
import org.citrusframework.simulator.web.rest.ScenarioResource.Scenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ScenarioResourceTest {

    @Mock
    private ScenarioExecutorService scenarioExecutorServiceMock;

    @Mock
    private ScenarioLookupService scenarioLookupServiceMock;

    private ScenarioResource fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new ScenarioResource(scenarioExecutorServiceMock, scenarioLookupServiceMock);
    }

    @Test
    void evictAndReloadScenarioCacheIsIdempotent() {
        Set<String> mockScenarioNames = Set.of("Scenario2", "Scenario1");
        Set<String> mockStarterNames = Set.of("Starter2", "Starter1");

        doReturn(mockScenarioNames).when(scenarioLookupServiceMock).getScenarioNames();
        doReturn(mockStarterNames).when(scenarioLookupServiceMock).getStarterNames();

        fixture.evictAndReloadScenarioCache(new ScenariosReloadedEvent(scenarioLookupServiceMock));
        verifyEvictAndReloadCache();

        // Check that the cache is really evicted and reloaded, not appended
        fixture.evictAndReloadScenarioCache(new ScenariosReloadedEvent(scenarioLookupServiceMock));
        verifyEvictAndReloadCache();
    }

    private void verifyEvictAndReloadCache() {
        assertThat((List<Scenario>) ReflectionTestUtils.getField(fixture, "scenarioCache"))
            .hasSize(4)
            .extracting("name")
            .containsExactly("Scenario1", "Scenario2", "Starter1", "Starter2");
    }
}
