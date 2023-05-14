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
import org.citrusframework.TestActionBuilder;
import org.citrusframework.TestActionContainerBuilder;
import org.citrusframework.actions.ReceiveMessageAction;
import org.citrusframework.actions.SendMessageAction;
import org.citrusframework.container.FinallySequence;
import org.citrusframework.container.TestActionContainer;
import org.citrusframework.context.TestContext;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.http.actions.HttpServerRequestActionBuilder;
import org.citrusframework.http.actions.HttpServerResponseActionBuilder;
import org.citrusframework.simulator.correlation.CorrelationHandlerBuilder;
import org.citrusframework.simulator.correlation.CorrelationManager;
import org.citrusframework.simulator.http.HttpScenarioActionBuilder;
import org.citrusframework.simulator.ws.SoapScenarioActionBuilder;
import org.citrusframework.spi.ReferenceResolver;
import org.springframework.context.ApplicationContext;

import java.util.Stack;

import static org.citrusframework.container.FinallySequence.Builder.doFinally;

/**
 * @author Christoph Deppisch
 */
public class ScenarioDesigner extends DefaultTestCaseRunner {

    /**
     * Scenario direct endpoint
     */
    private final ScenarioEndpoint scenarioEndpoint;

    /** Spring bean application context */
    private final ApplicationContext applicationContext;
    
    /** Bean reference resolver */
    private final ReferenceResolver referenceResolver;

    // TODO: Is this still relevant?
    /** Optional stack of containers cached for execution */
    protected Stack<TestActionContainerBuilder<? extends TestActionContainer, ?>> containers = new Stack<>();

    /**
     * Default constructor using fields.
     *
     * @param scenarioEndpoint
     * @param referenceResolver
     * @param context
     */
    public ScenarioDesigner(ScenarioEndpoint scenarioEndpoint, ReferenceResolver referenceResolver, ApplicationContext applicationContext, TestContext context) {
        super(context);
        this.scenarioEndpoint = scenarioEndpoint;
        this.applicationContext = applicationContext;
        this.referenceResolver = referenceResolver;
    }

    /**
     * Start new message correlation so scenario is provided with additional inbound messages.
     *
     * @return
     */
    public CorrelationManager correlation() {
        return () -> {
            CorrelationHandlerBuilder builder = new CorrelationHandlerBuilder(scenarioEndpoint, applicationContext);
            action(builder);
            // TODO: Not sure if this works? Where does this action get called?
            // See: https://github.com/citrusframework/citrus/pull/946/files#diff-f5bc91f18a5e506ee478ab30a349ef1aee9eecf68efbb8954c5978a7eb14029eL749
            doFinally().actions(builder.stop());
            return builder;
        };
    }


    /**
     * Receive message from scenario endpoint.
     *
     * @return
     */
    public ReceiveMessageAction.ReceiveMessageActionBuilder<?, ?, ?> receive() {
        return new HttpServerRequestActionBuilder()
                .endpoint(scenarioEndpoint)
                .description("Receive scenario request");
    }

    /**
     * Send message from scenario endpoint.
     *
     * @return
     */
    public SendMessageAction.SendMessageActionBuilder<?, ?, ?> send() {
        return new HttpServerResponseActionBuilder()
                .endpoint(scenarioEndpoint)
                .description("Send scenario response");
    }

    public HttpScenarioActionBuilder http() {
        HttpScenarioActionBuilder builder = new HttpScenarioActionBuilder(scenarioEndpoint).withReferenceResolver(referenceResolver);
        action(builder);
        return builder;
    }

    public SoapScenarioActionBuilder soap() {
        SoapScenarioActionBuilder builder = new SoapScenarioActionBuilder(scenarioEndpoint).withReferenceResolver(referenceResolver);
        action(builder);
        return builder;
    }

    /**
     * Gets the scenario inbound endpoint.
     *
     * @return
     */
    public ScenarioEndpoint scenarioEndpoint() {
        return scenarioEndpoint;
    }

    private void action(TestActionBuilder<?> builder) {
        if (builder instanceof TestActionContainerBuilder<?, ?>) {
            if (containers.lastElement().equals(builder)) {
                containers.pop();
            } else {
                throw new CitrusRuntimeException("Invalid use of action containers - the container execution is not expected!");
            }

            if (builder instanceof FinallySequence.Builder) {
                ((FinallySequence.Builder) builder).getActions().forEach(getTestCase()::addFinalAction);
                return;
            }
        }

        if (containers.isEmpty()) {
            getTestCase().addTestAction(builder.build());
        } else {
            containers.lastElement().getActions().add(builder);
        }
    }
}
