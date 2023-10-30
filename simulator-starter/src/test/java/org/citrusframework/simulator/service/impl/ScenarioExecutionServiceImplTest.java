package org.citrusframework.simulator.service.impl;

import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.repository.ScenarioExecutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ScenarioExecutionServiceImplTest {

    @Mock
    private ScenarioExecutionRepository scenarioExecutionRepositoryMock;

    @Mock
    TimeProvider timeProviderMock;

    private ScenarioExecution sampleScenarioExecution;

    private ScenarioExecutionServiceImpl fixture;

    @BeforeEach
    void beforeEachSetup() {
        sampleScenarioExecution = new ScenarioExecution();

        fixture = new ScenarioExecutionServiceImpl(scenarioExecutionRepositoryMock);
        ReflectionTestUtils.setField(fixture, "timeProvider", timeProviderMock, TimeProvider.class);
    }

    @Test
    void testSave() {
        doReturn(sampleScenarioExecution).when(scenarioExecutionRepositoryMock).save(sampleScenarioExecution);

        ScenarioExecution savedScenarioExecution = fixture.save(sampleScenarioExecution);
        assertEquals(sampleScenarioExecution, savedScenarioExecution);
    }

    @Test
    void testFindAll() {
        Pageable pageable = Pageable.unpaged();
        Page<ScenarioExecution> page = new PageImpl<>(List.of(sampleScenarioExecution));

        doReturn(page).when(scenarioExecutionRepositoryMock).findAll(pageable);

        Page<ScenarioExecution> result = fixture.findAll(pageable);

        assertEquals(page, result);
    }

    @Test
    void testFindOne() {
        Long scenarioExecutionId = 1L;

        doReturn(Optional.of(sampleScenarioExecution)).when(scenarioExecutionRepositoryMock).findById(scenarioExecutionId);

        Optional<ScenarioExecution> maybeScenarioExecution = fixture.findOne(scenarioExecutionId);

        assertTrue(maybeScenarioExecution.isPresent());
        assertEquals(sampleScenarioExecution, maybeScenarioExecution.get());
    }

    @Test
    void testCreateAndSaveExecutionScenario() {
        String scenarioName = "sampleScenario";

        Instant now = Instant.now();
        doReturn(now).when(timeProviderMock).getTimeNow();

        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ScenarioExecution.class)).when(scenarioExecutionRepositoryMock).save(any(ScenarioExecution.class));

        ScenarioParameter scenarioParameter = new ScenarioParameter();
        List<ScenarioParameter> scenarioParameters = List.of(scenarioParameter);

        ScenarioExecution result = fixture.createAndSaveExecutionScenario(scenarioName, scenarioParameters);

        assertEquals(scenarioName, result.getScenarioName());
        assertEquals(now, result.getStartDate());
        assertEquals(ScenarioExecution.Status.RUNNING, result.getStatus());
        assertThat(result.getScenarioParameters())
            .hasSize(1)
            .containsExactly(scenarioParameter);
    }
}
