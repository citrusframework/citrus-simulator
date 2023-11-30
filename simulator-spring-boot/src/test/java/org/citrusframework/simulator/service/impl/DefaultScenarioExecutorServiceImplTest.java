package org.citrusframework.simulator.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.apache.commons.lang3.tuple.Triple;
import org.citrusframework.Citrus;
import org.citrusframework.CitrusContext;
import org.citrusframework.TestCase;
import org.citrusframework.context.TestContext;
import org.citrusframework.report.TestListeners;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class DefaultScenarioExecutorServiceImplTest {

    private static ScenarioEndpoint scenarioEndpointMock;
    private static AtomicBoolean customScenarioExecuted;

    @Mock
    private ApplicationContext applicationContextMock;

    @Mock
    private Citrus citrusMock;

    @Mock
    private EntityManagerFactory entityManagerFactoryMock;

    @Mock
    private ScenarioExecutionService scenarioExecutionServiceMock;

    @Mock
    private SimulatorConfigurationProperties propertiesMock;

    @Mock
    private ExecutorService executorServiceMock;

    private DefaultScenarioExecutorServiceImpl fixture;

    private String scenarioName = "testScenario";
    private List<ScenarioParameter> parameters = List.of(
        ScenarioParameter.builder()
            .name("param1")
            .value("value1")
            .build(),
        ScenarioParameter.builder()
            .name("param2")
            .value("value2")
            .build()
    );

    @BeforeEach
    public void beforeEachSetup() {
        doReturn(1).when(propertiesMock).getExecutorThreads();

        fixture = new DefaultScenarioExecutorServiceImpl(applicationContextMock, citrusMock, entityManagerFactoryMock, scenarioExecutionServiceMock, propertiesMock);
        setField(fixture, "executorService", executorServiceMock, ExecutorService.class);

        scenarioEndpointMock = mock(ScenarioEndpoint.class);
        customScenarioExecuted = new AtomicBoolean(false);
    }

    @Nested
    class RunScenario {

        static Stream<Long> withPessimisticLock() {
            return Stream.of(
                0L,
                1L
            );
        }

        @Nested
        class ByName {

            static Stream<Long> withPessimisticLock() {
                return RunScenario.withPessimisticLock();
            }

            @Test
            void withOptimisticLock() {
                preparePessimisticLockTimeout(-1L);

                Triple<SimulatorScenario, EntityManager, EntityTransaction> result = submitSimulatorScenarioByName();

                verify(result.getMiddle(), never()).flush();
                verify(result.getMiddle(), never()).lock(any(ScenarioExecution.class), eq(PESSIMISTIC_WRITE));

                runSimulatorScenario(result);
            }

            @MethodSource
            @ParameterizedTest
            void withPessimisticLock(Long pessimisticLockTimeout) {
                preparePessimisticLockTimeout(pessimisticLockTimeout);

                Triple<SimulatorScenario, EntityManager, EntityTransaction> result = submitSimulatorScenarioByName();

                verify(result.getMiddle()).flush();
                verify(result.getMiddle()).lock(any(ScenarioExecution.class), eq(PESSIMISTIC_WRITE));

                runSimulatorScenario(result);
            }

            private Triple<SimulatorScenario, EntityManager, EntityTransaction> submitSimulatorScenarioByName() {
                SimulatorScenario simulatorScenarioMock = mock(SimulatorScenario.class);
                doReturn(simulatorScenarioMock).when(applicationContextMock).getBean(scenarioName, SimulatorScenario.class);

                EntityManager entityManagerMock = mock(EntityManager.class);
                doReturn(entityManagerMock).when(entityManagerFactoryMock).createEntityManager();
                EntityTransaction transactionMock = mock(EntityTransaction.class);
                doReturn(transactionMock).when(entityManagerMock).getTransaction();

                Long executionId = mockScenarioExecutionCreation(entityManagerMock);

                // Note that this does not actually "run" the scenario (because of the mocked executor service), it just creates it
                Long result = fixture.run(scenarioName, parameters);
                assertEquals(executionId, result);

                // Transaction should have been started
                verify(transactionMock).begin();
                verify(scenarioExecutionServiceMock).createAndSaveExecutionScenario(scenarioName, parameters, entityManagerMock);

                return Triple.of(simulatorScenarioMock, entityManagerMock, transactionMock);
            }

            private void runSimulatorScenario(Triple<SimulatorScenario, EntityManager, EntityTransaction> arguments) {
                ArgumentCaptor<Runnable> scenarioRunnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
                verify(executorServiceMock).execute(scenarioRunnableArgumentCaptor.capture());

                // Now, we need more mocks!
                mockCitrusTestContext();

                // This invokes the scenario execution with the captured runnable
                scenarioRunnableArgumentCaptor.getValue().run();

                verifyNoInteractions(arguments.getLeft());

                // Transaction has been committed
                verify(arguments.getRight()).commit();
            }
        }

        @Nested
        class ByBean {

            static Stream<Long> withPessimisticLock() {
                return RunScenario.withPessimisticLock();
            }

            @Test
            void withOptimisticLock() {
                preparePessimisticLockTimeout(-1L);

                Object[] result = submitSimulatorScenarioBean();

                EntityManager entityManagerMock = (EntityManager) result[0];
                verify(entityManagerMock, never()).flush();
                verify(entityManagerMock, never()).lock(any(ScenarioExecution.class), eq(PESSIMISTIC_WRITE));

                runSimulatorScenario(Triple.of((SimulatorScenario) result[3], (Long) result[2], (EntityTransaction) result[1]));
            }

            @MethodSource
            @ParameterizedTest
            void withPessimisticLock(Long pessimisticLockTimeout) {
                preparePessimisticLockTimeout(pessimisticLockTimeout);

                Object[] result = submitSimulatorScenarioBean();

                EntityManager entityManagerMock = (EntityManager) result[0];
                verify(entityManagerMock).flush();
                verify(entityManagerMock).lock(any(ScenarioExecution.class), eq(PESSIMISTIC_WRITE));

                runSimulatorScenario(Triple.of((SimulatorScenario) result[3], (Long) result[2], (EntityTransaction) result[1]));
            }

            private Object[] submitSimulatorScenarioBean() {
                EntityManager entityManagerMock = mock(EntityManager.class);
                doReturn(entityManagerMock).when(entityManagerFactoryMock).createEntityManager();
                EntityTransaction transactionMock = mock(EntityTransaction.class);
                doReturn(transactionMock).when(entityManagerMock).getTransaction();

                Long executionId = mockScenarioExecutionCreation(entityManagerMock);

                SimulatorScenario simulatorScenario = spy(new CustomSimulatorScenario());

                // Note that this does not actually "run" the scenario (because of the mocked executor service), it just creates it
                Long result = fixture.run(simulatorScenario, scenarioName, parameters);
                assertEquals(executionId, result);

                // Transaction should have been started
                verify(transactionMock).begin();
                verify(scenarioExecutionServiceMock).createAndSaveExecutionScenario(scenarioName, parameters, entityManagerMock);

                return new Object[]{entityManagerMock, transactionMock, executionId, simulatorScenario};
            }

            private void runSimulatorScenario(Triple<SimulatorScenario, Long, EntityTransaction> arguments) {
                ArgumentCaptor<Runnable> scenarioRunnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
                verify(executorServiceMock).execute(scenarioRunnableArgumentCaptor.capture());

                // Now, we need more mocks!
                TestContext testContextMock = mockCitrusTestContext();
                TestListeners testListenersMock = mock(TestListeners.class);
                doReturn(testListenersMock).when(testContextMock).getTestListeners();

                // This invokes the scenario execution with the captured runnable
                scenarioRunnableArgumentCaptor.getValue().run();

                ArgumentCaptor<ScenarioRunner> scenarioRunnerArgumentCaptor = ArgumentCaptor.forClass(ScenarioRunner.class);
                verify(arguments.getLeft()).run(scenarioRunnerArgumentCaptor.capture());

                ScenarioRunner scenarioRunner = scenarioRunnerArgumentCaptor.getValue();
                assertEquals(scenarioEndpointMock, scenarioRunner.scenarioEndpoint());
                assertEquals(arguments.getMiddle(), scenarioRunner.getTestCaseRunner().getTestCase().getVariableDefinitions().get(ScenarioExecution.EXECUTION_ID));

                verify(testListenersMock).onTestStart(any(TestCase.class));
                verify(testListenersMock).onTestSuccess(any(TestCase.class));
                verify(testListenersMock).onTestFinish(any(TestCase.class));
                verifyNoMoreInteractions(testListenersMock);

                assertTrue(customScenarioExecuted.get());

                // Transaction has been committed
                verify(arguments.getRight()).commit();
            }
        }

        private void preparePessimisticLockTimeout(Long pessimisticLockTimeout) {
            doReturn(pessimisticLockTimeout).when(propertiesMock).getPessimisticLockTimeout();
            // Re-Init, so that properties will be read again
            fixture = new DefaultScenarioExecutorServiceImpl(applicationContextMock, citrusMock, entityManagerFactoryMock, scenarioExecutionServiceMock, propertiesMock);
            setField(fixture, "executorService", executorServiceMock, ExecutorService.class);
        }
    }

    @Test
    void shutdownExecutorOnDestroy() throws Exception {
        fixture.destroy();
        verify(executorServiceMock).shutdownNow();
    }

    @Test
    void shutdownExecutorOnApplicationContextEvent() {
        fixture.onApplicationEvent(new ContextClosedEvent(applicationContextMock));
        verify(executorServiceMock).shutdownNow();
    }

    private Long mockScenarioExecutionCreation(EntityManager entityManager) {
        Long executionId = 1L;
        ScenarioExecution scenarioExecutionMock = mock(ScenarioExecution.class);
        doReturn(executionId).when(scenarioExecutionMock).getExecutionId();
        doReturn(scenarioExecutionMock).when(scenarioExecutionServiceMock).createAndSaveExecutionScenario(scenarioName, parameters, entityManager);
        return executionId;
    }

    private TestContext mockCitrusTestContext() {
        CitrusContext citrusContextMock = mock(CitrusContext.class);
        doReturn(citrusContextMock).when(citrusMock).getCitrusContext();
        TestContext testContextMock = mock(TestContext.class);
        doReturn(testContextMock).when(citrusContextMock).createTestContext();
        return testContextMock;
    }

    public static class CustomSimulatorScenario implements SimulatorScenario {

        @Override
        public ScenarioEndpoint getScenarioEndpoint() {
            return scenarioEndpointMock;
        }

        @Override
        public void run(ScenarioRunner runner) {
            customScenarioExecuted.set(true);
        }
    }
}
