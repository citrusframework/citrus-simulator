package org.citrusframework.simulator.service;

import jakarta.persistence.criteria.JoinType;
import org.citrusframework.simulator.model.ScenarioExecution_;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.model.ScenarioParameter_;
import org.citrusframework.simulator.repository.ScenarioParameterRepository;
import org.citrusframework.simulator.service.criteria.ScenarioParameterCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for executing complex queries for {@link ScenarioParameter} entities in the database.
 * The main input is a {@link ScenarioParameterCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ScenarioParameter} or a {@link Page} of {@link ScenarioParameter} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ScenarioParameterQueryService extends QueryService<ScenarioParameter> {

    private final Logger log = LoggerFactory.getLogger(ScenarioParameterQueryService.class);

    private final ScenarioParameterRepository scenarioParameterRepository;

    public ScenarioParameterQueryService(ScenarioParameterRepository scenarioParameterRepository) {
        this.scenarioParameterRepository = scenarioParameterRepository;
    }

    /**
     * Return a {@link List} of {@link ScenarioParameter} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ScenarioParameter> findByCriteria(ScenarioParameterCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ScenarioParameter> specification = createSpecification(criteria);
        return scenarioParameterRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link ScenarioParameter} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ScenarioParameter> findByCriteria(ScenarioParameterCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ScenarioParameter> specification = createSpecification(criteria);
        return scenarioParameterRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ScenarioParameterCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ScenarioParameter> specification = createSpecification(criteria);
        return scenarioParameterRepository.count(specification);
    }

    /**
     * Function to convert {@link ScenarioParameterCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ScenarioParameter> createSpecification(ScenarioParameterCriteria criteria) {
        Specification<ScenarioParameter> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getParameterId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getParameterId(), ScenarioParameter_.parameterId));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), ScenarioParameter_.name));
            }
            if (criteria.getControlType() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getControlType(), ScenarioParameter_.controlType));
            }
            if (criteria.getValue() != null) {
                specification = specification.and(buildStringSpecification(criteria.getValue(), ScenarioParameter_.value));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), ScenarioParameter_.createdDate));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), ScenarioParameter_.lastModifiedDate));
            }
            if (criteria.getScenarioExecutionId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getScenarioExecutionId(),
                            root -> root.join(ScenarioParameter_.scenarioExecution, JoinType.LEFT).get(ScenarioExecution_.executionId)
                        )
                    );
            }
        }
        return specification;
    }
}
