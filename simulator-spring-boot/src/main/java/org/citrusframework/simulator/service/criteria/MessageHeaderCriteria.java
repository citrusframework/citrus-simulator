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
import org.citrusframework.simulator.service.filter.LongFilter;
import org.citrusframework.simulator.service.filter.StringFilter;
import org.springdoc.core.annotations.ParameterObject;

import java.io.Serial;
import java.io.Serializable;

/**
 * Criteria class for the {@link org.citrusframework.simulator.model.MessageHeader} entity. This class is used
 * in {@link org.citrusframework.simulator.web.rest.MessageHeaderResource} to receive all the possible filtering options
 * from the Http GET request parameters.
 * <p>
 * For example the following could be a valid request:
 * {@code /message-headers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * <p>
 * As Spring is unable to properly convert the types, unless
 * specific {@link org.citrusframework.simulator.service.filter.Filter} class are used, we need to use fix type specific
 * filters.
 */
@Getter
@Setter
@ToString
@ParameterObject
public class MessageHeaderCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter headerId;

    private StringFilter name;

    private StringFilter value;

    private InstantFilter createdDate;

    private InstantFilter lastModifiedDate;

    private LongFilter messageId;

    private Boolean distinct;

    public MessageHeaderCriteria() {
    }

    public MessageHeaderCriteria(MessageHeaderCriteria other) {
        this.headerId = other.headerId == null ? null : other.headerId.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.value = other.value == null ? null : other.value.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
        this.messageId = other.messageId == null ? null : other.messageId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public MessageHeaderCriteria copy() {
        return new MessageHeaderCriteria(this);
    }

    public LongFilter headerId() {
        if (headerId == null) {
            headerId = new LongFilter();
        }
        return headerId;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public StringFilter value() {
        if (value == null) {
            value = new StringFilter();
        }
        return value;
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

    public LongFilter messageId() {
        if (messageId == null) {
            messageId = new LongFilter();
        }
        return messageId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MessageHeaderCriteria messageHeaderCriteria)) {
            return false;
        }

        return new EqualsBuilder()
            .append(headerId, messageHeaderCriteria.headerId)
            .append(name, messageHeaderCriteria.name)
            .append(value, messageHeaderCriteria.value)
            .append(createdDate, messageHeaderCriteria.createdDate)
            .append(lastModifiedDate, messageHeaderCriteria.lastModifiedDate)
            .append(messageId, messageHeaderCriteria.messageId)
            .append(distinct, messageHeaderCriteria.distinct)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(headerId)
            .append(name)
            .append(value)
            .append(createdDate)
            .append(lastModifiedDate)
            .append(messageId)
            .append(distinct)
            .toHashCode();
    }
}
