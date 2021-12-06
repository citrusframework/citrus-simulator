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

import org.citrusframework.simulator.scenario.*;

@Scenario("Hello")
public class HelloScenario extends AbstractSimulatorScenario {

    @Override
    public void run(ScenarioDesigner scenario) {
        scenario.echo("Simulator: ${simulator.name}");

        scenario
            .receive()
            .payload("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                        "<from>user@citrusframework.org</from>" +
                        "<to>citrus@citrusframework.org</to>" +
                        "<cc></cc>" +
                        "<bcc></bcc>" +
                        "<subject>Hello</subject>" +
                        "<body>" +
                            "<contentType>text/plain; charset=utf-8</contentType>" +
                            "<content>Say Hello!</content>" +
                        "</body>" +
                    "</mail-message>");

        scenario
            .send()
            .payload("<mail-response xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                        "<code>250</code>" +
                        "<message>OK</message>" +
                    "</mail-response>");
    }
}
