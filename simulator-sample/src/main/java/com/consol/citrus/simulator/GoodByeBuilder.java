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

package com.consol.citrus.simulator;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component("GoodBye")
@Scope("prototype")
public class GoodByeBuilder extends AbstractSimulatorBuilder {

    @Override
    protected void configure() {
        receiveSOAPRequest()
            .payload("<GoodBye xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Say GoodBye!" +
                     "</GoodBye>");

        sendSOAPResponse()
            .payload("<GoodByeResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Bye bye!" +
                     "</GoodByeResponse>");
    }
}
