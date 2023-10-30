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

package org.citrusframework.simulator.service;

import jakarta.persistence.criteria.JoinType;
import org.citrusframework.simulator.model.ScenarioAction;
import org.citrusframework.simulator.model.ScenarioAction_;
import org.citrusframework.simulator.model.ScenarioExecution_;
import org.citrusframework.simulator.repository.ScenarioActionRepository;
import org.citrusframework.simulator.service.criteria.ScenarioActionCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for executing complex queries for {@link ScenarioAction} entities in the database.
 * The main input is a {@link ScenarioActionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ScenarioAction} or a {@link Page} of {@link ScenarioAction} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ScenarioActionQueryService extends QueryService<ScenarioAction> {

    private final Logger log = LoggerFactory.getLogger(ScenarioActionQueryService.class);

    private final ScenarioActionRepository scenarioActionRepository;

    public ScenarioActionQueryService(ScenarioActionRepository scenarioActionRepository) {
        this.scenarioActionRepository = scenarioActionRepository;
    }

    /**
     * Return a {@link List} of {@link ScenarioAction} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ScenarioAction> findByCriteria(ScenarioActionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ScenarioAction> specification = createSpecification(criteria);
        return scenarioActionRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link ScenarioAction} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ScenarioAction> findByCriteria(ScenarioActionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ScenarioAction> specification = createSpecification(criteria);
        return scenarioActionRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ScenarioActionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ScenarioAction> specification = createSpecification(criteria);
        return scenarioActionRepository.count(specification);
    }

    /**
     * Function to convert {@link ScenarioActionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ScenarioAction> createSpecification(ScenarioActionCriteria criteria) {
        Specification<ScenarioAction> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getActionId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getActionId(), ScenarioAction_.actionId));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), ScenarioAction_.name));
            }
            if (criteria.getStartDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartDate(), ScenarioAction_.startDate));
            }
            if (criteria.getEndDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndDate(), ScenarioAction_.endDate));
            }
            if (criteria.getScenarioExecutionId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getScenarioExecutionId(),
                            root -> root.join(ScenarioAction_.scenarioExecution, JoinType.LEFT).get(ScenarioExecution_.executionId)
                        )
                    );
            }
        }
        return specification;
    }
}
