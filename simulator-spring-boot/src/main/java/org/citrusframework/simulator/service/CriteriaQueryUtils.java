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
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.springframework.data.domain.Pageable.unpaged;

final class CriteriaQueryUtils {

    private CriteriaQueryUtils() {
        // Static utility class
    }

    /**
     * There is an issue within Hibernate when trying to apply {@link Pageable} in combination with filtering through
     * a {@link Specification}. Hibernate cannot create a query (although SQL would allow it) that returns the paginated
     * result. We must therefore manually execute two (or rather three) queries:
     * <ul>
     *     <li>The first query to fetch a <b>paginated</b> list of all entity ID's</li>
     *     <li>The second query, to fetch all entities that belong to those ID's</li>
     *     <li>And a third "count"-query, in order to retrieve the missing piece of pagination information</li>
     * </ul>
     * <p>
     * The warning is being seen in the log:
     * <pre>
     * [TIMESTAMP]: HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
     * </pre>
     * <p>
     * The query created and returned by this function is the first of the mentioned queries, constructed with the
     * restrictions from a given {@link Specification}.
     *
     * @see <a href="https://github.com/citrusframework/citrus-simulator/issues/255">problem with in-memory pagination when having high volume of data</a>
     */
    static <E> List<Long> selectAllIds(Class<E> entityClass, SingularAttribute<E, Long> idAttribute, Specification<E> specification, Pageable page, EntityManager entityManager) {
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<E> root = criteriaQuery.from(entityClass);

        criteriaQuery = selectIdField(idAttribute, root, criteriaQuery);

        criteriaQuery = whereSpecificationApplies(specification, root, criteriaQuery, criteriaBuilder);

        criteriaQuery = orderBy(page.getSort(), root, criteriaQuery, criteriaBuilder);

        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);

        if (!unpaged().equals(page)) {
            query = selectPage(page, query);
        }

        return query.getResultList();
    }

    /**
     * Find all entities matching the {@link Specification}.
     */
    static <E> List<E> selectAll(Class<E> entityClass, Specification<E> specification, Pageable page, EntityManager entityManager) {
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<E> root = criteriaQuery.from(entityClass);

        criteriaQuery = whereSpecificationApplies(specification, root, criteriaQuery, criteriaBuilder);

        criteriaQuery = orderBy(page.getSort(), root, criteriaQuery, criteriaBuilder);

        TypedQuery<E> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList();
    }

    /**
     * Restrict the query to the ID of the entity.
     *
     * @return the modified {@link CriteriaQuery<Long>}.
     */
    private static <R> CriteriaQuery<Long> selectIdField(SingularAttribute<R, Long> idAttribute, Root<R> root, CriteriaQuery<Long> criteriaQuery) {
        return criteriaQuery.select(root.get(idAttribute));
    }

    /**
     * Apply the specifications (criteria) to the query, if any restrictions exist.
     *
     * @return the modified {@link CriteriaQuery<Long>}.
     */
    private static <E, R> CriteriaQuery<R> whereSpecificationApplies(Specification<E> specification, Root<E> root, CriteriaQuery<R> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        var predicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder);
        if (nonNull(predicate)) {
            return criteriaQuery.where(predicate);
        }
        return criteriaQuery;
    }

    /**
     * Handle sorting, according to the definition of the {@link Pageable}.
     *
     * @return the modified {@link CriteriaQuery<Long>}.
     */
    private static <E,R> CriteriaQuery<E> orderBy(Sort sort, Root<R> root, CriteriaQuery<E> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        var orders = new ArrayList<Order>();
        for (var sortOrder : sort) {
            var path = root.get(sortOrder.getProperty());
            orders.add(sortOrder.isAscending() ? criteriaBuilder.asc(path) : criteriaBuilder.desc(path));
        }
        if (!orders.isEmpty()) {
            return criteriaQuery.orderBy(orders);
        }

        return criteriaQuery;
    }

    /**
     * Apply pagination.
     *
     * @return The same {@link TypedQuery<Long>}.
     */
    private static <E> TypedQuery<E> selectPage(Pageable page, TypedQuery<E> query) {
        // Calculate the first result index based on the page number and page size
        var pageSize = page.getPageSize();
        var firstResult = page.getPageNumber() * pageSize;

        query = query.setFirstResult(firstResult);
        query = query.setMaxResults(pageSize);

        return query;
    }
}
