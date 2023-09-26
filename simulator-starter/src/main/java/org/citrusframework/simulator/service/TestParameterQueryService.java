package org.citrusframework.simulator.service;

import jakarta.persistence.criteria.JoinType;
import org.citrusframework.simulator.model.TestParameter;
import org.citrusframework.simulator.model.TestParameter_;
import org.citrusframework.simulator.model.TestResult_;
import org.citrusframework.simulator.repository.TestParameterRepository;
import org.citrusframework.simulator.service.criteria.TestParameterCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for executing complex queries for {@link TestParameter} entities in the database.
 * The main input is a {@link TestParameterCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TestParameter} or a {@link Page} of {@link TestParameter} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TestParameterQueryService extends QueryService<TestParameter> {

    private final Logger log = LoggerFactory.getLogger(TestParameterQueryService.class);

    private final TestParameterRepository testParameterRepository;

    public TestParameterQueryService(TestParameterRepository testParameterRepository) {
        this.testParameterRepository = testParameterRepository;
    }

    /**
     * Return a {@link List} of {@link TestParameter} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TestParameter> findByCriteria(TestParameterCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TestParameter> specification = createSpecification(criteria);
        return testParameterRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link TestParameter} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TestParameter> findByCriteria(TestParameterCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TestParameter> specification = createSpecification(criteria);
        return testParameterRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TestParameterCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TestParameter> specification = createSpecification(criteria);
        return testParameterRepository.count(specification);
    }

    /**
     * Function to convert {@link TestParameterCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TestParameter> createSpecification(TestParameterCriteria criteria) {
        Specification<TestParameter> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getKey() != null) {
                specification = specification.and(buildSpecification(criteria.getKey(), root -> root.get(TestParameter_.testParameterId).get("key")));
            }
            if (criteria.getValue() != null) {
                specification = specification.and(buildStringSpecification(criteria.getValue(), TestParameter_.value));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), TestParameter_.createdDate));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), TestParameter_.lastModifiedDate));
            }
            if (criteria.getTestResultId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTestResultId(),
                            root -> root.join(TestParameter_.testResult, JoinType.LEFT).get(TestResult_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
