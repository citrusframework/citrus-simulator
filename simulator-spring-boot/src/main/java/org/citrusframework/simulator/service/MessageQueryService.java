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
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.MessageHeader_;
import org.citrusframework.simulator.model.Message_;
import org.citrusframework.simulator.model.ScenarioExecution_;
import org.citrusframework.simulator.repository.MessageRepository;
import org.citrusframework.simulator.service.criteria.MessageCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.citrusframework.simulator.service.CriteriaQueryUtils.selectAllIds;

/**
 * Service for executing complex queries for {@link Message} entities in the database.
 * The main input is a {@link MessageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Message} or a {@link Page} of {@link Message} which fulfills the criteria.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class MessageQueryService extends QueryService<Message> {

    private final EntityManager entityManager;
    private final MessageRepository messageRepository;

    public MessageQueryService(EntityManager entityManager, MessageRepository messageRepository) {
        this.entityManager = entityManager;
        this.messageRepository = messageRepository;
    }

    /**
     * Return a {@link Page} of {@link Message} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Message> findByCriteria(MessageCriteria criteria, Pageable page) {
        logger.debug("find by criteria : {}, page: {}", criteria, page);

        var specification = createSpecification(criteria);
        var messageIds = selectAllIds(
            Message.class,
            Message_.messageId,
            specification,
            page,
            entityManager
        );

        var messages = messageRepository.findAllWhereMessageIdIn(messageIds, page.getSort());
        return new PageImpl<>(messages, page, messageRepository.count(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MessageCriteria criteria) {
        logger.debug("count by criteria : {}", criteria);
        final Specification<Message> specification = createSpecification(criteria);
        return messageRepository.count(specification);
    }

    /**
     * Function to convert {@link MessageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Message> createSpecification(MessageCriteria criteria) {
        Specification<Message> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getMessageId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getMessageId(), Message_.messageId));
            }
            if (criteria.getDirection() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDirection(), Message_.direction));
            }
            if (criteria.getPayload() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPayload(), Message_.payload));
            }
            if (criteria.getCitrusMessageId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCitrusMessageId(), Message_.citrusMessageId));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Message_.createdDate));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Message_.lastModifiedDate));
            }
            if (criteria.getHeadersId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getHeadersId(),
                            root -> root.join(Message_.headers, JoinType.LEFT).get(MessageHeader_.headerId)
                        )
                    );
            }
            if (criteria.getScenarioExecutionId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getScenarioExecutionId(),
                            root -> root.join(Message_.scenarioExecution, JoinType.LEFT).get(ScenarioExecution_.executionId)
                        )
                    );
            }
        }
        return specification;
    }
}
