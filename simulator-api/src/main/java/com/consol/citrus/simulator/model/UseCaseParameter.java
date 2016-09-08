/*
 * Copyright 2006-2014 the original author or authors.
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

package com.consol.citrus.simulator.model;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author Christoph Deppisch
 */
public class UseCaseParameter {

    private final String id;
    private final String displayName;
    private final String value;

    private List<String> options;

    private String fieldType;
    private String useCaseFilter;

    /**
     * Constructor using form field id and value.
     * @param id
     * @param value
     */
    public UseCaseParameter(String id, String value) {
        this(id, id, value);
    }

    /**
     * Constructor using form field id, displayName and value.
     * @param id
     * @param displayName
     * @param value
     */
    public UseCaseParameter(String id, String displayName, String value) {
        this.id = id;
        this.displayName = displayName;
        this.value = value;
    }

    /**
     * Constructor using additional field options.
     * @param id
     * @param displayName
     * @param value
     * @param options
     */
    public UseCaseParameter(String id, String displayName, String value, List<String> options) {
        this(id, displayName, value);
        this.options = options;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getUseCaseFilter() {
        return useCaseFilter;
    }

    /**
     * Adds use case filter which resides in conditional display of parameter field in Html form.
     * @param triggerType
     * @return
     */
    public UseCaseParameter addUseCaseFilter(Class<? extends UseCaseTrigger> triggerType) {
        if (StringUtils.hasText(useCaseFilter)) {
            this.useCaseFilter += " " + triggerType.getAnnotation(Component.class).value();
        } else {
            this.useCaseFilter = triggerType.getAnnotation(Component.class).value();
        }

        return this;
    }

    public String getFieldType() {
        return fieldType;
    }

    /**
     * Adds field type which resides in additional input field css style class.
     * @param fieldType
     * @return
     */
    public UseCaseParameter addFieldType(FieldType fieldType) {
        if (StringUtils.hasText(this.fieldType)) {
            this.fieldType += " " + fieldType.getCssClass();
        } else {
            this.fieldType = fieldType.getCssClass();
        }

        return this;
    }

    /**
     * Field type representation used for css input field class and special field rendering such as
     * date picker.
     */
    public static enum FieldType {
        DATE_FIELD("datefield");

        /** Css style class to add to input field */
        private String cssClass;

        /**
         * Default constructor using css style class.
         * @param cssClass
         */
        FieldType(String cssClass) {
            this.cssClass = cssClass;
        }

        /**
         * Gets the css style class.
         * @return
         */
        public String getCssClass() {
            return cssClass;
        }
    }
}
