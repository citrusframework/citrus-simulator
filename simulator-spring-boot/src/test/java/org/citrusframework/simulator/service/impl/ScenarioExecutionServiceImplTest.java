package org.citrusframework.simulator.service.impl;

import jakarta.persistence.EntityManager;
import org.citrusframework.TestCase;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.repository.ScenarioExecutionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.citrusframework.simulator.model.ScenarioExecution.EXECUTION_ID;
import static org.citrusframework.simulator.service.impl.DefaultScenarioExecutorServiceImpl.ENTITY_MANAGER_VARIABLE_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class ScenarioExecutionServiceImplTest {

    @Mock
    private ScenarioExecutionRepository scenarioExecutionRepositoryMock;

    @Mock
    private TimeProvider timeProviderMock;

    private ScenarioExecution sampleScenarioExecution;

    private ScenarioExecutionServiceImpl fixture;

    @BeforeEach
    void beforeEachSetup() {
        sampleScenarioExecution = new ScenarioExecution();

        fixture = new ScenarioExecutionServiceImpl(null, scenarioExecutionRepositoryMock);
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

        doReturn(Optional.of(sampleScenarioExecution)).when(scenarioExecutionRepositoryMock).findOneByExecutionId(scenarioExecutionId);

        Optional<ScenarioExecution> maybeScenarioExecution = fixture.findOne(scenarioExecutionId);

        assertTrue(maybeScenarioExecution.isPresent());
        assertEquals(sampleScenarioExecution, maybeScenarioExecution.get());
    }

    @Test
    void findOneLazy() {
        Long scenarioExecutionId = 1L;

        doReturn(Optional.of(sampleScenarioExecution)).when(scenarioExecutionRepositoryMock).findById(scenarioExecutionId);

        Optional<ScenarioExecution> maybeScenarioExecution = fixture.findOneLazy(scenarioExecutionId);

        assertTrue(maybeScenarioExecution.isPresent());
        assertEquals(sampleScenarioExecution, maybeScenarioExecution.get());
    }

    @Test
    void createAndSaveExecutionScenario() {
        String scenarioName = "sampleScenario";

        Instant now = Instant.now();
        doReturn(now).when(timeProviderMock).getTimeNow();

        long executionId = 1234L;

        EntityManager entityManagerMock = mock(EntityManager.class);
        doAnswer(invocationOnMock -> {
            ScenarioExecution scenarioExecution = invocationOnMock.getArgument(0, ScenarioExecution.class);
            setField(scenarioExecution, "executionId", executionId, Long.class);
            return null;
        }).when(entityManagerMock).persist(any(ScenarioExecution.class));

        ScenarioParameter scenarioParameter = new ScenarioParameter();
        List<ScenarioParameter> scenarioParameters = List.of(scenarioParameter);

        ScenarioExecution result = fixture.createAndSaveExecutionScenario(scenarioName, scenarioParameters, entityManagerMock);

        assertEquals(executionId, result.getExecutionId());
        assertEquals(scenarioName, result.getScenarioName());
        assertEquals(now, result.getStartDate());
        assertEquals(ScenarioExecution.Status.RUNNING, result.getStatus());
        assertThat(result.getScenarioParameters())
            .hasSize(1)
            .containsExactly(scenarioParameter);
    }

    @Nested
    class CompleteScenarioExecution {

        private final Long scenarioExecutionId = 1234L;
        private final Instant now = Instant.now();

        @Mock
        private EntityManager entityManagerMock;

        @Mock
        private TestCase testCaseMock;

        @BeforeEach
        void beforeEachSetup() {
            setField(sampleScenarioExecution, "executionId", scenarioExecutionId);

            Map<String, Object> variableDefinitions = Map.of(
                ENTITY_MANAGER_VARIABLE_KEY, entityManagerMock,
                EXECUTION_ID, String.valueOf(scenarioExecutionId)
            );
            doReturn(variableDefinitions).when(testCaseMock).getVariableDefinitions();
        }

        @Test
        void successful() {
            preparePersistenceLayerMocks();

            ScenarioExecution result = fixture.completeScenarioExecutionSuccess(testCaseMock);

            assertEquals(ScenarioExecution.Status.SUCCESS, result.getStatus());
            assertEquals(now, result.getEndDate());
            assertNull(result.getErrorMessage());

            verifyNoInteractions(scenarioExecutionRepositoryMock);
        }

        @Test
        void successfulWithNoScenarioExecutionFound() {
            String testName = "testCase";
            doReturn(testName).when(testCaseMock).getName();

            doReturn(null).when(entityManagerMock).find(ScenarioExecution.class, scenarioExecutionId);

            CitrusRuntimeException exception = assertThrows(CitrusRuntimeException.class, () -> fixture.completeScenarioExecutionSuccess(testCaseMock));
            assertEquals(String.format("Failed to find corresponding ScenarioExecution by executionId %s for test %s", scenarioExecutionId, testName), exception.getMessage());
        }

        @Test
        void failed() {
            preparePersistenceLayerMocks();

            Throwable cause = new RuntimeException("Failure cause");
            ScenarioExecution result = fixture.completeScenarioExecutionFailure(testCaseMock, cause);

            assertEquals(ScenarioExecution.Status.FAILED, result.getStatus());
            assertEquals(now, result.getEndDate());
            assertTrue(result.getErrorMessage().startsWith("java.lang.RuntimeException: Failure cause"), "Error message must contain cause!");

            verifyNoInteractions(scenarioExecutionRepositoryMock);
        }

        @Test
        void failedWithNoScenarioExecutionFound() {
            String testName = "testCase";
            doReturn(testName).when(testCaseMock).getName();

            doReturn(null).when(entityManagerMock).find(ScenarioExecution.class, scenarioExecutionId);

            CitrusRuntimeException exception = assertThrows(CitrusRuntimeException.class, () -> fixture.completeScenarioExecutionFailure(testCaseMock, new RuntimeException("Failure cause")));
            assertEquals(String.format("Failed to find corresponding ScenarioExecution by executionId %s for test %s", scenarioExecutionId, testName), exception.getMessage());

            verifyNoInteractions(scenarioExecutionRepositoryMock);
        }

        @AfterEach
        void afterEachTeardown() {
            verifyNoInteractions(scenarioExecutionRepositoryMock);
        }

        private void preparePersistenceLayerMocks() {
            doReturn(sampleScenarioExecution).when(entityManagerMock).find(ScenarioExecution.class, scenarioExecutionId);
            doReturn(now).when(timeProviderMock).getTimeNow();
        }
    }
}
