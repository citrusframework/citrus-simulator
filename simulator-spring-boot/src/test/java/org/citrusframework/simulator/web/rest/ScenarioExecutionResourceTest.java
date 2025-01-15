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

import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.service.ScenarioExecutionQueryService;
import org.citrusframework.simulator.service.ScenarioExecutionQueryService.ResultDetailsConfiguration;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.citrusframework.simulator.service.criteria.ScenarioExecutionCriteria;
import org.citrusframework.simulator.web.rest.dto.ScenarioExecutionDTO;
import org.citrusframework.simulator.web.rest.dto.mapper.ScenarioExecutionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.captor;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.context.request.RequestContextHolder.setRequestAttributes;

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
        fixture = new ScenarioExecutionResource(scenarioExecutionServiceMock,
            scenarioExecutionQueryServiceMock, scenarioExecutionMapperMock);
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
        private ArgumentCaptor<ResultDetailsConfiguration> resultDetailsConfigurationArgumentCaptor;

        @BeforeEach
        void beforeEachSetup() {
            var request = new MockHttpServletRequest();
            setRequestAttributes(new ServletRequestAttributes(request));

            scenarioExecution = new ScenarioExecution();
            var scenarioExecutions = new PageImpl<>(singletonList(scenarioExecution));

            resultDetailsConfigurationArgumentCaptor = captor();
            doReturn(scenarioExecutions)
                .when(scenarioExecutionQueryServiceMock)
                .findByCriteria(eq(criteriaMock), eq(pageableMock), resultDetailsConfigurationArgumentCaptor.capture());

            doReturn(scenarioExecutionDTOMock)
                .when(scenarioExecutionMapperMock)
                .toDto(scenarioExecution);
        }

        @Test
        void stripsIncludedActions() {
            var response = fixture.getAllScenarioExecutions(criteriaMock, FALSE, TRUE, TRUE, TRUE, pageableMock);
            verifyResponseContainsDtos(response);

            assertThat(resultDetailsConfigurationArgumentCaptor.getValue())
                .isNotNull()
                .satisfies(
                    r -> assertThat(r.includeActions()).isFalse(),
                    r -> assertThat(r.includeMessages()).isTrue(),
                    r -> assertThat(r.includeMessageHeaders()).isTrue(),
                    r -> assertThat(r.includeParameters()).isTrue()
                );
        }

        @Test
        void stripsIncludedMessages() {
            var response = fixture.getAllScenarioExecutions(criteriaMock, TRUE, FALSE, TRUE, TRUE, pageableMock);
            verifyResponseContainsDtos(response);

            assertThat(resultDetailsConfigurationArgumentCaptor.getValue())
                .isNotNull()
                .satisfies(
                    r -> assertThat(r.includeActions()).isTrue(),
                    r -> assertThat(r.includeMessages()).isFalse(),
                    r -> assertThat(r.includeMessageHeaders()).isTrue(),
                    r -> assertThat(r.includeParameters()).isTrue()
                );
        }

        @Test
        void stripsIncludedMessageHeaderss() {
            var response = fixture.getAllScenarioExecutions(criteriaMock, TRUE, TRUE, FALSE, TRUE, pageableMock);
            verifyResponseContainsDtos(response);

            assertThat(resultDetailsConfigurationArgumentCaptor.getValue())
                .isNotNull()
                .satisfies(
                    r -> assertThat(r.includeActions()).isTrue(),
                    r -> assertThat(r.includeMessages()).isTrue(),
                    r -> assertThat(r.includeMessageHeaders()).isFalse(),
                    r -> assertThat(r.includeParameters()).isTrue()
                );
        }

        @Test
        void stripsIncludedParameters() {
            var response = fixture.getAllScenarioExecutions(criteriaMock, TRUE, TRUE, TRUE, FALSE, pageableMock);
            verifyResponseContainsDtos(response);

            assertThat(resultDetailsConfigurationArgumentCaptor.getValue())
                .isNotNull()
                .satisfies(
                    r -> assertThat(r.includeActions()).isTrue(),
                    r -> assertThat(r.includeMessages()).isTrue(),
                    r -> assertThat(r.includeMessageHeaders()).isTrue(),
                    r -> assertThat(r.includeParameters()).isFalse()
                );
        }

        private void verifyResponseContainsDtos(ResponseEntity<List<ScenarioExecutionDTO>> response) {
            assertThat(response)
                .satisfies(
                    r -> assertThat(r.getStatusCode()).isEqualTo(OK),
                    r -> assertThat(r.getBody())
                        .singleElement()
                        .isEqualTo(scenarioExecutionDTOMock)
                );
        }
    }
}
