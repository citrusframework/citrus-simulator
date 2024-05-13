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

import org.citrusframework.message.builder.MarshallingPayloadBuilder;
import org.citrusframework.simulator.sample.jms.async.model.FaxStatusEnumType;
import org.citrusframework.simulator.sample.jms.async.variables.Variables;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioRunner;

import static org.citrusframework.actions.SendMessageAction.Builder.send;
import static org.citrusframework.actions.SleepAction.Builder.sleep;
import static org.citrusframework.dsl.MessageSupport.MessageBodySupport.fromBody;
import static org.citrusframework.dsl.XpathSupport.xpath;

/**
 * @author Martin Maher
 */
@Scenario("FaxNoAnswer")
public class FaxNoAnswerScenario extends AbstractFaxScenario {

    @Override
    public void run(ScenarioRunner scenario) {
        scenario.$(scenario.receive()
                .message()
                .validate(xpath().expression(Variables.ROOT_ELEMENT_XPATH, "SendFaxMessage"))
                .extract(fromBody().expression(Variables.REFERENCE_ID_XPATH, Variables.REFERENCE_ID_VAR)));

        scenario.$(send()
                .endpoint(getStatusEndpoint())
                .message()
                .body(new MarshallingPayloadBuilder(
                    getPayloadHelper().generateFaxStatusMessage(Variables.REFERENCE_ID_PH,
                            FaxStatusEnumType.QUEUED,
                            "The fax message has been queued and will be send shortly"),
                    getPayloadHelper().getMarshaller())
            ));

        scenario.$(sleep().milliseconds(2000L));

        scenario.$(send()
                .endpoint(getStatusEndpoint())
                .message()
                .body(new MarshallingPayloadBuilder(
                    getPayloadHelper().generateFaxStatusMessage(Variables.REFERENCE_ID_PH,
                            FaxStatusEnumType.ERROR,
                            "Error transmitting fax: No answer from the receiving fax"),
                    getPayloadHelper().getMarshaller())
            ));
    }
}
