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
