package org.citrusframework.simulator.scenario;

import static org.citrusframework.simulator.service.impl.DefaultScenarioExecutorServiceImpl.ENTITY_MANAGER_VARIABLE_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import jakarta.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import org.citrusframework.TestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class TestCaseUtilsTest {

    @Mock
    private EntityManager defaultEntityManager;

    @Mock
    private EntityManager customEntityManager;

    @Mock
    private TestCase testCase;

    @Test
    void testGetContainedEntityManagerOrDefault_WithCustomEntityManager() {
        Map<String, Object> variableDefinitions = new HashMap<>();
        variableDefinitions.put(ENTITY_MANAGER_VARIABLE_KEY, customEntityManager);

        doReturn(variableDefinitions).when(testCase).getVariableDefinitions();

        EntityManager result = TestCaseUtils.getContainedEntityManagerOrDefault(testCase, defaultEntityManager);
        assertEquals(customEntityManager, result, "Expected custom EntityManager to be returned");
    }

    @Test
    void testGetContainedEntityManagerOrDefault_WithDefaultEntityManager() {
        doReturn(new HashMap<>()).when(testCase).getVariableDefinitions();

        EntityManager result = TestCaseUtils.getContainedEntityManagerOrDefault(testCase, defaultEntityManager);
        assertEquals(defaultEntityManager, result, "Expected default EntityManager to be returned");
    }

    @Test
    void testGetContainedEntityManagerOrDefault_WithInvalidType() {
        Map<String, Object> variableDefinitions = new HashMap<>();
        variableDefinitions.put(ENTITY_MANAGER_VARIABLE_KEY, new Object()); // Invalid type

        doReturn(variableDefinitions).when(testCase).getVariableDefinitions();

        EntityManager result = TestCaseUtils.getContainedEntityManagerOrDefault(testCase, defaultEntityManager);
        assertEquals(defaultEntityManager, result, "Expected default EntityManager to be returned when invalid type is present");
    }
}
