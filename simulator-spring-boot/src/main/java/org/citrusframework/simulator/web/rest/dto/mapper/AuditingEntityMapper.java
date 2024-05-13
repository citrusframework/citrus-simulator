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

package org.citrusframework.simulator.web.rest.dto.mapper;

import org.citrusframework.simulator.model.AbstractAuditingEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;

import java.util.stream.Stream;

public interface AuditingEntityMapper {

    @AfterMapping
    default <T> void completeAuditingInformation(AbstractAuditingEntity<T, ?> abstractAuditingEntity, @TargetType Class<T> targetType, @MappingTarget T result) throws NoSuchFieldException, IllegalAccessException {
        if (Stream.of(targetType.getDeclaredFields()).anyMatch(f -> f.getName().equals("createdDate"))) {
            targetType.getDeclaredField("createdDate").set(result, abstractAuditingEntity.getCreatedDate());
        }
        if (Stream.of(targetType.getDeclaredFields()).anyMatch(f -> f.getName().equals("lastModifiedDate"))) {
            targetType.getDeclaredField("lastModifiedDate").set(result, abstractAuditingEntity.getLastModifiedDate());
        }
    }
}
