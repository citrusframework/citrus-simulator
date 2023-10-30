package org.citrusframework.simulator.events;

import java.util.Set;
import org.citrusframework.simulator.service.ScenarioLookupService;
import org.springframework.context.ApplicationEvent;

public  final class ScenariosReloadedEvent extends ApplicationEvent {

    private final Set<String> scenarioNames;
    private final Set<String> scenarioStarterNames;

    public ScenariosReloadedEvent(ScenarioLookupService source) {
        super(source);

        this.scenarioNames = source.getScenarioNames();
        this.scenarioStarterNames = source.getStarterNames();
    }

    public Set<String> getScenarioNames() {
        return scenarioNames;
    }

    public Set<String> getScenarioStarterNames() {
        return scenarioStarterNames;
    }
}
