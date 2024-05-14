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

package org.citrusframework.simulator.sample.jms.async.scenario;

import org.citrusframework.jms.endpoint.JmsEndpoint;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Christoph Deppisch
 */
public class AbstractFaxScenario extends AbstractSimulatorScenario {

    /** Fax payload helper */
    private final PayloadHelper payloadHelper = new PayloadHelper();

    @Autowired
    @Qualifier("simulatorJmsStatusEndpoint")
    private JmsEndpoint statusEndpoint;

    /**
     * Gets the payloadHelper.
     *
     * @return
     */
    public PayloadHelper getPayloadHelper() {
        return payloadHelper;
    }

    /**
     * Gets the statusEndpoint.
     *
     * @return
     */
    public JmsEndpoint getStatusEndpoint() {
        return statusEndpoint;
    }
}
