package org.citrusframework.simulator.service.impl;

import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.repository.ScenarioExecutionRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScenarioExecutionServiceImplTest {

    @Mock
    private ScenarioExecutionRepository scenarioExecutionRepositoryMock;

    private ScenarioExecution sampleScenarioExecution;

    private ScenarioExecutionServiceImpl fixture;

    @BeforeEach
    void beforeEachSetup() {
        sampleScenarioExecution = new ScenarioExecution();

        fixture = new ScenarioExecutionServiceImpl(scenarioExecutionRepositoryMock);
    }

    @Test
    void testSave() {
        when(scenarioExecutionRepositoryMock.save(sampleScenarioExecution)).thenReturn(sampleScenarioExecution);

        ScenarioExecution savedScenarioExecution = fixture.save(sampleScenarioExecution);
        assertEquals(sampleScenarioExecution, savedScenarioExecution);
    }

    @Test
    void testFindAll() {
        Pageable pageable = Pageable.unpaged();
        Page<ScenarioExecution> page = new PageImpl<>(List.of(sampleScenarioExecution));

        when(scenarioExecutionRepositoryMock.findAll(pageable)).thenReturn(page);

        Page<ScenarioExecution> result = fixture.findAll(pageable);

        assertEquals(page, result);
    }

    @Test
    void testFindOne() {
        Long scenarioExecutionId = 1L;

        when(scenarioExecutionRepositoryMock.findById(scenarioExecutionId)).thenReturn(Optional.of(sampleScenarioExecution));

        Optional<ScenarioExecution> maybeScenarioExecution = fixture.findOne(scenarioExecutionId);

        assertTrue(maybeScenarioExecution.isPresent());
        assertEquals(sampleScenarioExecution, maybeScenarioExecution.get());
    }
}
