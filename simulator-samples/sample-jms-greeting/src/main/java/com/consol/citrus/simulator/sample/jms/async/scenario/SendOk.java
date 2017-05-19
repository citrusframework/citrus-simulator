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

import com.consol.citrus.simulator.sample.model.xml.fax.FaxStatusEnumType;
import com.consol.citrus.simulator.scenario.Scenario;

/**
 * @author Martin Maher
 */
@Scenario("SEND_OK")
public class SendOk extends BaseFaxScenario {
    @Override
    protected void configure() {
        scenario()
                .receive()
                .extractFromPayload(REFERENCE_ID_XPATH, REFERENCE_ID_VAR)
        ;

        scenario()
                .send()
                .payload(
                        payloadHelper.generateFaxStatusMessage(placeholder(REFERENCE_ID_VAR), FaxStatusEnumType.QUEUED, "The fax message has been queued and will be send shortly"),
                        payloadHelper.getMarshaller()
                )
        ;

        sleep(5000L);

        scenario()
                .send()
                .payload(
                        payloadHelper.generateFaxStatusMessage(placeholder(REFERENCE_ID_VAR), FaxStatusEnumType.SUCCESS, "The fax message has been successfully sent"),
                        payloadHelper.getMarshaller()
                )
        ;

    }
}
