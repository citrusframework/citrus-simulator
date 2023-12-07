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

package org.citrusframework.simulator.scenario;

import org.citrusframework.DefaultTestCaseRunner;
import org.citrusframework.GherkinTestActionRunner;
import org.citrusframework.TestAction;
import org.citrusframework.TestActionBuilder;
import org.citrusframework.TestBehavior;
import org.citrusframework.TestCaseRunner;
import org.citrusframework.actions.ReceiveMessageAction;
import org.citrusframework.actions.SendMessageAction;
import org.citrusframework.context.TestContext;
import org.citrusframework.simulator.correlation.CorrelationHandlerBuilder;
import org.citrusframework.simulator.http.HttpScenarioActionBuilder;
import org.citrusframework.simulator.ws.SoapScenarioActionBuilder;
import org.springframework.context.ApplicationContext;

import static org.citrusframework.container.FinallySequence.Builder.doFinally;

/**
 * @author Christoph Deppisch
 */
public class ScenarioRunner implements GherkinTestActionRunner {

    private final TestCaseRunner delegate;

    /**
     * Scenario direct endpoint
     */
    private final ScenarioEndpoint scenarioEndpoint;

    /** Spring bean application context */
    private final ApplicationContext applicationContext;

    /**
     * Default constructor using fields.
     *
     * @param scenarioEndpoint
     * @param applicationContext
     * @param context
     */
    public ScenarioRunner(ScenarioEndpoint scenarioEndpoint, ApplicationContext applicationContext, TestContext context) {
        this.scenarioEndpoint = scenarioEndpoint;
        this.applicationContext = applicationContext;

        this.delegate = new DefaultTestCaseRunner(context);
    }

    /**
     * Gets the scenario inbound endpoint.
     *
     * @return
     */
    public ScenarioEndpoint scenarioEndpoint() {
        return scenarioEndpoint;
    }

    public SendMessageAction.Builder send() {
        return SendMessageAction.Builder.send().endpoint(scenarioEndpoint);
    }

    public ReceiveMessageAction.Builder receive() {
        return ReceiveMessageAction.Builder.receive().endpoint(scenarioEndpoint);
    }

    public HttpScenarioActionBuilder http() {
        return new HttpScenarioActionBuilder(scenarioEndpoint);
    }

    public SoapScenarioActionBuilder soap() {
        return new SoapScenarioActionBuilder(scenarioEndpoint);
    }

    @Override
    public <T extends TestAction> T run(TestActionBuilder<T> builder) {
        if (builder instanceof CorrelationHandlerBuilder correlationHandlerBuilder) {
            correlationHandlerBuilder.setApplicationContext(applicationContext);
            delegate.run(doFinally().actions(((CorrelationHandlerBuilder) builder).stop()));
        }

        return delegate.run(builder);
    }

    @Override
    public <T extends TestAction> TestActionBuilder<T> applyBehavior(TestBehavior behavior) {
        return delegate.applyBehavior(behavior);
    }

    public TestCaseRunner getTestCaseRunner() {
        return delegate;
    }

    public <T> T variable(String name, T value) {
        return delegate.variable(name, value);
    }

    public void start() {
        delegate.start();
    }

    public void stop() {
        delegate.stop();
    }

    public void name(String name) {
        delegate.name(name);
    }
}
