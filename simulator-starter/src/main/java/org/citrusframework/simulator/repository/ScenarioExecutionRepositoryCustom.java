package org.citrusframework.simulator.repository;

import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioExecutionFilter;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScenarioExecutionRepositoryCustom {
    
    /**
     * Find all {@link ScenarioExecution} that correspond to the given filter
     * 
     * @param filter
     * @return
     */
    List<ScenarioExecution> find(@Param("filter") ScenarioExecutionFilter filter);
}
