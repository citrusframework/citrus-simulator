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

import org.citrusframework.simulator.service.filter.InstantFilter;
import org.citrusframework.simulator.service.filter.IntegerFilter;
import org.citrusframework.simulator.service.filter.LongFilter;
import org.citrusframework.simulator.service.filter.StringFilter;
import org.springdoc.core.annotations.ParameterObject;

import java.io.Serializable;
import java.util.Objects;

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
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MessageCriteria implements Serializable, Criteria {

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

    public LongFilter getMessageId() {
        return messageId;
    }

    public LongFilter messageId() {
        if (messageId == null) {
            messageId = new LongFilter();
        }
        return messageId;
    }

    public void setMessageId(LongFilter messageId) {
        this.messageId = messageId;
    }

    public IntegerFilter getDirection() {
        return direction;
    }

    public IntegerFilter direction() {
        if (direction == null) {
            direction = new IntegerFilter();
        }
        return direction;
    }

    public void setDirection(IntegerFilter direction) {
        this.direction = direction;
    }

    public StringFilter getPayload() {
        return payload;
    }

    public StringFilter payload() {
        if (payload == null) {
            payload = new StringFilter();
        }
        return payload;
    }

    public void setPayload(StringFilter payload) {
        this.payload = payload;
    }

    public StringFilter getCitrusMessageId() {
        return citrusMessageId;
    }

    public StringFilter citrusMessageId() {
        if (citrusMessageId == null) {
            citrusMessageId = new StringFilter();
        }
        return citrusMessageId;
    }

    public void setCitrusMessageId(StringFilter citrusMessageId) {
        this.citrusMessageId = citrusMessageId;
    }

    public LongFilter getHeadersId() {
        return headersId;
    }

    public LongFilter headersId() {
        if (headersId == null) {
            headersId = new LongFilter();
        }
        return headersId;
    }

    public void setHeadersId(LongFilter headersId) {
        this.headersId = headersId;
    }

    public LongFilter getScenarioExecutionId() {
        return scenarioExecutionId;
    }

    public LongFilter scenarioExecutionId() {
        if (scenarioExecutionId == null) {
            scenarioExecutionId = new LongFilter();
        }
        return scenarioExecutionId;
    }

    public void setScenarioExecutionId(LongFilter scenarioExecutionId) {
        this.scenarioExecutionId = scenarioExecutionId;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            createdDate = new InstantFilter();
        }
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public InstantFilter getLastModifiedDate() {
        return lastModifiedDate;
    }

    public InstantFilter lastModifiedDate() {
        if (lastModifiedDate == null) {
            lastModifiedDate = new InstantFilter();
        }
        return lastModifiedDate;
    }

    public void setLastModifiedDate(InstantFilter lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MessageCriteria that = (MessageCriteria) o;
        return (
            Objects.equals(messageId, that.messageId) &&
                Objects.equals(direction, that.direction) &&
                Objects.equals(payload, that.payload) &&
                Objects.equals(citrusMessageId, that.citrusMessageId) &&
                Objects.equals(headersId, that.headersId) &&
                Objects.equals(scenarioExecutionId, that.scenarioExecutionId) &&
                Objects.equals(createdDate, that.createdDate) &&
                Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
                Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, direction, payload, citrusMessageId, headersId, scenarioExecutionId, createdDate, lastModifiedDate, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MessageCriteria{" +
            (messageId != null ? "messageId=" + messageId + ", " : "") +
            (direction != null ? "direction=" + direction + ", " : "") +
            (payload != null ? "payload=" + payload + ", " : "") +
            (citrusMessageId != null ? "citrusMessageId=" + citrusMessageId + ", " : "") +
            (headersId != null ? "headersId=" + headersId + ", " : "") +
            (scenarioExecutionId != null ? "scenarioExecutionId=" + scenarioExecutionId + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
