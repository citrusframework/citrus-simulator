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

package org.citrusframework.simulator.model;

import jakarta.persistence.Column;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * EntityUtils - A utility class for handling data model operations.
 *
 * This class provides utility methods for working with data models and entities.
 *
 * @author Thorsten Schlathoelter
 */
public class EntityUtils {

    private static final Logger logger = LoggerFactory.getLogger(EntityUtils.class);

    private EntityUtils() {
        // only static access
    }

    /**
     * Truncate a string value to fit the column size specified for an entity property.
     *
     * This method is used to truncate a string value to fit the maximum column size
     * specified in the corresponding entity class for a specific property. If the provided
     * value is longer than the specified column size, it will be silently truncated to match
     * the column size.
     *
     * The column size is determined using reflection. If reflection fails due to the entityProperty not
     * matching a field, an exception is thrown to indicate a misconfiguration. If it fails due to a
     * security manager preventing reflection, truncation will be silently ignored. This is only harmful
     * if a value requires truncation, which will be detected by the persistence layer.
     *
     * @param entityClass The class of the entity containing the property definition.
     * @param entityProperty The name of the property for which the column size is defined.
     * @param value The value to be truncated.
     * @return The truncated value.
     */
    public static String truncateToColumnSize(Class<?> entityClass, String entityProperty, String value) {
        if (StringUtils.hasLength(value)) {
            try {
                int size = entityClass.getDeclaredField(entityProperty)
                    .getAnnotation(Column.class)
                    .length();
                int inLength = value.length();
                if (inLength > size) {
                    return value.substring(0, size);
                }
            } catch (NoSuchFieldException e) {
                throw new CitrusRuntimeException(String.format("entityProperty '%s' unknown for class '%s'", entityProperty, entityClass.getName()));
            } catch (SecurityException e) {
                logger.warn("Unable to perform truncation for entity class '{}' and field '{}'.", entityClass, entityProperty, e);
            }
        }

        return value;
    }

}
