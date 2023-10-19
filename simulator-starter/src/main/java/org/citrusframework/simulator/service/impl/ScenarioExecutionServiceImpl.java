package org.citrusframework.simulator.service.impl;

import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.repository.ScenarioExecutionRepository;
import org.citrusframework.simulator.service.ScenarioExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link ScenarioExecution}.
 */
@Service
@Transactional
public class ScenarioExecutionServiceImpl implements ScenarioExecutionService {

    private final Logger log = LoggerFactory.getLogger(ScenarioExecutionServiceImpl.class);

    private final ScenarioExecutionRepository scenarioExecutionRepository;

    public ScenarioExecutionServiceImpl(ScenarioExecutionRepository scenarioExecutionRepository) {
        this.scenarioExecutionRepository = scenarioExecutionRepository;
    }

    @Override
    public ScenarioExecution save(ScenarioExecution scenarioExecution) {
        log.debug("Request to save ScenarioExecution : {}", scenarioExecution);
        return scenarioExecutionRepository.save(scenarioExecution);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScenarioExecution> findAll(Pageable pageable) {
        log.debug("Request to get all ScenarioExecutions");
        return scenarioExecutionRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScenarioExecution> findOne(Long id) {
        log.debug("Request to get ScenarioExecution : {}", id);
        return scenarioExecutionRepository.findById(id);
    }
}
