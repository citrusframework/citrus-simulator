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

package org.citrusframework.simulator.web.rest;

import org.citrusframework.simulator.events.ScenariosReloadedEvent;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.citrusframework.simulator.service.ScenarioLookupService;
import org.citrusframework.simulator.web.rest.ScenarioResource.Scenario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.citrusframework.simulator.web.rest.ScenarioResource.Scenario.ScenarioType.STARTER;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes;
import static org.springframework.web.context.request.RequestContextHolder.setRequestAttributes;

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
        assertThat((List<Scenario>) getField(fixture, "scenarioCache"))
            .hasSize(4)
            .extracting("name")
            .containsExactly("Scenario1", "Scenario2", "Starter1", "Starter2");
    }

    @Nested
    class GetScenarios {

        private static final List<Scenario> SCENARIO_CACHE = asList(
            new Scenario("abc", STARTER),
            new Scenario("cde", STARTER),
            new Scenario("efg", STARTER)
        );

        @Test
        void doesFilterCacheWithNameContains() {
            setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

            setField(fixture, "scenarioCache", SCENARIO_CACHE);

            String filterLetter = "b";
            var result = fixture.getScenarios(Optional.of(filterLetter), Pageable.unpaged());

            assertThat(result)
                .extracting(ResponseEntity::getBody)
                .asList()
                .hasSize(1)
                .first()
                .asInstanceOf(type(Scenario.class))
                .extracting(Scenario::name)
                .isEqualTo("abc");
        }

        @Test
        void doesFilterCacheWithNameStartsOrEndsWith() {
            setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

            setField(fixture, "scenarioCache", SCENARIO_CACHE);

            String filterLetter = "e";
            var result = fixture.getScenarios(Optional.of(filterLetter), Pageable.unpaged());

            assertThat(result)
                .extracting(ResponseEntity::getBody)
                .asList()
                .hasSize(2)
                .noneSatisfy(scenario ->
                    assertThat(scenario)
                        .asInstanceOf(type(Scenario.class))
                        .extracting(Scenario::name)
                        .isEqualTo("abc")
                );
        }

        @Test
        void doesNotFilterCacheWithoutNameContains() {
            setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

            setField(fixture, "scenarioCache", SCENARIO_CACHE);

            var result = fixture.getScenarios(Optional.empty(), Pageable.unpaged());

            assertThat(result)
                .extracting(ResponseEntity::getBody)
                .asList()
                .isEqualTo(SCENARIO_CACHE);
        }

        @AfterEach
        void afterEachTeardown() {
            resetRequestAttributes();
        }
    }
}
