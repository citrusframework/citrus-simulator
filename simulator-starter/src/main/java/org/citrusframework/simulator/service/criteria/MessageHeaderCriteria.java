package org.citrusframework.simulator.service.criteria;

import org.citrusframework.simulator.service.filter.LongFilter;
import org.citrusframework.simulator.service.filter.StringFilter;
import org.springdoc.core.annotations.ParameterObject;

import java.io.Serializable;
import java.util.Objects;

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
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MessageHeaderCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter headerId;

    private StringFilter name;

    private StringFilter value;

    private LongFilter messageId;

    private Boolean distinct;

    public MessageHeaderCriteria() {}

    public MessageHeaderCriteria(MessageHeaderCriteria other) {
        this.headerId = other.headerId == null ? null : other.headerId.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.value = other.value == null ? null : other.value.copy();
        this.messageId = other.messageId == null ? null : other.messageId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public MessageHeaderCriteria copy() {
        return new MessageHeaderCriteria(this);
    }

    public LongFilter getHeaderId() {
        return headerId;
    }

    public LongFilter headerId() {
        if (headerId == null) {
            headerId = new LongFilter();
        }
        return headerId;
    }

    public void setHeaderId(LongFilter headerId) {
        this.headerId = headerId;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getValue() {
        return value;
    }

    public StringFilter value() {
        if (value == null) {
            value = new StringFilter();
        }
        return value;
    }

    public void setValue(StringFilter value) {
        this.value = value;
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
        final MessageHeaderCriteria that = (MessageHeaderCriteria) o;
        return (
            Objects.equals(headerId, that.headerId) &&
            Objects.equals(name, that.name) &&
            Objects.equals(value, that.value) &&
            Objects.equals(messageId, that.messageId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(headerId, name, value, messageId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MessageHeaderCriteria{" +
            (headerId != null ? "id=" + headerId + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (value != null ? "value=" + value + ", " : "") +
            (messageId != null ? "messageId=" + messageId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
