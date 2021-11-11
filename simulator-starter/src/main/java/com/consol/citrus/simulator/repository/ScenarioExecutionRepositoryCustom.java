package com.consol.citrus.simulator.repository;

import com.consol.citrus.simulator.model.ScenarioExecution;
import com.consol.citrus.simulator.model.ScenarioExecutionFilter;
import java.util.List;

public interface ScenarioExecutionRepositoryCustom {
    
    /**
     * Find all {@link ScenarioExecution} that correspond to the given filter
     * 
     * @param filter
     * @return
     */
    List<ScenarioExecution> find(ScenarioExecutionFilter filter);
}
