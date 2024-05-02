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

package org.citrusframework.simulator.service.criteria;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.citrusframework.simulator.service.filter.InstantFilter;
import org.citrusframework.simulator.service.filter.IntegerFilter;
import org.citrusframework.simulator.service.filter.LongFilter;
import org.citrusframework.simulator.service.filter.StringFilter;
import org.springdoc.core.annotations.ParameterObject;

import java.io.Serial;
import java.io.Serializable;

/**
 * Criteria class for the {@link org.citrusframework.simulator.model.Message} entity. This class is used
 * in {@link org.citrusframework.simulator.web.rest.MessageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * <p>
 * For example the following could be a valid request:
 * {@code /messages?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * <p>
 * As Spring is unable to properly convert the types, unless
 * specific {@link org.citrusframework.simulator.service.filter.Filter} class are used, we need to use fix type specific
 * filters.
 */
@Getter
@Setter
@ToString
@ParameterObject
public class MessageCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter messageId;

    private IntegerFilter direction;

    private StringFilter payload;

    private StringFilter citrusMessageId;

    private LongFilter headersId;

    private LongFilter scenarioExecutionId;

    private InstantFilter createdDate;

    private InstantFilter lastModifiedDate;

    private Boolean distinct;

    public MessageCriteria() {
    }

    public MessageCriteria(MessageCriteria other) {
        this.messageId = other.messageId == null ? null : other.messageId.copy();
        this.direction = other.direction == null ? null : other.direction.copy();
        this.payload = other.payload == null ? null : other.payload.copy();
        this.citrusMessageId = other.citrusMessageId == null ? null : other.citrusMessageId.copy();
        this.headersId = other.headersId == null ? null : other.headersId.copy();
        this.scenarioExecutionId = other.scenarioExecutionId == null ? null : other.scenarioExecutionId.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
        this.distinct = other.distinct;
    }

    @Override
    public MessageCriteria copy() {
        return new MessageCriteria(this);
    }

    public LongFilter messageId() {
        if (messageId == null) {
            messageId = new LongFilter();
        }
        return messageId;
    }

    public IntegerFilter direction() {
        if (direction == null) {
            direction = new IntegerFilter();
        }
        return direction;
    }

    public StringFilter payload() {
        if (payload == null) {
            payload = new StringFilter();
        }
        return payload;
    }

    public StringFilter citrusMessageId() {
        if (citrusMessageId == null) {
            citrusMessageId = new StringFilter();
        }
        return citrusMessageId;
    }

    public LongFilter headersId() {
        if (headersId == null) {
            headersId = new LongFilter();
        }
        return headersId;
    }

    public LongFilter scenarioExecutionId() {
        if (scenarioExecutionId == null) {
            scenarioExecutionId = new LongFilter();
        }
        return scenarioExecutionId;
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            createdDate = new InstantFilter();
        }
        return createdDate;
    }

    public InstantFilter lastModifiedDate() {
        if (lastModifiedDate == null) {
            lastModifiedDate = new InstantFilter();
        }
        return lastModifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MessageCriteria messageCriteria)) {
            return false;
        }

        return new EqualsBuilder()
            .append(messageId, messageCriteria.messageId)
            .append(direction, messageCriteria.messageId)
            .append(payload, messageCriteria.payload)
            .append(citrusMessageId, messageCriteria.citrusMessageId)
            .append(headersId, messageCriteria.headersId)
            .append(scenarioExecutionId, messageCriteria.scenarioExecutionId)
            .append(createdDate, messageCriteria.createdDate)
            .append(lastModifiedDate, messageCriteria.lastModifiedDate)
            .append(distinct, messageCriteria.distinct)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(messageId)
            .append(direction)
            .append(payload)
            .append(citrusMessageId)
            .append(headersId)
            .append(scenarioExecutionId)
            .append(createdDate)
            .append(lastModifiedDate)
            .append(distinct)
            .toHashCode();
    }
}
