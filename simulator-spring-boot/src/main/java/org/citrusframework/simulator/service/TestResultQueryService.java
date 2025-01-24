/*
 * Copyright the original author or authors.
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.citrusframework.simulator.model.TestParameter_;
import org.citrusframework.simulator.model.TestResult;
import org.citrusframework.simulator.model.TestResult_;
import org.citrusframework.simulator.repository.TestResultRepository;
import org.citrusframework.simulator.service.criteria.TestResultCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.citrusframework.simulator.service.CriteriaQueryUtils.selectAllIds;

/**
 * Service for executing complex queries for {@link TestResult} entities in the database.
 * The main input is a {@link TestResultCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TestResult} or a {@link Page} of {@link TestResult} which fulfills the criteria.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class TestResultQueryService extends QueryService<TestResult> {

    private final EntityManager entityManager;
    private final TestResultRepository testResultRepository;

    public TestResultQueryService(EntityManager entityManager, TestResultRepository testResultRepository) {
        this.entityManager = entityManager;
        this.testResultRepository = testResultRepository;
    }

    /**
     * Return a {@link Page} of {@link TestResult} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TestResult> findByCriteria(TestResultCriteria criteria, Pageable page) {
        logger.debug("find by criteria : {}, page: {}", criteria, page);

        var specification = createSpecification(criteria);
        var testResultIds = selectAllIds(
            TestResult.class,
            TestResult_.id,
            specification,
            page,
            entityManager
        );

        var testResults = testResultRepository.findAllWhereIdIn(testResultIds, page.getSort());
        return new PageImpl<>(testResults, page, testResultRepository.count(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TestResultCriteria criteria) {
        logger.debug("count by criteria : {}", criteria);
        final Specification<TestResult> specification = createSpecification(criteria);
        return testResultRepository.count(specification);
    }

    /**
     * Function to convert {@link TestResultCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TestResult> createSpecification(TestResultCriteria criteria) {
        Specification<TestResult> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TestResult_.id));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStatus(), TestResult_.status));
            }
            if (criteria.getTestName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTestName(), TestResult_.testName));
            }
            if (criteria.getClassName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getClassName(), TestResult_.className));
            }
            if (criteria.getErrorMessage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getErrorMessage(), TestResult_.errorMessage));
            }
            if (criteria.getStackTrace() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStackTrace(), TestResult_.stackTrace));
            }
            if (criteria.getFailureType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFailureType(), TestResult_.failureType));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), TestResult_.createdDate));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), TestResult_.lastModifiedDate));
            }
            if (criteria.getTestParameterKey() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTestParameterKey(),
                            root -> root.join(TestResult_.testParameters, JoinType.LEFT).get(TestParameter_.testParameterId).get("key")
                        )
                    );
            }
        }
        return specification;
    }
}
