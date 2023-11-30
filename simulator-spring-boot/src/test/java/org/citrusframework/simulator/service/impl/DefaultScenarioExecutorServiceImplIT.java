package org.citrusframework.simulator.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.RollbackException;
import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.ExecutorService;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@IntegrationTest
@ExtendWith({MockitoExtension.class})
@TestPropertySource({"classpath:META-INF/citrus-simulator.properties"})
@TestPropertySource(properties = {"citrus.simulator.pessimistic-lock-timeout=0"})
class DefaultScenarioExecutorServiceImplIT {

    @Mock
    private ExecutorService executorServiceMock;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private DefaultScenarioExecutorServiceImpl fixture;

    @BeforeEach
    void beforeEachSetup() {
        setField(fixture, "executorService", executorServiceMock, ExecutorService.class);
    }

    @Test
    void transactionBoundariesAndLockOnAsyncScenarioExecution() {
        Long scenarioExecutionId = fixture.run(mock(SimulatorScenario.class), "ScenarioName", emptyList());
        assertNotNull(scenarioExecutionId);

        // At this point, the ScenarioExecution should be persisted, but locked!
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            // assertThrows(PessimisticLockException.class, () -> entityManager.createNativeQuery("SELECT count(execution_id) FROM scenario_execution WHERE execution_id=:" + ScenarioExecution_.EXECUTION_ID).setParameter(ScenarioExecution_.EXECUTION_ID, scenarioExecutionId).getFirstResult());
            assertThrows(PessimisticLockException.class, () -> entityManager.createNativeQuery("DROP TABLE scenario_execution CASCADE").executeUpdate());
            assertThrows(RollbackException.class, () -> entityManager.getTransaction().commit());
        }

        ArgumentCaptor<Runnable> scenarioRunnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(executorServiceMock).execute(scenarioRunnableArgumentCaptor.capture());

        // This invokes the scenario execution with the captured runnable, the lock will be freed soon!
        scenarioRunnableArgumentCaptor.getValue().run();

        // Transaction has been committed, table no longer locked
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            // assertThrows(PessimisticLockException.class, () -> entityManager.createNativeQuery("SELECT count(execution_id) FROM scenario_execution WHERE execution_id=:" + ScenarioExecution_.EXECUTION_ID).setParameter(ScenarioExecution_.EXECUTION_ID, scenarioExecutionId).getFirstResult());
            assertDoesNotThrow(() -> entityManager.createNativeQuery("DROP TABLE scenario_execution CASCADE").executeUpdate());
            entityManager.getTransaction().commit();
        }
    }
}
