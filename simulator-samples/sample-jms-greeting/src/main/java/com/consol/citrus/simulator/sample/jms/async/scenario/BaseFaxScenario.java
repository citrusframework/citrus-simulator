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

package com.consol.citrus.simulator.sample.jms.async.scenario;

import com.consol.citrus.simulator.jms.SimulatorJmsAsyncScenario;
import com.consol.citrus.simulator.sample.model.xml.fax.PayloadHelper;

/**
 * @author Martin Maher
 */
public class BaseFaxScenario extends SimulatorJmsAsyncScenario {

    public static final String REFERENCE_ID_XPATH = "//fax:referenceId";
    public static final String REFERENCE_ID_VAR = "referenceId";
    public static final String REFERENCE_ID_PH = placeholder(REFERENCE_ID_VAR);

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

    protected PayloadHelper payloadHelper = new PayloadHelper();


}
