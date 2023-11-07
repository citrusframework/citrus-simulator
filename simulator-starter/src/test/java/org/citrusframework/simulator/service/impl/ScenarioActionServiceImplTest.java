package org.citrusframework.simulator.service.impl;

import org.citrusframework.simulator.model.ScenarioAction;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.repository.ScenarioActionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScenarioActionServiceImplTest {

    @Mock
    private ScenarioActionRepository scenarioActionRepositoryMock;

    private ScenarioAction scenarioActionWithScenarioExecution;

    private ScenarioActionServiceImpl fixture;

    @BeforeEach
    void beforeEachSetup()  {
        ScenarioAction scenarioAction = new ScenarioAction();
        ScenarioExecution scenarioExecution = ScenarioExecution.builder()
            .startDate(Instant.now())
            .endDate(Instant.now())
            .scenarioName("scenario-name")
            .errorMessage("error-message")
            .build();
        scenarioAction.setScenarioExecution(scenarioExecution);
        scenarioActionWithScenarioExecution = spy(scenarioAction);

        fixture = new ScenarioActionServiceImpl(scenarioActionRepositoryMock);
    }

    @Test
    void testSave() {
        when(scenarioActionRepositoryMock.save(scenarioActionWithScenarioExecution)).thenReturn(scenarioActionWithScenarioExecution);

        ScenarioAction savedScenarioAction = fixture.save(scenarioActionWithScenarioExecution);
        assertEquals(scenarioActionWithScenarioExecution, savedScenarioAction);
    }

    @Test
    void testFindAll() {
        Pageable pageable = Pageable.unpaged();
        Page<ScenarioAction> page = new PageImpl<>(List.of(scenarioActionWithScenarioExecution));

        when(scenarioActionRepositoryMock.findAll(pageable)).thenReturn(page);

        Page<ScenarioAction> result = fixture.findAll(pageable);

        assertEquals(page, result);

        verifyDtoPreparations();
    }

    @Test
    void testFindOne() {
        Long scenarioActionId = 1L;

        when(scenarioActionRepositoryMock.findOneWithEagerRelationships(scenarioActionId)).thenReturn(Optional.of(scenarioActionWithScenarioExecution));

        Optional<ScenarioAction> maybeScenarioAction = fixture.findOne(scenarioActionId);

        assertTrue(maybeScenarioAction.isPresent());
        assertEquals(scenarioActionWithScenarioExecution, maybeScenarioAction.get());

        verifyDtoPreparations();
    }

    private void verifyDtoPreparations() {
        ArgumentCaptor<ScenarioExecution> scenarioExecutionArgumentCaptor = ArgumentCaptor.forClass(ScenarioExecution.class);
        verify(scenarioActionWithScenarioExecution).setScenarioExecution(scenarioExecutionArgumentCaptor.capture());

        ScenarioExecution expectedScenarioExecution = scenarioActionWithScenarioExecution.getScenarioExecution();
        ScenarioExecution capturedScenarioExecution = scenarioExecutionArgumentCaptor.getValue();

        assertEquals(expectedScenarioExecution.getScenarioName(), capturedScenarioExecution.getScenarioName());
    }
}
