package org.citrusframework.simulator.service.impl;

import org.citrusframework.simulator.model.ScenarioAction;
import org.citrusframework.simulator.repository.ScenarioActionRepository;
import org.citrusframework.simulator.service.ScenarioActionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link ScenarioAction}.
 */
@Service
@Transactional
public class ScenarioActionServiceImpl implements ScenarioActionService {

    private final Logger log = LoggerFactory.getLogger(ScenarioActionServiceImpl.class);

    private final ScenarioActionRepository scenarioActionRepository;

    public ScenarioActionServiceImpl(ScenarioActionRepository scenarioActionRepository) {
        this.scenarioActionRepository = scenarioActionRepository;
    }

    @Override
    public ScenarioAction save(ScenarioAction scenarioAction) {
        log.debug("Request to save ScenarioAction : {}", scenarioAction);
        return scenarioActionRepository.save(scenarioAction);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScenarioAction> findAll(Pageable pageable) {
        log.debug("Request to get all ScenarioActions with eager relationships");
        return scenarioActionRepository.findAll(pageable)
            .map(ScenarioActionService::restrictToDtoProperties);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScenarioAction> findOne(Long id) {
        log.debug("Request to get ScenarioAction : {}", id);
        return scenarioActionRepository.findOneWithEagerRelationships(id)
            .map(ScenarioActionService::restrictToDtoProperties);
    }
}
