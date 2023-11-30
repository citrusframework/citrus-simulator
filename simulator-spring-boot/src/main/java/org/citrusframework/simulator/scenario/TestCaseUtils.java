package org.citrusframework.simulator.scenario;

import static org.citrusframework.simulator.service.impl.DefaultScenarioExecutorServiceImpl.ENTITY_MANAGER_VARIABLE_KEY;

import jakarta.persistence.EntityManager;
import java.util.Map;
import org.citrusframework.TestCase;

public final class TestCaseUtils {

    private TestCaseUtils() {
        // Not intended for instantiation
    }

    public static EntityManager getContainedEntityManagerOrDefault(TestCase testCase, EntityManager defaultEntityManager) {
        return getContainedEntityManagerOrDefault(testCase.getVariableDefinitions(), defaultEntityManager);
    }


    public static EntityManager getContainedEntityManagerOrDefault(Map<String, Object> variableDefinitions, EntityManager defaultEntityManager) {
        if (variableDefinitions.containsKey(ENTITY_MANAGER_VARIABLE_KEY) && variableDefinitions.get(ENTITY_MANAGER_VARIABLE_KEY) instanceof EntityManager containedEntityManager) {
            return containedEntityManager;
        }

        return defaultEntityManager;
    }
}
