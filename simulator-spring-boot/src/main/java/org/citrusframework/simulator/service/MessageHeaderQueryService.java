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

import jakarta.persistence.criteria.JoinType;
import org.citrusframework.simulator.model.MessageHeader;
import org.citrusframework.simulator.model.MessageHeader_;
import org.citrusframework.simulator.model.Message_;
import org.citrusframework.simulator.repository.MessageHeaderRepository;
import org.citrusframework.simulator.service.criteria.MessageHeaderCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for executing complex queries for {@link MessageHeader} entities in the database.
 * The main input is a {@link MessageHeaderCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MessageHeader} or a {@link Page} of {@link MessageHeader} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MessageHeaderQueryService extends QueryService<MessageHeader> {

    private static final Logger logger = LoggerFactory.getLogger(MessageHeaderQueryService.class);

    private final MessageHeaderRepository messageHeaderRepository;

    public MessageHeaderQueryService(MessageHeaderRepository messageHeaderRepository) {
        this.messageHeaderRepository = messageHeaderRepository;
    }

    /**
     * Return a {@link List} of {@link MessageHeader} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MessageHeader> findByCriteria(MessageHeaderCriteria criteria) {
        logger.debug("find by criteria : {}", criteria);
        final Specification<MessageHeader> specification = createSpecification(criteria);
        return messageHeaderRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link MessageHeader} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MessageHeader> findByCriteria(MessageHeaderCriteria criteria, Pageable page) {
        logger.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<MessageHeader> specification = createSpecification(criteria);
        return messageHeaderRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MessageHeaderCriteria criteria) {
        logger.debug("count by criteria : {}", criteria);
        final Specification<MessageHeader> specification = createSpecification(criteria);
        return messageHeaderRepository.count(specification);
    }

    /**
     * Function to convert {@link MessageHeaderCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<MessageHeader> createSpecification(MessageHeaderCriteria criteria) {
        Specification<MessageHeader> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getHeaderId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getHeaderId(), MessageHeader_.headerId));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), MessageHeader_.name));
            }
            if (criteria.getValue() != null) {
                specification = specification.and(buildStringSpecification(criteria.getValue(), MessageHeader_.value));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), MessageHeader_.createdDate));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), MessageHeader_.lastModifiedDate));
            }
            if (criteria.getMessageId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getMessageId(),
                            root -> root.join(MessageHeader_.message, JoinType.LEFT).get(Message_.messageId)
                        )
                    );
            }
        }
        return specification;
    }
}
