/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    private static final Logger logger = LoggerFactory.getLogger(ScenarioActionServiceImpl.class);

    private final ScenarioActionRepository scenarioActionRepository;

    public ScenarioActionServiceImpl(ScenarioActionRepository scenarioActionRepository) {
        this.scenarioActionRepository = scenarioActionRepository;
    }

    @Override
    public ScenarioAction save(ScenarioAction scenarioAction) {
        logger.debug("Request to save ScenarioAction : {}", scenarioAction);
        return scenarioActionRepository.save(scenarioAction);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScenarioAction> findAll(Pageable pageable) {
        logger.debug("Request to get all ScenarioActions with eager relationships");
        return scenarioActionRepository.findAll(pageable)
            .map(ScenarioActionService::restrictToDtoProperties);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScenarioAction> findOne(Long id) {
        logger.debug("Request to get ScenarioAction : {}", id);
        return scenarioActionRepository.findOneWithEagerRelationships(id)
            .map(ScenarioActionService::restrictToDtoProperties);
    }
}
