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

package org.citrusframework.simulator.service.impl;

import org.apache.commons.lang3.NotImplementedException;
import org.citrusframework.simulator.events.ScenariosReloadedEvent;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.simulator.scenario.ScenarioStarter;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.scenario.Starter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ScenarioLookupServiceImplTest {

    public static final String SCENARIO_NAME = "ScenarioLookupServiceImplTest#testSimulatorScenario";
    public static final String STARTER_NAME = "ScenarioLookupServiceImplTest#testScenarioStarter";

    private static final ScenarioParameter SCENARIO_PARAMETER = ScenarioParameter.builder()
        .name("parameter-name")
        .value("parameter-value")
        .build();

    @Mock
    private ApplicationContext applicationContextMock;

    private ScenarioLookupServiceImpl fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new ScenarioLookupServiceImpl(applicationContextMock);
    }

    static Stream<Arguments> evictAndReloadScenarioCacheIsIdempotent() {
        return Stream.of(
            Arguments.of(
                (Consumer<ScenarioLookupServiceImpl>) ScenarioLookupServiceImpl::afterPropertiesSet),
            Arguments.of(
                (Consumer<ScenarioLookupServiceImpl>) ScenarioLookupServiceImpl::evictAndReloadScenarioCache)
        );
    }

    @MethodSource
    @ParameterizedTest
    void evictAndReloadScenarioCacheIsIdempotent(Consumer<ScenarioLookupServiceImpl> invocation) {
        final String testSimulatorScenario = SCENARIO_NAME;
        Map<String, SimulatorScenario> contextSimulatorScenarios = Map.of(testSimulatorScenario, new TestSimulatorScenario(), "invalidTestSimulatorScenario", new InvalidTestSimulatorScenario());
        doReturn(contextSimulatorScenarios).when(applicationContextMock).getBeansOfType(SimulatorScenario.class);

        final String testScenarioStarter = STARTER_NAME;
        Map<String, ScenarioStarter> contextScenarioStarters = Map.of(testScenarioStarter, new TetsScenarioStarter());
        doReturn(contextScenarioStarters).when(applicationContextMock).getBeansOfType(ScenarioStarter.class);

        invocation.accept(fixture);
        verifyScenariosHaveBeenReloadedFromApplicationContext(testSimulatorScenario, testScenarioStarter);

        clearInvocations(applicationContextMock);

        // Check that the cache is really evicted and reloaded, not appended
        invocation.accept(fixture);
        verifyScenariosHaveBeenReloadedFromApplicationContext(testSimulatorScenario, testScenarioStarter);
    }

    private void verifyScenariosHaveBeenReloadedFromApplicationContext(String testSimulatorScenario, String testScenarioStarter) {
        Map<String, SimulatorScenario> simulatorScenarios = (Map<String, SimulatorScenario>) ReflectionTestUtils.getField(fixture, "scenarios");
        assertThat(simulatorScenarios).hasSize(1);

        Map<String, ScenarioStarter> scenarioStarters = (Map<String, ScenarioStarter>) ReflectionTestUtils.getField(fixture, "scenarioStarters");
        assertThat(scenarioStarters).hasSize(1);

        ArgumentCaptor<ScenariosReloadedEvent> scenariosReloadedEventArgumentCaptor = ArgumentCaptor.forClass(ScenariosReloadedEvent.class);
        verify(applicationContextMock).publishEvent(scenariosReloadedEventArgumentCaptor.capture());

        ScenariosReloadedEvent scenariosReloadedEvent = scenariosReloadedEventArgumentCaptor.getValue();
        assertThat(scenariosReloadedEvent.getScenarioNames()).containsExactly(testSimulatorScenario);
        assertThat(scenariosReloadedEvent.getScenarioStarterNames()).containsExactly(testScenarioStarter);
    }

    @Test
    void getScenarioNames() {
        final String testSimulatorScenario = SCENARIO_NAME;
        ReflectionTestUtils.setField(fixture, "scenarios", Map.of(testSimulatorScenario, new TestSimulatorScenario()), Map.class);

        assertThat(fixture.getScenarioNames())
            .hasSize(1)
            .containsExactly(testSimulatorScenario);
    }

    @Test
    void getStarterNames() {
        final String testScenarioStarter = STARTER_NAME;
        ReflectionTestUtils.setField(fixture, "scenarioStarters", Map.of(testScenarioStarter, new TetsScenarioStarter()), Map.class);

        assertThat(fixture.getStarterNames())
            .hasSize(1)
            .containsExactly(testScenarioStarter);
    }

    @Test
    void lookupScenarioParameters() {
        String scenarioName = "scenarioName";

        Map<String, ScenarioStarter> scenarioStartersMock = Map.of(scenarioName, new TetsScenarioStarter());
        ReflectionTestUtils.setField(fixture, "scenarioStarters", scenarioStartersMock, Map.class);

        assertThat(fixture.lookupScenarioParameters(scenarioName))
            .hasSize(1)
            .containsExactly(SCENARIO_PARAMETER);
    }

    @Test
    void lookupScenarioParametersReturnsEmptyListForInvalidScenarioNames() {
        String scenarioName = "scenarioName";

        Map<String, ScenarioStarter> scenarioStartersMock = mock(Map.class);
        ReflectionTestUtils.setField(fixture, "scenarioStarters", scenarioStartersMock, Map.class);

        doReturn(false).when(scenarioStartersMock).containsKey(scenarioName);

        assertThat(fixture.lookupScenarioParameters(scenarioName))
            .isEmpty();
    }

    @Scenario(SCENARIO_NAME)
    private static class TestSimulatorScenario implements SimulatorScenario {

        @Override
        public ScenarioEndpoint getScenarioEndpoint() {
            throw new NotImplementedException();
        }
    }

    @Starter(STARTER_NAME)
    private static class TetsScenarioStarter implements ScenarioStarter {

        @Override
        public List<ScenarioParameter> getScenarioParameters() {
            return List.of(SCENARIO_PARAMETER);
        }

        @Override
        public ScenarioEndpoint getScenarioEndpoint() {
            throw new NotImplementedException();
        }
    }

    @Starter("ScenarioLookupServiceImplTest#invalidTestScenarioStarter")
    private static class InvalidTestSimulatorScenario implements SimulatorScenario {

        @Override
        public ScenarioEndpoint getScenarioEndpoint() {
            throw new NotImplementedException();
        }
    }
}
