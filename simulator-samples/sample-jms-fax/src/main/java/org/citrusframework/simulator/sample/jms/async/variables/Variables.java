/*
 * Copyright 2006-2017 the original author or authors.
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

package org.citrusframework.simulator.sample.jms.async.variables;

/**
 * @author Martin Maher
 */
public class Variables {

    private Variables() {
    }

    public static final String ROOT_ELEMENT_XPATH = "string:local-name(/*)";
    public static final String REFERENCE_ID_XPATH = "//fax:referenceId";
    public static final String REFERENCE_ID_VAR = "referenceId";
    public static final String REFERENCE_ID_PH = placeholder(REFERENCE_ID_VAR);
    public static final String STATUS_VAR = "status";
    public static final String STATUS_MESSAGE_VAR = "statusMessage";

    /**
     * Generates the placeholder for the supplied {@code variableName}.
     * <br>E.g. For variable 'id' the placeholder '${id}' is returned
     *
     * @param variableName the variable to generate the placeholder for
     * @return the placeholder equivalent
     */
    protected static String placeholder(String variableName) {
        return String.format("${%s}", variableName);
    }
}
