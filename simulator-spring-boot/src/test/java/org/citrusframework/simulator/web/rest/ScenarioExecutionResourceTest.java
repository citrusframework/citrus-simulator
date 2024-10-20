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

import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.ScenarioAction;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.service.ScenarioExecutionQueryService;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.citrusframework.simulator.service.criteria.ScenarioExecutionCriteria;
import org.citrusframework.simulator.web.rest.dto.ScenarioExecutionDTO;
import org.citrusframework.simulator.web.rest.dto.mapper.ScenarioExecutionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith({MockitoExtension.class})
class ScenarioExecutionResourceTest {

    @Mock
    private ScenarioExecutionService scenarioExecutionServiceMock;

    @Mock
    private ScenarioExecutionQueryService scenarioExecutionQueryServiceMock;

    @Mock
    private ScenarioExecutionMapper scenarioExecutionMapperMock;

    private ScenarioExecutionResource fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new ScenarioExecutionResource(scenarioExecutionServiceMock, scenarioExecutionQueryServiceMock, scenarioExecutionMapperMock);
    }

    @Nested
    class GetAllScenarioExecutions {

        @Mock
        private ScenarioExecutionCriteria criteriaMock;

        @Mock
        private Pageable pageableMock;

        @Mock
        private ScenarioExecutionDTO scenarioExecutionDTOMock;

        private ScenarioExecution scenarioExecution;

        @BeforeEach
        void beforeEachSetup() {
            var request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            scenarioExecution = new ScenarioExecution();
            scenarioExecution.getScenarioActions().add(mock(ScenarioAction.class));
            scenarioExecution.getScenarioMessages().add(mock(Message.class));
            scenarioExecution.getScenarioParameters().add(mock(ScenarioParameter.class));

            var scenarioExecutions = new PageImpl<>(singletonList(scenarioExecution));
            doReturn(scenarioExecutions).when(scenarioExecutionQueryServiceMock).findByCriteria(criteriaMock, pageableMock);

            doReturn(scenarioExecutionDTOMock).when(scenarioExecutionMapperMock).toDto(scenarioExecution);
        }

        @Test
        void stripsIncludedActions() {
            var response = fixture.getAllScenarioExecutions(criteriaMock, FALSE, TRUE, TRUE, pageableMock);

            assertThat(response)
                .satisfies(
                    r -> assertThat(r.getStatusCode()).isEqualTo(OK),
                    r -> assertThat(r.getBody())
                        .singleElement()
                        .isEqualTo(scenarioExecutionDTOMock)
                );

            assertThat(scenarioExecution.getScenarioActions()).isEmpty();
            assertThat(scenarioExecution.getScenarioMessages()).isNotEmpty();
            assertThat(scenarioExecution.getScenarioParameters()).isNotEmpty();
        }

        @Test
        void stripsIncludedMessages() {
            var response = fixture.getAllScenarioExecutions(criteriaMock, TRUE, FALSE, TRUE, pageableMock);

            assertThat(response)
                .satisfies(
                    r -> assertThat(r.getStatusCode()).isEqualTo(OK),
                    r -> assertThat(r.getBody())
                        .singleElement()
                        .isEqualTo(scenarioExecutionDTOMock)
                );

            assertThat(scenarioExecution.getScenarioActions()).isNotEmpty();
            assertThat(scenarioExecution.getScenarioMessages()).isEmpty();
            assertThat(scenarioExecution.getScenarioParameters()).isNotEmpty();
        }

        @Test
        void stripsIncludedParameters() {
            var response = fixture.getAllScenarioExecutions(criteriaMock, TRUE, TRUE, FALSE, pageableMock);

            assertThat(response)
                .satisfies(
                    r -> assertThat(r.getStatusCode()).isEqualTo(OK),
                    r -> assertThat(r.getBody())
                        .singleElement()
                        .isEqualTo(scenarioExecutionDTOMock)
                );

            assertThat(scenarioExecution.getScenarioActions()).isNotEmpty();
            assertThat(scenarioExecution.getScenarioMessages()).isNotEmpty();
            assertThat(scenarioExecution.getScenarioParameters()).isEmpty();
        }
    }
}
