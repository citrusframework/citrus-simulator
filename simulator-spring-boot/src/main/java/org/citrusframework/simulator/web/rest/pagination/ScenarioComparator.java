/*
 * Copyright 2024 the original author or authors.
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

package org.citrusframework.simulator.web.rest.pagination;

import org.citrusframework.simulator.web.rest.ScenarioResource;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

public class ScenarioComparator implements Comparator<ScenarioResource.Scenario> {

    private final Function<ScenarioResource.Scenario, Object> propertyExtractor;

    private ScenarioComparator(Function<ScenarioResource.Scenario, Object> propertyExtractor) {
        this.propertyExtractor = propertyExtractor;
    }

    public static Optional<ScenarioComparator> fromProperty(String property) {
        return switch (property.toLowerCase()) {
            case "name" -> Optional.of(new ScenarioComparator(ScenarioResource.Scenario::name));
            case "type" -> Optional.of(new ScenarioComparator(ScenarioResource.Scenario::type));
            default -> Optional.empty();
        };
    }

    @Override
    public int compare(ScenarioResource.Scenario scenario1, ScenarioResource.Scenario scenario2) {
        Object prop1 = propertyExtractor.apply(scenario1);
        Object prop2 = propertyExtractor.apply(scenario2);

        if (prop1 == null && prop2 == null) {
            return 0;
        } else if (prop1 == null) {
            return -1;
        } else if (prop2 == null) {
            return 1;
        }

        if (prop1 instanceof Comparable comparable1 && prop2 instanceof Comparable comparable2) {
            return comparable1.compareTo(comparable2);
        }

        throw new IllegalArgumentException("The properties must be Comparable");

    }
}
