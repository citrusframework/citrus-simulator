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

package org.citrusframework.simulator.repository;

import org.citrusframework.simulator.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Message} entity.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {

    default Optional<Message> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default Page<Message> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select message from Message message left join fetch message.scenarioExecution",
        countQuery = "select count(message) from Message message"
    )
    Page<Message> findAllWithToOneRelationships(Pageable pageable);

    @Query("select message from Message message left join fetch message.headers left join fetch message.scenarioExecution where message.messageId = :messageId")
    Optional<Message> findOneWithToOneRelationships(@Param("messageId") Long messageId);

    @Override
    @EntityGraph(attributePaths = {"headers", "scenarioExecution", "scenarioExecution.testResult"})
    Page<Message> findAll(Specification<Message> spec, Pageable pageable);

    default List<Message> findAllForScenarioExecution(Long scenarioExecutionId, String citrusMessageId, Message.Direction direction) {
        return findAllByScenarioExecutionExecutionIdEqualsAndCitrusMessageIdEqualsIgnoreCaseAndDirectionEquals(scenarioExecutionId, citrusMessageId, direction.getId());
    }

    List<Message> findAllByScenarioExecutionExecutionIdEqualsAndCitrusMessageIdEqualsIgnoreCaseAndDirectionEquals(@Param("scenarioExecutionId") Long scenarioExecutionId, @Param("citrusMessageId") String citrusMessageId, @Param("direction") Integer direction);

    @Query("FROM Message WHERE messageId IN :messageIds")
    @EntityGraph(attributePaths = {"headers", "scenarioExecution", "scenarioExecution.testResult"})
    List<Message> findAllWhereMessageIdIn(@Param("messageIds") List<Long> messageIds, Sort sort);
}
