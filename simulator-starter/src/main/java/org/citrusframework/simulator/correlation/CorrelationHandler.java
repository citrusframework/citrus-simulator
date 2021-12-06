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

package org.citrusframework.simulator.correlation;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.Message;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;

/**
 * @author Christoph Deppisch
 */
public interface CorrelationHandler {

    /**
     * Evaluates if handler is capable of handling given message
     * @param message
     * @param context
     * @return
     */
    default boolean isHandlerFor(Message message, TestContext context) {
        return false;
    }

    /**
     * Gets the scenario endpoint that this handler is handling messages for.
     * @return
     */
    ScenarioEndpoint getScenarioEndpoint();
}
