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

package org.citrusframework.simulator.sample.jms.async.scenario;

import org.citrusframework.simulator.sample.jms.async.model.FaxStatusEnumType;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioDesigner;
import org.citrusframework.simulator.sample.jms.async.variables.Variables;

/**
 * @author Martin Maher
 */
@Scenario("FaxBusy")
public class FaxBusyScenario extends AbstractFaxScenario {

    @Override
    public void run(ScenarioDesigner scenario) {
        scenario
            .receive()
            .xpath(Variables.ROOT_ELEMENT_XPATH, "SendFaxMessage")
            .extractFromPayload(Variables.REFERENCE_ID_XPATH, Variables.REFERENCE_ID_VAR);

        scenario
            .send(getStatusEndpoint())
            .payload(
                    getPayloadHelper().generateFaxStatusMessage(Variables.REFERENCE_ID_PH,
                            FaxStatusEnumType.QUEUED,
                            "The fax message has been queued and will be send shortly"),
                    getPayloadHelper().getMarshaller()
            );

        scenario.sleep(2000L);

        scenario
            .send(getStatusEndpoint())
            .payload(
                    getPayloadHelper().generateFaxStatusMessage(Variables.REFERENCE_ID_PH,
                            FaxStatusEnumType.ERROR,
                            "Error transmitting fax: The receiving fax was busy"),
                    getPayloadHelper().getMarshaller()
            );
    }
}
