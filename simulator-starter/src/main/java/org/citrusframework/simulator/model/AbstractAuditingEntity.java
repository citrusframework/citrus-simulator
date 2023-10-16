package org.citrusframework.simulator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;

/**
 * Base abstract class for entities which will hold definitions for created and last modified by attributes.
 */
@MappedSuperclass
@EntityListeners({ AuditingEntityListener.class })
@JsonIgnoreProperties(value = { "createdDate", "lastModifiedDate" }, allowGetters = true)
public abstract class AbstractAuditingEntity<T, I> implements Serializable {

    private static final long serialVersionUID = 1L;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate = Instant.now();

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate = Instant.now();

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public T createdDate(Instant createdDate) {
        setCreatedDate(createdDate);
        return (T) this;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public T lastModifiedDate(Instant lastModifiedDate) {
        setLastModifiedDate(lastModifiedDate);
        return (T) this;
    }
}
