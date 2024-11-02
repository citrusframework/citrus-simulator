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

package org.citrusframework.simulator.web.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Nullable;
import org.citrusframework.TestCaseRunner;
import org.citrusframework.simulator.common.FeatureFlagNotEnabledException;
import org.citrusframework.simulator.events.ScenariosReloadedEvent;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.simulator.scenario.ScenarioStarter;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.citrusframework.simulator.service.ScenarioLookupService;
import org.citrusframework.simulator.service.ScenarioRegistrationService;
import org.citrusframework.simulator.web.rest.ScenarioResource.Scenario;
import org.citrusframework.simulator.web.rest.dto.mapper.ScenarioParameterMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.citrusframework.simulator.config.OpenFeatureConfig.EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED;
import static org.citrusframework.simulator.web.rest.ScenarioResource.Scenario.ScenarioType.MESSAGE_TRIGGERED;
import static org.citrusframework.simulator.web.rest.ScenarioResource.Scenario.ScenarioType.STARTER;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.data.domain.Pageable.unpaged;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
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

    @Mock
    private ScenarioRegistrationService scenarioRegistrationServiceMock;

    @Mock
    private ScenarioParameterMapper scenarioParameterMapperMock;

    private ScenarioResource fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new ScenarioResource(scenarioExecutorServiceMock, scenarioLookupServiceMock, scenarioRegistrationServiceMock, scenarioParameterMapperMock);
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
            new Scenario("efg", STARTER),
            new Scenario("$#&", STARTER)
        );

        static Stream<Arguments> doesFilterCacheWithNameContains() {
            return Stream.of(
                arguments("b", "abc"),
                arguments("#", "$#&")
            );
        }

        @MethodSource
        @ParameterizedTest
        void doesFilterCacheWithNameContains(String filterLetter, String expectedScenario) {
            setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

            setField(fixture, "scenarioCache", SCENARIO_CACHE);

            var result = fixture.getScenarios(Optional.of(filterLetter), unpaged());

            assertThat(result)
                .extracting(ResponseEntity::getBody)
                .asInstanceOf(LIST)
                .hasSize(1)
                .first()
                .asInstanceOf(type(Scenario.class))
                .extracting(Scenario::name)
                .isEqualTo(expectedScenario);
        }

        @Test
        void doesFilterCacheWithNameStartsOrEndsWith() {
            setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

            setField(fixture, "scenarioCache", SCENARIO_CACHE);

            String filterLetter = "e";
            var result = fixture.getScenarios(Optional.of(filterLetter), unpaged());

            assertThat(result)
                .extracting(ResponseEntity::getBody)
                .asInstanceOf(LIST)
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

            var result = fixture.getScenarios(Optional.empty(), unpaged());

            assertThat(result)
                .extracting(ResponseEntity::getBody)
                .asInstanceOf(LIST)
                .isEqualTo(SCENARIO_CACHE);
        }

        @AfterEach
        void afterEachTeardown() {
            resetRequestAttributes();
        }
    }

    @Nested
    class UploadScenario {

        private static final String SCENARIO_NAME = "TestScenario";
        private static final String JAVA_SOURCE_CODE = "public class TestScenario implements ScenarioStarter { }";

        @Test
        void shouldCreateStarterScenarioSuccessfully() throws FeatureFlagNotEnabledException {
            var simulatorScenario = new ScenarioStarter() {
                @Override
                public ScenarioEndpoint getScenarioEndpoint() {
                    return null;
                }

                @Override
                public String getName() {
                    return SCENARIO_NAME;
                }

                @Nullable
                @Override
                public TestCaseRunner getTestCaseRunner() {
                    return null;
                }

                @Override
                public void setTestCaseRunner(TestCaseRunner testCaseRunner) {

                }
            };

            doReturn(simulatorScenario)
                .when(scenarioRegistrationServiceMock)
                .registerScenarioFromJavaSourceCode(
                    eq(SCENARIO_NAME),
                    eq(JAVA_SOURCE_CODE));

            // When
            ResponseEntity<?> response = fixture.uploadScenario(SCENARIO_NAME, JAVA_SOURCE_CODE);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(CREATED);
            assertThat(response.getHeaders().getLocation())
                .hasToString("/api/scenarios/" + SCENARIO_NAME);

            Scenario responseBody = (Scenario) response.getBody();
            assertThat(responseBody)
                .isNotNull()
                .satisfies(
                    scenario -> assertThat(scenario.name()).isEqualTo(SCENARIO_NAME),
                    scenario -> assertThat(scenario.type()).isEqualTo(STARTER)
                );
        }

        @Test
        void shouldCreateMessageTriggeredScenarioSuccessfully() throws FeatureFlagNotEnabledException {
            var simulatorScenario = new SimulatorScenario() {
                @Override
                public ScenarioEndpoint getScenarioEndpoint() {
                    return null;
                }

                @Override
                public String getName() {
                    return SCENARIO_NAME;
                }

                @Nullable
                @Override
                public TestCaseRunner getTestCaseRunner() {
                    return null;
                }

                @Override
                public void setTestCaseRunner(TestCaseRunner testCaseRunner) {

                }
            };

            doReturn(simulatorScenario)
                .when(scenarioRegistrationServiceMock)
                .registerScenarioFromJavaSourceCode(
                    eq(SCENARIO_NAME),
                    eq(JAVA_SOURCE_CODE)
                );

            // When
            ResponseEntity<?> response = fixture.uploadScenario(SCENARIO_NAME, JAVA_SOURCE_CODE);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(CREATED);
            assertThat(response.getHeaders().getLocation())
                .hasToString("/api/scenarios/" + SCENARIO_NAME);

            Scenario responseBody = (Scenario) response.getBody();
            assertThat(responseBody)
                .isNotNull()
                .satisfies(scenario -> assertThat(scenario.name()).isEqualTo(SCENARIO_NAME),
                    scenario -> assertThat(scenario.type()).isEqualTo(MESSAGE_TRIGGERED)
                );
        }

        @Test
        void shouldReturnNotImplementedWhenFeatureFlagNotEnabled() throws FeatureFlagNotEnabledException {
            doThrow(new FeatureFlagNotEnabledException(EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED))
                .when(scenarioRegistrationServiceMock)
                .registerScenarioFromJavaSourceCode(
                    anyString(),
                    anyString()
                );

            ResponseEntity<?> response = fixture.uploadScenario(SCENARIO_NAME, JAVA_SOURCE_CODE);

            assertThat(response.getStatusCode()).isEqualTo(NOT_IMPLEMENTED);

            ObjectNode responseBody = (ObjectNode) response.getBody();
            assertThat(responseBody)
                .isNotNull();

            assertThat(responseBody.get("message").asText())
                .isEqualTo("Feature flag not enabled.");
            assertThat(responseBody.get("flag").asText())
                .isEqualTo(EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED);
        }
    }
}
