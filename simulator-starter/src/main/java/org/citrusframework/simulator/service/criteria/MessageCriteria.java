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

    public LongFilter id() {
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
                Objects.equals(createdDate, that.createdDate) &&
                Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
                Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, direction, payload, citrusMessageId, createdDate, lastModifiedDate, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MessageCriteria{" +
            (messageId != null ? "id=" + messageId + ", " : "") +
            (direction != null ? "direction=" + direction + ", " : "") +
            (payload != null ? "payload=" + payload + ", " : "") +
            (citrusMessageId != null ? "citrusMessageId=" + citrusMessageId + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
