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

package org.citrusframework.simulator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;

/**
 * Base abstract class for entities which will hold definitions for created and last modified by attributes.
 */
@Data
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
@JsonIgnoreProperties(value = {"createdDate", "lastModifiedDate"}, allowGetters = true)
public abstract class AbstractAuditingEntity<T, I> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate = Instant.now();

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate = Instant.now();

    public static abstract class AuditingEntityBuilder<B extends AuditingEntityBuilder<B, E, A>, E extends AbstractAuditingEntity<E, A>, A> {

        public B createdDate(ZonedDateTime createdDate) {
            return createdDate(createdDate.toInstant());
        }

        @SuppressWarnings("unchecked")
        public B createdDate(Instant createdDate) {
            getEntity().setCreatedDate(createdDate);
            return (B) this;
        }

        public B lastModifiedDate(ZonedDateTime lastModifiedDate) {
            return lastModifiedDate(lastModifiedDate.toInstant());
        }

        @SuppressWarnings("unchecked")
        public B lastModifiedDate(Instant lastModifiedDate) {
            getEntity().setLastModifiedDate(lastModifiedDate);
            return (B) this;
        }

        protected abstract E getEntity();

        public E build() {
            return getEntity();
        }
    }
}
