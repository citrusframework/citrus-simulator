/*
 * Copyright 2023-2024 the original author or authors.
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

import static org.citrusframework.simulator.service.CriteriaQueryUtils.newSelectIdBySpecificationQuery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.citrusframework.simulator.model.Message_;
import org.citrusframework.simulator.model.ScenarioAction_;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioExecution_;
import org.citrusframework.simulator.model.ScenarioParameter_;
import org.citrusframework.simulator.repository.ScenarioExecutionRepository;
import org.citrusframework.simulator.service.criteria.ScenarioExecutionCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for executing complex queries for {@link ScenarioExecution} entities in the database.
 * The main input is a {@link ScenarioExecutionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ScenarioExecution} or a {@link Page} of {@link ScenarioExecution} which fulfills the criteria.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ScenarioExecutionQueryService extends QueryService<ScenarioExecution> {

    private final EntityManager entityManager;
    private final ScenarioExecutionRepository scenarioExecutionRepository;

    public ScenarioExecutionQueryService(EntityManager entityManager, ScenarioExecutionRepository scenarioExecutionRepository) {
        this.entityManager = entityManager;
        this.scenarioExecutionRepository = scenarioExecutionRepository;
    }

    /**
     * Return a {@link List} of {@link ScenarioExecution} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ScenarioExecution> findByCriteria(ScenarioExecutionCriteria criteria) {
        logger.debug("find by criteria : {}", criteria);
        final Specification<ScenarioExecution> specification = createSpecification(criteria);
        return scenarioExecutionRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link ScenarioExecution} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ScenarioExecution> findByCriteria(ScenarioExecutionCriteria criteria, Pageable page) {
        logger.debug("find by criteria : {}, page: {}", criteria, page);

        var specification = createSpecification(criteria);
        var scenarioExecutionIds = newSelectIdBySpecificationQuery(
            ScenarioExecution.class,
            ScenarioExecution_.executionId,
            specification,
            page,
            entityManager
        )
            .getResultList();

        var scenarioExecutions = scenarioExecutionRepository.findAllWhereIdIn(scenarioExecutionIds, page);
        return new PageImpl<>(scenarioExecutions.getContent(), page, scenarioExecutionRepository.count(specification));
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ScenarioExecutionCriteria criteria) {
        logger.debug("count by criteria : {}", criteria);
        final Specification<ScenarioExecution> specification = createSpecification(criteria);
        return scenarioExecutionRepository.count(specification);
    }

    /**
     * Function to convert {@link ScenarioExecutionCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ScenarioExecution> createSpecification(ScenarioExecutionCriteria criteria) {
        Specification<ScenarioExecution> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getExecutionId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getExecutionId(), ScenarioExecution_.executionId));
            }
            if (criteria.getStartDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartDate(), ScenarioExecution_.startDate));
            }
            if (criteria.getEndDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndDate(), ScenarioExecution_.endDate));
            }
            if (criteria.getScenarioName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getScenarioName(), ScenarioExecution_.scenarioName));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStatus(), ScenarioExecution_.status));
            }
            if (criteria.getErrorMessage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getErrorMessage(), ScenarioExecution_.errorMessage));
            }
            if (criteria.getScenarioActionsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getScenarioActionsId(),
                            root -> root.join(ScenarioExecution_.scenarioActions, JoinType.LEFT).get(ScenarioAction_.actionId)
                        )
                    );
            }
            if (criteria.getScenarioMessagesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getScenarioMessagesId(),
                            root -> root.join(ScenarioExecution_.scenarioMessages, JoinType.LEFT).get(Message_.messageId)
                        )
                    );
            }
            if (criteria.getScenarioParametersId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getScenarioParametersId(),
                            root -> root.join(ScenarioExecution_.scenarioParameters, JoinType.LEFT).get(ScenarioParameter_.parameterId)
                        )
                    );
            }
        }
        return specification;
    }
}
