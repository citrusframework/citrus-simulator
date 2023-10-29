package org.citrusframework.simulator.service.impl;

import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.repository.ScenarioParameterRepository;
import org.citrusframework.simulator.service.ScenarioParameterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link ScenarioParameter}.
 */
@Service
@Transactional
public class ScenarioParameterServiceImpl implements ScenarioParameterService {

    private final Logger log = LoggerFactory.getLogger(ScenarioParameterServiceImpl.class);

    private final ScenarioParameterRepository scenarioParameterRepository;

    public ScenarioParameterServiceImpl(ScenarioParameterRepository scenarioParameterRepository) {
        this.scenarioParameterRepository = scenarioParameterRepository;
    }

    @Override
    public ScenarioParameter save(ScenarioParameter scenarioParameter) {
        log.debug("Request to save ScenarioParameter : {}", scenarioParameter);
        return scenarioParameterRepository.save(scenarioParameter);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScenarioParameter> findAll(Pageable pageable) {
        log.debug("Request to get all ScenarioParameters with eager relationships");
        return scenarioParameterRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScenarioParameter> findOne(Long id) {
        log.debug("Request to get ScenarioParameter : {}", id);
        return scenarioParameterRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ScenarioParameter : {}", id);
        scenarioParameterRepository.deleteById(id);
    }
}
