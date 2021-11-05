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

package ${package}.scenario;

import com.consol.citrus.simulator.scenario.*;

@Scenario("GoodBye")
public class GoodByeScenario extends AbstractSimulatorScenario {

    @Override
    public void run(ScenarioDesigner scenario) {
        scenario
            .receive()
            .payload("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Say GoodBye!" +
                     "</GoodBye>");

        scenario
            .send()
            .payload("<GoodByeResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Bye bye!" +
                     "</GoodByeResponse>");
    }
}
