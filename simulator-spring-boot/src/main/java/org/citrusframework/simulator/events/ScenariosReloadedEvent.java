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
import org.springframework.context.ApplicationEvent;

import java.util.Set;

public final class ScenariosReloadedEvent extends ApplicationEvent {

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
