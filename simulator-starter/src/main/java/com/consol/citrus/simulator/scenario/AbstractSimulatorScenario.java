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

package com.consol.citrus.simulator.scenario;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.Message;
import com.consol.citrus.simulator.correlation.CorrelationHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractSimulatorScenario implements SimulatorScenario, CorrelationHandler, ApplicationContextAware {

    /** Spring bean application context */
    private ApplicationContext applicationContext;

    /** Scenario endpoint */
    private ScenarioEndpoint scenarioEndpoint = new ScenarioEndpoint(new ScenarioEndpointConfiguration());

    @Override
    public boolean isHandlerFor(Message message, TestContext context) {
        return false;
    }

    /**
     * Sets the applicationContext.
     *
     * @param applicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Gets the applicationContext.
     *
     * @return
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Gets the scenarioEndpoint.
     *
     * @return
     */
    @Override
    public ScenarioEndpoint getScenarioEndpoint() {
        return scenarioEndpoint;
    }
}
