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

package org.citrusframework.simulator.service.impl;

import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.repository.ScenarioParameterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.data.domain.Pageable.unpaged;

@ExtendWith(MockitoExtension.class)
class ScenarioParameterServiceImplTest {

    @Mock
    private ScenarioParameterRepository scenarioParameterRepositoryMock;

    private ScenarioParameter sampleScenarioParameter;

    private ScenarioParameterServiceImpl fixture;

    @BeforeEach
    void beforeEachSetup() {
        sampleScenarioParameter = new ScenarioParameter();
        // You can add any necessary initialization for the ScenarioParameter here.
        // sampleScenarioParameter.set...

        fixture = new ScenarioParameterServiceImpl(scenarioParameterRepositoryMock);
    }

    @Test
    void testSave() {
        doReturn(sampleScenarioParameter).when(scenarioParameterRepositoryMock).save(sampleScenarioParameter);

        ScenarioParameter savedScenarioParameter = fixture.save(sampleScenarioParameter);
        assertEquals(sampleScenarioParameter, savedScenarioParameter);
    }

    @Test
    void testFindAll() {
        Pageable pageable = unpaged();
        Page<ScenarioParameter> page = new PageImpl<>(List.of(sampleScenarioParameter));

        doReturn(page).when(scenarioParameterRepositoryMock).findAllWithEagerRelationships(pageable);

        Page<ScenarioParameter> result = fixture.findAll(pageable);

        assertEquals(page, result);
    }

    @Test
    void testFindOne() {
        Long scenarioParameterId = 1L;

        doReturn(Optional.of(sampleScenarioParameter)).when(scenarioParameterRepositoryMock).findOneWithEagerRelationships(scenarioParameterId);

        Optional<ScenarioParameter> maybeScenarioParameter = fixture.findOne(scenarioParameterId);

        assertTrue(maybeScenarioParameter.isPresent());
        assertEquals(sampleScenarioParameter, maybeScenarioParameter.get());
    }

    @Test
    void testDelete() {
        Long scenarioParameterId = 1L;

        doNothing().when(scenarioParameterRepositoryMock).deleteById(scenarioParameterId);

        fixture.delete(scenarioParameterId);

        verify(scenarioParameterRepositoryMock).deleteById(scenarioParameterId);
    }
}
